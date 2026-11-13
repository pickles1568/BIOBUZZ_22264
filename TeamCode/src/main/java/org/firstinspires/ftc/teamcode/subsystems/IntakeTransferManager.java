package org.firstinspires.ftc.teamcode.subsystems;

import static com.pedropathing.ivy.commands.Commands.conditional;
import static com.pedropathing.ivy.commands.Commands.instant;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class IntakeTransferManager {
    DcMotor Intake;
    DcMotor Transfer;
    boolean Collecting = false;
    boolean Expelling = false;

    public IntakeTransferManager(DcMotor Intake, DcMotor Transfer) {
        this.Intake = Intake;
        this.Transfer = Transfer;

        Intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        Transfer.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    Command StartCollecting = instant(() -> {Intake.setPower(1); Transfer.setPower(1); Collecting = true; Expelling = false;}).requiring(Intake, Transfer);
    Command StopCollecting = instant(() -> {Intake.setPower(0); Transfer.setPower(0); Collecting = false; Expelling = false;}).requiring(Intake, Transfer);
    Command ReveseIntake = instant(() -> {Intake.setPower(-1); Transfer.setPower(-1); Collecting = false; Expelling = true;});

    Command ActivateTransfer = instant(() -> {Transfer.setPower(1);}).requiring(Transfer);
    Command StopTransfer = instant(() -> {Transfer.setPower(0);}).requiring(Transfer);

    public Command toggleIntakeMode = conditional(
            () -> Collecting,
            StopCollecting,
            StartCollecting
    ).requiring(Intake, Transfer);

    public Command toggleReverseIntakeMode = conditional(
            () -> Expelling,
            StopCollecting,
            ReveseIntake
    ).requiring(Intake, Transfer);

}