package org.firstinspires.ftc.teamcode;

import com.pedropathing.revhub.localizers.Encoder;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp
public class BadSwerve extends OpMode {
    DcMotor rb;
    DcMotor lf;
    DcMotor rf;
    DcMotor lb;

    CRServo rbTurn;
    CRServo lfTurn;
    CRServo rfTurn;
    CRServo lbTurn;

    DcMotor intake;
    DcMotor turret;

    AnalogInput rbTurnEncoder;
    AnalogInput lfTurnEncoder;
    AnalogInput rfTurnEncoder;
    AnalogInput lbTurnEncoder;


    @Override
    public void init() {
        // Map motors using configuration names
        rb = hardwareMap.get(DcMotor.class, "rb");
        lf = hardwareMap.get(DcMotor.class, "lf");
        rf = hardwareMap.get(DcMotor.class, "rf");
        lb = hardwareMap.get(DcMotor.class, "lb");

        // Intake & turret motor
        intake = hardwareMap.get(DcMotor.class, "intake");
        turret = hardwareMap.get(DcMotor.class, "turret");

        // Map continuous rotation servos using CRServo.class
        rbTurn = hardwareMap.get(CRServo.class, "rbTurn");
        lfTurn = hardwareMap.get(CRServo.class, "lfTurn");
        rfTurn = hardwareMap.get(CRServo.class, "rfTurn");
        lbTurn = hardwareMap.get(CRServo.class, "lbTurn");

        rbTurnEncoder = hardwareMap.get(AnalogInput.class, "rbTurnEncoder");
        lfTurnEncoder = hardwareMap.get(AnalogInput.class, "lfTurnEncoder");
        rfTurnEncoder = hardwareMap.get(AnalogInput.class, "rfTurnEncoder");
        lbTurnEncoder = hardwareMap.get(AnalogInput.class, "lbTurnEncoder");
    }

    @Override
    public void loop() {
        // Continuous rotation servos use setPower(-1.0 to 1.0)
        double turnPower = gamepad1.left_stick_x;

        rbTurn.setPower(turnPower);
        lfTurn.setPower(turnPower);
        rfTurn.setPower(turnPower);
        lbTurn.setPower(turnPower);

        // Map left stick Y to motor power (negated so pushing up is forward)
        double drivePower = -gamepad1.right_stick_y;

        rb.setPower(drivePower);
        lf.setPower(drivePower);
        rf.setPower(drivePower);
        lb.setPower(drivePower);

        intake.setPower(gamepad2.left_stick_y);
        turret.setPower(gamepad2.right_stick_y);

        telemetry.addData("rbTurnEncoder: ", rbTurnEncoder.getVoltage());
        telemetry.addData("lfTurnEncoder: ", lfTurnEncoder.getVoltage());
        telemetry.addData("rfTurnEncoder: ", rfTurnEncoder.getVoltage());
        telemetry.addData("lbTurnEncoder: ", lbTurnEncoder.getVoltage());

        telemetry.update();
    }
}