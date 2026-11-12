package org.firstinspires.ftc.teamcode;

import static com.pedropathing.ivy.Scheduler.schedule;
import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.teamcode.helpers.Button;

public class TeleopController {

    private final Robot robot;

    //Driver A buttons
    private final Button driverA;
    private final Button driverLBump;
    private final Button driverRBump;

    //Driver B Buttons
    private final Button shooterB;
    private final Button shooterRBump;
    private final Button shooterLBump;



    public TeleopController(Robot robot, Gamepad gamepad1, Gamepad gamepad2) {
        this.robot = robot;

        //driver Button binding
        this.driverA = new Button(() -> gamepad1.a);
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

        if (driverLBump.isHeld() || driverRBump.isHeld())     {            }//slow mode


        // Handle operator/shooter controls
        if (shooterB.wasJustPressed()) {           }
        if (shooterRBump.wasJustPressed()){        schedule(robot.StartShooting);}//shooterRightBump
        if (shooterLBump.wasJustPressed()){        schedule(robot.StopShooting);}
        robot.aimSystem.TurnSwivel(gamepad2.right_stick_x);
    }
}