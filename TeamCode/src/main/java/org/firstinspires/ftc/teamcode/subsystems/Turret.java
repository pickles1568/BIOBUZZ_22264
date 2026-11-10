package org.firstinspires.ftc.teamcode.subsystems;

import static com.pedropathing.ivy.commands.Commands.conditional;
import static com.pedropathing.ivy.commands.Commands.instant;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.helpers.Button;

public class Turret {

    final double encoderResolutionTPR = 28.0;
    final double MaxRPM = 6000;

    public DcMotorEx Flywheel;

    public CRServo kicker;

    double lastEncoderPosition;
    long lastTimeNano;

    public double highVel = 1000;
    public double lowVel = 100;

    // Made public so the tuner OpMode can display them in telemetry
    public double curTargetVel = highVel;
    public double F = 10.0;
    public double P = 10.0;

    public double[] stepSizes = {10.0, 1.0, 0.1, 0.001, 0.0001};
    public int stepIndex = 1;

    public Turret(DcMotorEx Flywheel, CRServo kicker) {
        this.Flywheel = Flywheel;
        this.kicker = kicker;
        lastEncoderPosition = Flywheel.getCurrentPosition();
        Flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        lastTimeNano = System.nanoTime();

        PIDFCoefficients pidfcoefficients = new PIDFCoefficients(P, 0.0, 0.0, F);
        Flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfcoefficients);
    }

    public void spinUp(){
        if(curTargetVel == highVel){
            curTargetVel = lowVel;
        } else {
            curTargetVel = highVel;
        }
    }

    public void incrementStep(){
        stepIndex = (stepIndex + 1) % stepSizes.length;
    }

    public void tuneF(int mult){
        F += stepSizes[stepIndex] * mult;
    }

    public void tuneP(int mult){
        P += stepSizes[stepIndex] * mult;
    }

    public void tuneHigh(int mult){highVel += stepSizes[stepIndex] * mult;}

    public void updateTune(){
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0.0, 0.0, F);
        Flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
    }

    public void updateVel(){
        Flywheel.setVelocity(curTargetVel);
    }

    public double[] getTelemData() {
        double curVel = Flywheel.getVelocity();
        double error = curTargetVel - curVel;

        return new double[]{curVel, error};
    }

    public double getRPM() {
        long now = System.nanoTime();
        double dt = (now - lastTimeNano) / 1e9;
        if (dt <= 0) return 0;

        double current = Flywheel.getCurrentPosition();
        double delta = current - lastEncoderPosition;

        lastEncoderPosition = current;
        lastTimeNano = now;

        return (delta / encoderResolutionTPR) / dt * 60.0;
    }

    public void STOP() {
        Flywheel.setPower(0);
    }

    public double getEncoder() {
        return Flywheel.getCurrentPosition();
    }

    double CurVel;
    double Error;
    double tolerence = 50;

    public Command DoFlywheel = Command.build()
            .setStart(() -> {
                curTargetVel = highVel;
                updateVel();
            })
            .setExecute(() -> {
                CurVel = Flywheel.getVelocity();
                Error = curTargetVel - CurVel;
                updateVel();
            })
            .setDone(() -> {
                return Error <= tolerence;
            })
            .requiring(Flywheel);

    public Command RunFlywheel = Command.build()
            .setStart(() -> {
                curTargetVel = highVel;
            })
            .setExecute(() -> {
                updateVel();
            })
            .setDone(() -> false) // Runs continuously until interrupted/stopped
            .requiring(Flywheel);

    public Command StopFlywheel = Command.build()
            .setStart(() -> {
                curTargetVel = 0;
                Flywheel.setVelocity(0);
                Flywheel.setPower(0);
            })
            .setDone(() -> true)
            .requiring(Flywheel);

    public Command ToggleFlywheel = conditional(
            () -> DoFlywheel.isScheduled(),
            StopFlywheel,
            DoFlywheel
    );

    public Command StartKicking = instant(() -> {kicker.setPower(1);}).requiring(kicker);
    public Command StopKicking = instant(() -> {kicker.setPower(0);}).requiring(kicker);


    private final Button.ButtonSimple btnDpadUp = new Button.ButtonSimple();
    private final Button.ButtonSimple btnDpadDown = new Button.ButtonSimple();
    private final Button.ButtonSimple btnDpadLeft = new Button.ButtonSimple();
    private final Button.ButtonSimple btnDpadRight = new Button.ButtonSimple();
    private final Button.ButtonSimple btnX = new Button.ButtonSimple();
    private final Button.ButtonSimple btnA = new Button.ButtonSimple();
    private final Button.ButtonSimple btnB = new Button.ButtonSimple();
    private final Button.ButtonSimple btnLeftBumper = new Button.ButtonSimple();
    private final Button.ButtonSimple btnRightBumper = new Button.ButtonSimple();
    public void handleTuningInput(com.qualcomm.robotcore.hardware.Gamepad gamepad) {
        if (btnDpadUp.wasJustPressed(gamepad.dpad_up))          { tuneP(1); updateTune(); }
        if (btnDpadDown.wasJustPressed(gamepad.dpad_down))      { tuneP(-1); updateTune(); }
        if (btnDpadRight.wasJustPressed(gamepad.dpad_right))    { tuneF(1); updateTune(); }
        if (btnDpadLeft.wasJustPressed(gamepad.dpad_left))      { tuneF(-1); updateTune(); }
        if (btnLeftBumper.wasJustPressed(gamepad.left_bumper))  { tuneHigh(1); updateTune(); }
        if (btnRightBumper.wasJustPressed(gamepad.right_bumper)){ tuneHigh(-1); updateTune(); }
        if (btnX.wasJustPressed(gamepad.x))                     { incrementStep(); }
        if (btnA.wasJustPressed(gamepad.a))                     { spinUp(); }
        if (gamepad.b)                                          { kicker.setPower(1);}else{ kicker.setPower(0);}

        updateVel();
    }
    public void sendTelemetry(org.firstinspires.ftc.robotcore.external.Telemetry telemetry) {
        double[] telemData = getTelemData();
        telemetry.addData("Controls", "Dpad Up/Down: P | Dpad Right/Left: F");
        telemetry.addData("Controls", "X: Change Step Size | A: Toggle Target Vel");
        telemetry.addData("Controls", "Bumper Left/Right : Target Vel");
        telemetry.addData("Controls", "B : Run Kicker");
        telemetry.addLine("----------------------------------------");
        telemetry.addData("Current Step Size", stepSizes[stepIndex]);
        telemetry.addData("P Coefficient", P);
        telemetry.addData("F Coefficient", F);
        telemetry.addData("Target Velocity (ticks/s)", curTargetVel);
        telemetry.addData("Actual Velocity (ticks/s)", telemData[0]);
        telemetry.addData("Error (Target - Actual)", telemData[1]);
        telemetry.addData("Calculated RPM", getRPM());
        telemetry.addData("Target HighVel", highVel);
    }
}
