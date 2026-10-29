package org.firstinspires.ftc.teamcode.subsystems;

import static com.pedropathing.ivy.commands.Commands.conditional;
import static com.pedropathing.ivy.commands.Commands.instant;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class IntakeTransferManager {
    DcMotor Intake;
    DcMotor Transfer;
    CRServo PollenKicker;
    Boolean Collecting = false;

    public IntakeTransferManager(DcMotor Intake, DcMotor Transfer, CRServo PollenKicker) {
        this.Intake = Intake;
        this.Transfer = Transfer;
        this.PollenKicker = PollenKicker;

        Intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        Intake.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    Command StartCollecting = instant(() -> {Intake.setPower(1); Transfer.setPower(1); Collecting = true;});
    Command StopCollecting = instant(() -> {Intake.setPower(0); Transfer.setPower(0); Collecting = false;});

    public Command StartKicking = instant(() -> {PollenKicker.setPower(1);});
    public Command StopKicking = instant(() -> {PollenKicker.setPower(0);});

    Command ActivateTransfer = instant(() -> {Transfer.setPower(1);});
    Command StopTransfer = instant(() -> {Transfer.setPower(0);});

    public Command toggleIntakeMode = conditional(
            () -> Collecting,
            StopCollecting,
            StartCollecting
    );

}