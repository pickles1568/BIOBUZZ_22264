package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Turret {

    final double encoderResolutionTPR = 28.0;
    final double MaxRPM = 1150;

    DcMotorEx Flywheel;

    double lastEncoderPosition;
    long lastTimeNano;

    public double highVel = 2500;
    public double lowVel = 1500;

    // Made public so the tuner OpMode can display them in telemetry
    public double curTargetVel = highVel;
    public double F = 10.0;
    public double P = 10.0;

    public double[] stepSizes = {10.0, 1.0, 0.1, 0.001, 0.0001};
    public int stepIndex = 1;

    public Turret(DcMotorEx Flywheel) {
        this.Flywheel = Flywheel;
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
}