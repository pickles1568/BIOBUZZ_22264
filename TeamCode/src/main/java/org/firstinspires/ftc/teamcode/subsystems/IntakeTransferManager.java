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
    Boolean Collecting = false;

    public IntakeTransferManager(DcMotor Intake, DcMotor Transfer) {
        this.Intake = Intake;
        this.Transfer = Transfer;

        Intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        Transfer.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    Command StartCollecting = instant(() -> {Intake.setPower(1); Transfer.setPower(1); Collecting = true;}).requiring(Intake, Transfer);
    Command StopCollecting = instant(() -> {Intake.setPower(0); Transfer.setPower(0); Collecting = false;}).requiring(Intake, Transfer);

    Command ActivateTransfer = instant(() -> {Transfer.setPower(1);}).requiring(Transfer);
    Command StopTransfer = instant(() -> {Transfer.setPower(0);}).requiring(Transfer);

    public Command toggleIntakeMode = conditional(
            () -> Collecting,
            StopCollecting,
            StartCollecting
    ).requiring(Intake, Transfer);

}