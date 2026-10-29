package org.firstinspires.ftc.teamcode.subsystems;

import static com.pedropathing.ivy.commands.Commands.conditional;
import static com.pedropathing.ivy.commands.Commands.instant;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Turret {

    final double encoderResolutionTPR = 28.0;
    final double MaxRPM = 6000;

    DcMotorEx Flywheel;

    double lastEncoderPosition;
    long lastTimeNano;

    public double highVel = 5500;
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

    public Command StopFlywheel = instant(() -> {
        curTargetVel = 0;
        Flywheel.setVelocity(0);
        Flywheel.setPower(0);
    });

    public Command ToggleFlywheel = conditional(
            () -> DoFlywheel.isScheduled(),
            StopFlywheel,
            DoFlywheel
    );
}
