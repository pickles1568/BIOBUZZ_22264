package org.firstinspires.ftc.teamcode.testingScripts;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Zeroing Test", group = "2 - Testing")
public class ZeroingTest extends OpMode {
    DcMotor rb;
    DcMotor lf;
    DcMotor rf;
    DcMotor lb;

    Servo rbTurn;
    Servo lfTurn;
    Servo rfTurn;
    Servo lbTurn;

    DcMotor intake;
    DcMotor turret;

    AnalogInput rbTurnEncoder;
    AnalogInput lfTurnEncoder;
    AnalogInput rfTurnEncoder;
    AnalogInput lbTurnEncoder;

    DcMotor Transfer;
    CRServo PollenKicker;


    @Override
    public void init() {
        // Map motors using configuration names
        rb = hardwareMap.get(DcMotor.class, "rb");
        lf = hardwareMap.get(DcMotor.class, "lf");
        rf = hardwareMap.get(DcMotor.class, "rf");
        lb = hardwareMap.get(DcMotor.class, "lb");

        // Intake & turret motor
        intake = hardwareMap.get(DcMotor.class, "intake");
        turret = hardwareMap.get(DcMotor.class, "PollenTurret");

        // Map continuous rotation servos using CRServo.class
        rbTurn = hardwareMap.get(Servo.class, "rbTurn");
        lfTurn = hardwareMap.get(Servo.class, "lfTurn");
        rfTurn = hardwareMap.get(Servo.class, "rfTurn");
        lbTurn = hardwareMap.get(Servo.class, "lbTurn");

        rbTurnEncoder = hardwareMap.get(AnalogInput.class, "rbTurnEncoder");
        lfTurnEncoder = hardwareMap.get(AnalogInput.class, "lfTurnEncoder");
        rfTurnEncoder = hardwareMap.get(AnalogInput.class, "rfTurnEncoder");
        lbTurnEncoder = hardwareMap.get(AnalogInput.class, "lbTurnEncoder");

        Transfer = hardwareMap.get(DcMotor.class, "transfer");
        PollenKicker = hardwareMap.get(CRServo.class, "pollenKicker");
    }

    @Override
    public void loop() {
        rbTurn.setPosition(0.5);
        lfTurn.setPosition(0.5);
        rfTurn.setPosition(0.5);
        lbTurn.setPosition(0.5);
    }
}