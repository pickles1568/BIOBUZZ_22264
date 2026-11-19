package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;

public class AimSystem {

    //EXPORTS
    public boolean CanSeeHive;
    public float AngleToShoot;
    public float DistanceToShoot;
    public float ReqRPM;

    private static final double minAngle = -180;
    private static final double maxAngle = 180;
    private static final double KP = 0.02;

    private static final int RedPipeline = 1;
    private static final int BluePipeline = 1;

    private static final double gearRatio = 5.0; // 5 Servo rotations : 1 Turret rotation

    CRServo Swivel;
    AnalogInput SwivelEncoder;
    Limelight3A LimeLight;
    public AimSystem(CRServo Swivel, AnalogInput SwivelEncoder, Limelight3A LimeLight, Boolean IsRedAlliance){
        this.Swivel = Swivel;
        this.SwivelEncoder = SwivelEncoder;
        this.LimeLight = LimeLight;

        this.CanSeeHive = false;
        this.ReqRPM = 0;

        setAlliancePipeline(IsRedAlliance);
    }

    public void setAlliancePipeline(boolean isRedAlliance) {
        int targetPipeline = isRedAlliance ? RedPipeline : BluePipeline;
        LimeLight.pipelineSwitch(targetPipeline);
    }


    public double getTurretCurrentDegrees() {
        double voltage = SwivelEncoder.getVoltage();
        double maxVoltage = SwivelEncoder.getMaxVoltage();
        double turretDegrees = (voltage / maxVoltage) * 360.0;

        return turretDegrees;
    }


    public void update() {
        LLResult result = LimeLight.getLatestResult();

        if (result != null && result.isValid()) {
            CanSeeHive = true;
            double tx = result.getTx(); // Horizontal offset from target

            DistanceToShoot = (float) result.getTa(); //How big is it??? basically distance

            double error = tx;
            double swivelPower = error * KP;

            // Enforce Software Limits
            double currentDegrees = getTurretCurrentDegrees();
            if (currentDegrees >= maxAngle && swivelPower > 0) {
                swivelPower = 0.0;
            } else if (currentDegrees <= minAngle && swivelPower < 0) {
                swivelPower = 0.0;
            }

            TurnSwivel(swivelPower);

            ReqRPM = CalcRPM(DistanceToShoot);
        } else {
            CanSeeHive = false;
            ReqRPM = 0;
            //AngleToShoot = 0;
            TurnSwivel(0.0); // Stop swivel if target is lost
        }
    }


    public void TurnSwivel(double Power){
        Swivel.setPower(Power);
    }

    public float CalcRPM(float distance){
        return distance * 2;   // TODO - replace w/ Trey's math
    }



//    public void update() {
//        //Limelight.Update
//        if (CanSeeHive){
//            AngleToShoot = CalcAngle(0);
//            //Swivel.SetAngle = AngleToShoot;
//            ReqRPM = CalcRPM(DistanceToShoot);
//        }else{
//            ReqRPM = 0;
//            AngleToShoot = 0;
//        }
//        //the part where it updates
//    }

    public void stop() {Swivel.setPower(0.0);}
}
