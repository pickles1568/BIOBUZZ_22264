package org.firstinspires.ftc.teamcode.teleop;

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

    @Override
    public void init() {
        robot = new Robot(hardwareMap, false);
        follower = Constants.create(hardwareMap);
        controls = new TeleopController(robot, gamepad1, gamepad2);
        shooterA = new Button(() -> gamepad1.a);
        shooterB = new Button(() -> gamepad1.b);
        shooterX = new Button(() -> gamepad1.x);
        shooterY = new Button(() -> gamepad1.y);
    }

    @Override
    public void loop() {

        controls.update(gamepad1, gamepad2);

        if (shooterX.wasJustPressed()){ // Low
            robot.PollenTurret.curTargetVel = 1500;
            robot.NectarTurret.curTargetVel = 1500;
        }else if (shooterY.wasJustPressed()){// Med
            robot.PollenTurret.curTargetVel = 1000;
            robot.NectarTurret.curTargetVel = 1000;
        }else if(shooterB.wasJustPressed()){// High
            robot.PollenTurret.curTargetVel = 500;
            robot.NectarTurret.curTargetVel = 500;
        }

        if (shooterY.wasJustPressed()){// STOP
            Scheduler.schedule(robot.PollenTurret.StopFlywheel, robot.NectarTurret.StopFlywheel);
        }


        Scheduler.execute();
        robot.update();
    }
}