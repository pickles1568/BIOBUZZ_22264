package org.firstinspires.ftc.teamcode.teleop;

import static com.pedropathing.ivy.Scheduler.schedule;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.TeleopController;
import org.firstinspires.ftc.teamcode.helpers.Button;
import org.firstinspires.ftc.teamcode.pedro.Constants;

import org.firstinspires.ftc.teamcode.helpers.Button;

@TeleOp(name = "Demo TeleOp", group = "1 - Demos")
public class Demo extends OpMode {

    private Follower follower;
    private Robot robot;
    private TeleopController controls;

    Button shooterA;
    Button shooterB;
    Button shooterX;
    Button shooterY;
    Button Turret1;
    Button Turret2;
    Button Turret3;


    @Override
    public void init() {
        robot = new Robot(hardwareMap, false);
        follower = Constants.create(hardwareMap);
        follower.setPose(com.pedropathing.math.Pose.zero());
        controls = new TeleopController(robot, follower, gamepad1, gamepad2);
        shooterX = new Button(() -> gamepad2.x);
        shooterY = new Button(() -> gamepad2.y);
        shooterA = new Button(() -> gamepad2.a);
        Turret1 = new Button(() -> gamepad2.dpad_left);
        Turret2 = new Button(() -> gamepad2.dpad_up);
        Turret3 = new Button(() -> gamepad2.dpad_right);
    }

    @Override
    public void loop() {

        controls.update(gamepad1, gamepad2);

        if (shooterX.wasJustPressed()){ // High
            robot.PollenTurret.Flywheel.setPower(1);
            robot.NectarTurret.Flywheel.setPower(1);
        }
        if (shooterY.wasJustPressed()){// STOP
            robot.PollenTurret.Flywheel.setPower(0);
            robot.NectarTurret.Flywheel.setPower(0);
            robot.PollenTurret.kicker.setPower(0);
            robot.NectarTurret.kicker.setPower(0);
//            schedule(robot.PollenTurret.StopKicking);
//            schedule(robot.NectarTurret.StopKicking);
        }

        if (shooterA.wasJustPressed()){
            schedule(robot.IntakeManager.toggleIntakeMode);
//            schedule(robot.PollenTurret.StartKicking);
//            schedule(robot.NectarTurret.StartKicking);
            robot.PollenTurret.kicker.setPower(1);
            robot.NectarTurret.kicker.setPower(1);
        }



        // We live in a society
//        if (Turret1.wasJustPressed()){
//            robot.aimSystem.TurnSwivel(0.45);
//        }
//        if (Turret2.wasJustPressed()){
//            robot.aimSystem.TurnSwivel(0.5);
//        }
//        if (Turret3.wasJustPressed()){
//            robot.aimSystem.TurnSwivel(0.55);
//        }

        Scheduler.execute();
        //robot.update();
    }
}