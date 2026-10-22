package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.teamcode.subsystems.Turret;

@TeleOp(name = "Turret PIDF Tuner", group = "Tuning")
public class TurretPIDFTuner extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorEx flywheelMotor = hardwareMap.get(DcMotorEx.class, "turret");
        Turret turret = new Turret(flywheelMotor);

        boolean dpadUpPrev = false;
        boolean dpadDownPrev = false;
        boolean dpadLeftPrev = false;
        boolean dpadRightPrev = false;
        boolean xPrev = false;
        boolean aPrev = false;

        telemetry.addData("Status", "Initialized. Ready to tune!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Read current gamepad states
            boolean dpadUp = gamepad1.dpad_up;
            boolean dpadDown = gamepad1.dpad_down;
            boolean dpadLeft = gamepad1.dpad_left;
            boolean dpadRight = gamepad1.dpad_right;
            boolean xButton = gamepad1.x;
            boolean aButton = gamepad1.a;

            // Dpad Up / Down: Tune P coefficient
            if (dpadUp && !dpadUpPrev) {
                turret.tuneP(1);
                turret.updateTune();
            } else if (dpadDown && !dpadDownPrev) {
                turret.tuneP(-1);
                turret.updateTune();
            }

            // Dpad Right / Left: Tune F coefficient
            if (dpadRight && !dpadRightPrev) {
                turret.tuneF(1);
                turret.updateTune();
            } else if (dpadLeft && !dpadLeftPrev) {
                turret.tuneF(-1);
                turret.updateTune();
            }

            // X Button: Cycle through step increments (10, 1, 0.1, 0.001, etc.)
            if (xButton && !xPrev) {
                turret.incrementStep();
            }

            // A Button: Toggle target velocity (high / low)
            if (aButton && !aPrev) {
                turret.spinUp();
            }

            // Continuously apply velocity command
            turret.updateVel();

            // Save state for next loop iteration (debounce)
            dpadUpPrev = dpadUp;
            dpadDownPrev = dpadDown;
            dpadLeftPrev = dpadLeft;
            dpadRightPrev = dpadRight;
            xPrev = xButton;
            aPrev = aButton;

            // --- Telemetry Output ---
            double[] telemData = turret.getTelemData();
            telemetry.addData("Controls", "Dpad Up/Down: P | Dpad Right/Left: F");
            telemetry.addData("Controls", "X: Change Step Size | A: Toggle Target Vel");
            telemetry.addLine("----------------------------------------");
            telemetry.addData("Current Step Size", turret.stepSizes[turret.stepIndex]);
            telemetry.addData("P Coefficient", turret.P);
            telemetry.addData("F Coefficient", turret.F);
            telemetry.addData("Target Velocity (ticks/s)", turret.curTargetVel);
            telemetry.addData("Actual Velocity (ticks/s)", telemData[0]);
            telemetry.addData("Error (Target - Actual)", telemData[1]);
            telemetry.addData("Calculated RPM", turret.getRPM());
            telemetry.update();
        }
    }
}