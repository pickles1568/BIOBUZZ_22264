package org.firstinspires.ftc.teamcode.teleop;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.helpers.Button;


import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Scheduler;

import static com.pedropathing.ivy.Scheduler.schedule;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;


@Disabled
@TeleOp(name = "Main TeleOp")
public class TeleopMain extends OpMode {

    private Follower follower;
    Button btnA; Button btnB;
    Robot robot;

    @Override
    public void init() {
        robot = new Robot(hardwareMap, true);

        follower = Constants.create(hardwareMap);

        btnA = new Button(()-> gamepad1.a);
        btnB = new Button(() -> gamepad1.b);
    }

    @Override
    public void loop() {
//        DrivePowers powers = ManualDrive.fieldCentric(
//                -gamepad1.left_stick_y,
//                gamepad1.left_stick_x,
//                gamepad1.right_stick_x,
//                follower.pose().heading()
//        );
//
//        ManualDrive.driveOrHold(follower, powers);
//        follower.update();

        if(btnA.wasJustPressed()){schedule(robot.IntakeManager.toggleIntakeMode);}
        if(btnB.wasJustPressed()){schedule(robot.ToggleShooting);}

        Scheduler.execute();
        robot.update();
    }
}