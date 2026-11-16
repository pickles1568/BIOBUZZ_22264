package org.firstinspires.ftc.teamcode;

import static com.pedropathing.ivy.Scheduler.schedule;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.teamcode.helpers.Button;
import org.firstinspires.ftc.teamcode.pedro.Constants;

public class TeleopController {

    private final Robot robot;
    private Follower follower;

    //Driver A buttons
    private final Button driverA;
    private final Button driverB;
    private final Button driverLBump;
    private final Button driverRBump;

    //Driver B Buttons
    private final Button shooterB;
    private final Button shooterRBump;
    private final Button shooterLBump;



    public TeleopController(Robot robot, Gamepad gamepad1, Gamepad gamepad2) {
        this.robot = robot;
        follower = Constants.create(robot.hwMap);

        //driver Button binding
        this.driverA = new Button(() -> gamepad1.a);
        this.driverB = new Button(() -> gamepad1.b);
        this.driverLBump = new Button(() -> gamepad1.left_bumper);
        this.driverRBump = new Button(() -> gamepad1.right_bumper);

        //Shooter Button Binding
        this.shooterB = new Button(() -> gamepad2.b);
        this.shooterRBump = new Button(() -> gamepad2.right_bumper);
        this.shooterLBump = new Button(() -> gamepad2.left_bumper);
    }

    public void update(Gamepad gamepad1, Gamepad gamepad2) {
        // Handle driver controls
        if (driverA.wasJustPressed()) {            schedule(robot.IntakeManager.toggleIntakeMode);}
        if (driverB.wasJustPressed()) {            schedule(robot.IntakeManager.toggleReverseIntakeMode);}
        //Slow Mode
        if (driverLBump.isHeld() || driverRBump.isHeld()) {
            DrivePowers powers = ManualDrive.fieldCentric(
                    (0.5 * -gamepad1.left_stick_y),
                    (0.5 * gamepad1.left_stick_x),
                    (0.5 * gamepad1.right_stick_x),
                    follower.pose().heading()
            );

            follower.manual(powers);
            follower.update();
        } else {
            DrivePowers powers = ManualDrive.fieldCentric(
                    -gamepad1.left_stick_y,
                    gamepad1.left_stick_x,
                    gamepad1.right_stick_x,
                    follower.pose().heading()
            );

            follower.manual(powers);
            follower.update();
        }


        // Handle operator/shooter controls
        if (shooterB.wasJustPressed()) {           schedule(robot.ToggleShooting);}
        //if (shooterRBump.wasJustPressed()){        schedule(robot.StartShooting);}//shooterRightBump
        //if (shooterLBump.wasJustPressed()){        schedule(robot.StopShooting);}
        robot.aimSystem.TurnSwivel(gamepad2.right_stick_x);
    }
}