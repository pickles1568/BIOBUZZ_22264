package org.firstinspires.ftc.teamcode;
import  org.firstinspires.ftc.teamcode.subsystems.*; //All the subsystems


import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.conditional;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.sequential;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;


@TeleOp(name = "Main TeleOp")
public class TeleopMain extends OpMode {

    private Follower follower;

    boolean HoldingA = false; boolean HoldingB = false;
    IntakeTransferManager IntakeManager;
    Turret PollenTurret;
    Command StartShooting; Command StopShooting; Command ToggleShooting;


    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        IntakeManager = new IntakeTransferManager(
                hardwareMap.get(DcMotor.class, "intake"),
                hardwareMap.get(DcMotor.class, "transfer"),
                hardwareMap.get(CRServo.class, "pollenKicker"));

        PollenTurret = new Turret(hardwareMap.get(DcMotorEx.class, "PollenTurret"));

        StopShooting = parallel( PollenTurret.StopFlywheel, IntakeManager.StopKicking);

        StartShooting = sequential(PollenTurret.DoFlywheel, parallel(PollenTurret.RunFlywheel, IntakeManager.StartKicking));

        ToggleShooting = conditional(() -> StartShooting.isScheduled(),
                StopShooting,
                StartShooting
        );
    }

    @Override
    public void loop() {
        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                follower.pose().heading()
        );

        ManualDrive.driveOrHold(follower, powers);
        follower.update();

        if(!HoldingA && gamepad1.a){schedule(IntakeManager.toggleIntakeMode);} HoldingA = gamepad1.a;
        if(!HoldingB&& gamepad1.b){schedule(ToggleShooting);} HoldingB = gamepad1.b;


        Scheduler.execute();
    }
}