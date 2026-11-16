package org.firstinspires.ftc.teamcode.teleop;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.TeleopController;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp(name = "RED TeleOp", group = "0 - Game Ready")
public class RedTeleOp extends OpMode {

    private Follower follower;
    private Robot robot;
    private TeleopController controls;

    @Override
    public void init() {
        robot = new Robot(hardwareMap, true);
        follower = Constants.create(hardwareMap);
        controls = new TeleopController(robot, gamepad1, gamepad2);

        telemetry.addLine("Initialized! RED");
        telemetry.update();
    }

    @Override
    public void loop() {
        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                follower.pose().heading()
        );

        follower.manual(powers);
        follower.update();

        controls.update(gamepad1, gamepad2);

        Scheduler.execute();
        robot.update();
    }
}