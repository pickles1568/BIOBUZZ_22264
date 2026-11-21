package org.firstinspires.ftc.teamcode;

import static com.pedropathing.ivy.Scheduler.schedule;


import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.teamcode.helpers.Button;

public class TeleopController {

    private final Robot robot;
    private Follower follower;

    //Driver A buttons
    private final Button driverA;
    private final Button driverB;
    private final Button driverLBump;
    private final Button driverRBump;
    private final Button driverResetHeading;

    //Driver B Buttons
    private final Button shooterB;
    private final Button shooterRBump;
    private final Button shooterLBump;

    private double headingOffset = 0.0;
    private final Button shooterUpDPad;
    private final Button shooterDownDPad;
    private final Button shooterX;
    private final Button shooterBack;

    public TeleopController(Robot robot, Follower follower, Gamepad gamepad1, Gamepad gamepad2) {
        this.robot = robot;
        this.follower = follower;

        //driver Button binding
        this.driverA = new Button(() -> gamepad1.a);
        this.driverB = new Button(() -> gamepad1.b);
        this.driverLBump = new Button(() -> gamepad1.left_bumper);
        this.driverRBump = new Button(() -> gamepad1.right_bumper);
        this.driverResetHeading = new Button(() -> gamepad1.back || gamepad1.options);

        //Shooter Button Binding
        this.shooterB = new Button(() -> gamepad2.b);
        this.shooterRBump = new Button(() -> gamepad2.right_bumper);
        this.shooterLBump = new Button(() -> gamepad2.left_bumper);
        this.shooterUpDPad = new Button(() -> gamepad2.dpad_up);
        this.shooterDownDPad = new Button(() -> gamepad2.dpad_down);
        this.shooterX = new Button(() -> gamepad2.x);
        this.shooterBack = new Button(() -> gamepad2.back);
    }

    public void update(Gamepad gamepad1, Gamepad gamepad2) {
        // Handle driver controls
        if (driverA.wasJustPressed()) {            schedule(robot.IntakeManager.toggleIntakeMode);}
        if (driverB.wasJustPressed()) {            schedule(robot.IntakeManager.toggleReverseIntakeMode);}

        // Reset heading for field-centric mode
        if (driverResetHeading.wasJustPressed()) {
            follower.setPose(Pose.zero());
            headingOffset = follower.pose().heading();
        }

        double teleopScale = (driverLBump.isHeld() || driverRBump.isHeld()) ? 1.0 : 2.0;
        double forward = teleopScale * -gamepad1.left_stick_y;
        double lateral = teleopScale * gamepad1.left_stick_x;
        double turn    = teleopScale * -gamepad1.right_stick_x;

        double currentHeading = -(follower.pose().heading() - headingOffset);
        DrivePowers powers = ManualDrive.fieldCentric(forward, lateral, turn, currentHeading);

        follower.manual(powers);
        follower.update();


        // Handle operator/shooter controls
        robot.aimSystem.manualInfluence = gamepad2.left_stick_x;
        if (shooterB.wasJustPressed())    schedule(robot.ToggleSpinUp);
        if (shooterX.wasJustPressed())    schedule(robot.ToggleKick);
        if (shooterBack.wasJustPressed()) schedule(robot.ToggleForceKick);
    }
}