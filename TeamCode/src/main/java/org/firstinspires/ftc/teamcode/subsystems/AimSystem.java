package org.firstinspires.ftc.teamcode.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;

@Configurable
public class AimSystem {

    //EXPORTS
    public boolean CanSeeHive;
    public float AngleToShoot;
    public float DistanceToShoot;
    public float ReqRPM;

    // ---- Tunables (Panels can edit these live) ----
    public static double encoderRangeDeg = 180;  // servo-shaft degrees covered by one full 0-3.3V sweep (Axon wraps every 180)
    public static double gearRatio = 2.5;        // servo SHAFT rotations per 1 turret rotation (5 encoder wraps per turret turn)
    public static double startAngle = 0;         // turret angle (deg) when the robot is initialised
    public static double encoderDirection = 1;   // set to -1 if the angle counts down when the turret turns "positive"
    public static double minAngle = 0;
    public static double maxAngle = 350;
    public static double KP = 0.025;

    // Flip tuning
    public static double limitMargin = 2;        // deg from a limit that counts as "at the limit"
    public static double flipTxThreshold = 5;    // deg of tx pushing into a limit before we flip
    public static double flipKP = 0.01;
    public static double flipMaxPower = 0.8;
    public static double flipMinPower = 0.08;    // so the CR servo still moves near the end of the sweep
    public static double flipTolerance = 5;      // deg
    public static double flipTimeoutMs = 4000;
    public static double lostFlipWindowMs = 500; // only flip on a lost target if we saw it this recently
    public static double manualOverride = 0.2;   // stick deflection that cancels a flip

    private static final int RedPipeline = 1;
    private static final int BluePipeline = 0;

    public double manualInfluence = 0;

    CRServo Swivel;
    AnalogInput SwivelEncoder;
    Limelight3A LimeLight;

    // Encoder unwrapping state
    private double lastRawServoDeg;
    private double unwrappedServoDeg;
    private double servoDegAtStart;

    // Flip state
    private boolean flipping = false;
    private double flipTarget = 0;
    private long flipStartNano = 0;
    private double lastTx = 0;
    private long lastSeenNano = 0;

    public AimSystem(CRServo Swivel, AnalogInput SwivelEncoder, Limelight3A LimeLight, Boolean IsRedAlliance){
        this.Swivel = Swivel;
        this.SwivelEncoder = SwivelEncoder;
        this.LimeLight = LimeLight;

        this.CanSeeHive = false;
        this.ReqRPM = 1750;

        lastRawServoDeg = readRawServoDeg();
        unwrappedServoDeg = lastRawServoDeg;
        servoDegAtStart = lastRawServoDeg;

        LimeLight.start();
        setAlliancePipeline(IsRedAlliance);
    }

    public void setAlliancePipeline(boolean isRedAlliance) {
        int targetPipeline = isRedAlliance ? RedPipeline : BluePipeline;
        LimeLight.pipelineSwitch(targetPipeline);
    }




    // ignores wrap
    private double readRawServoDeg() {
        return (SwivelEncoder.getVoltage() / SwivelEncoder.getMaxVoltage()) * encoderRangeDeg;
    }

    // ignores wrap
    public void updateEncoder() {
        double raw = readRawServoDeg();
        double delta = raw - lastRawServoDeg;
        if (delta > encoderRangeDeg / 2) delta -= encoderRangeDeg;          // wrapped top -> bottom going backwards
        else if (delta < -encoderRangeDeg / 2) delta += encoderRangeDeg;    // wrapped bottom -> top going forwards
        unwrappedServoDeg += delta;
        lastRawServoDeg = raw;
    }

    // ignores wrapping
    public double getTurretCurrentDegrees() {
        return startAngle + encoderDirection * (unwrappedServoDeg - servoDegAtStart) / gearRatio;
    }

    public double getError(){
        LLResult result = LimeLight.getLatestResult();

        if (result != null && result.isValid()) {
            return result.getTx();
        }
        else{return -1;}
    }

    public boolean isFlipping() { return flipping; }
    public double getFlipTarget() { return flipTarget; }

    public void update() {
        updateEncoder();
        double current = getTurretCurrentDegrees();

        LLResult result = LimeLight.getLatestResult();
        boolean valid = result != null && result.isValid();

        if (valid) {
            CanSeeHive = true;
            DistanceToShoot = (float) result.getTa(); //How big is it??? basically distance
        } else {
            CanSeeHive = false;
        }

        // Mid-flip: drive to the target angle, ignore the camera, hold the last RPM
        if (flipping) {
            // Operator can cancel a flip by moving the stick
            if (Math.abs(manualInfluence) > manualOverride) {
                flipping = false;
            } else {
                runFlip(current);
                return;
            }
        }

        double swivelPower = 0;

        if (valid) {
            double tx = result.getTx(); // Horizontal offset from target
            lastTx = tx;
            lastSeenNano = System.nanoTime();

            swivelPower = tx * KP;
            ReqRPM = CalcRPM(DistanceToShoot);

            // Pinned at a limit and the target is still beyond it -> flip around
            if (tryStartFlip(current, tx)) {
                runFlip(current);
                return;
            }
        } else {
            //ReqRPM = 0;
            //AngleToShoot = 0;

            // Target vanished while we were pushing into a limit (it went out of the camera's view
            // because we ran out of travel). Flip using the last tx we saw.
            double sinceSeenMs = (System.nanoTime() - lastSeenNano) / 1e6;
            if (lastSeenNano != 0 && sinceSeenMs < lostFlipWindowMs && tryStartFlip(current, lastTx)) {
                runFlip(current);
                return;
            }
        }

        swivelPower += manualInfluence;
        swivelPower = clamp(swivelPower, -1, 1);

        // Enforce Software Limits
        if (current >= maxAngle && swivelPower > 0) {
            swivelPower = 0.0;
        } else if (current <= minAngle && swivelPower < 0) {
            swivelPower = 0.0;
        }
        TurnSwivel(swivelPower);
    }

    //flips when hit limit
    private boolean tryStartFlip(double current, double tx) {
        double target;

        if (current >= maxAngle - limitMargin && tx > flipTxThreshold) {
            target = current + tx - 360.0;          // same bearing, other side of the dead zone
        } else if (current <= minAngle + limitMargin && tx < -flipTxThreshold) {
            target = current + tx + 360.0;
        } else {
            return false;
        }

        // Target sits in the dead zone, can't reach it
        if (target < minAngle || target > maxAngle) return false;

        flipTarget = target;
        flipStartNano = System.nanoTime();
        flipping = true;
        return true;
    }


    private void runFlip(double current) {
        double error = flipTarget - current;
        double elapsedMs = (System.nanoTime() - flipStartNano) / 1e6;

        if (Math.abs(error) <= flipTolerance || elapsedMs > flipTimeoutMs) {
            flipping = false;
            TurnSwivel(0.0);
            return;
        }

        double power = clamp(error * flipKP, -flipMaxPower, flipMaxPower);
        if (Math.abs(power) < flipMinPower) power = Math.copySign(flipMinPower, error);

        // Never drive past a software limit
        if ((current >= maxAngle && power > 0) || (current <= minAngle && power < 0)) {
            flipping = false;
            TurnSwivel(0.0);
            return;
        }

        TurnSwivel(power);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    public void TurnSwivel(double Power){
        Swivel.setPower(Power);
    }

    public float CalcRPM(float distance){
        return 1700;   // TODO - replace w/ Trey's math
    }

    public void stop() {Swivel.setPower(0.0);}
}