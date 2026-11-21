package org.firstinspires.ftc.teamcode.teleop;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.TeleopController;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp(name = "BLUE TeleOp", group = "0 - Game Ready")
public class BlueTeleOp extends OpMode {

    private Follower follower;
    private Robot robot;
    private TeleopController controls;

    @Override
    public void init() {
        robot = new Robot(hardwareMap, false);
        follower = Constants.create(hardwareMap);
        follower.setPose(com.pedropathing.math.Pose.zero());
        controls = new TeleopController(robot, follower, gamepad1, gamepad2);

        telemetry.addLine("Initialized! BLUE");
        telemetry.update();
    }

    @Override
    public void loop() {
        Scheduler.execute();
        robot.update();

        double targetTicks = robot.PollenTurret.curTargetVel;
        double actualTicks = robot.PollenTurret.Flywheel.getVelocity();
        telemetry.addLine("Pollen shooter:");
        telemetry.addData("Target (ticks/s)", targetTicks);
        telemetry.addData("Actual (ticks/s)", actualTicks);
        telemetry.addData("Target RPM", targetTicks * 60.0 / 28.0);
        telemetry.addData("Actual RPM", actualTicks * 60.0 / 28.0);


        double NtargetTicks = robot.NectarTurret.curTargetVel;
        double NactualTicks = robot.NectarTurret.Flywheel.getVelocity();
        telemetry.addLine("Pollen shooter:");
        telemetry.addData("Target (ticks/s)", NtargetTicks);
        telemetry.addData("Actual (ticks/s)", NactualTicks);
        telemetry.addData("Target RPM", NtargetTicks * 60.0 / 28.0);
        telemetry.addData("Actual RPM", NactualTicks * 60.0 / 28.0);


        controls.update(gamepad1, gamepad2);

        telemetry.addLine("Aiming data:");
        telemetry.addData("Angle",robot.aimSystem.getTurretCurrentDegrees());
        telemetry.addData("Error", robot.aimSystem.getError());

        telemetry.addData("Min Limit", robot.aimSystem.minAngle);
        telemetry.addData("Max limit", robot.aimSystem.maxAngle);

        telemetry.update();
    }
}