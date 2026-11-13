package org.firstinspires.ftc.teamcode.testingScripts;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp(name = "Testing Script", group = "2 - Testing")
public class TestingScript extends OpMode {
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
        rbTurn = hardwareMap.get(CRServo.class, "rbTurn");
        lfTurn = hardwareMap.get(CRServo.class, "lfTurn");
        rfTurn = hardwareMap.get(CRServo.class, "rfTurn");
        lbTurn = hardwareMap.get(CRServo.class, "lbTurn");

        rbTurnEncoder = hardwareMap.get(AnalogInput.class, "rbTurnEncoder");
        lfTurnEncoder = hardwareMap.get(AnalogInput.class, "lfTurnEncoder");
        rfTurnEncoder = hardwareMap.get(AnalogInput.class, "rfTurnEncoder");
        lbTurnEncoder = hardwareMap.get(AnalogInput.class, "lbTurnEncoder");

        Transfer = hardwareMap.get(DcMotor.class, "transfer");
        PollenKicker = hardwareMap.get(CRServo.class, "pollenKicker");
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


        telemetry.addData("rbTurnEncoder: ", rbTurnEncoder.getVoltage());
        telemetry.addData("lfTurnEncoder: ", lfTurnEncoder.getVoltage());
        telemetry.addData("rfTurnEncoder: ", rfTurnEncoder.getVoltage());
        telemetry.addData("lbTurnEncoder: ", lbTurnEncoder.getVoltage());

        if(gamepad2.a){
            Transfer.setPower(-1);
            telemetry.addLine("Transfer Running");
        }else{
            Transfer.setPower(0);
            telemetry.addLine("(A) Run Transfer");
        }

        if(gamepad2.b) {
            turret.setPower((gamepad2.right_stick_y + 1) * 0.5);
            telemetry.addLine("[Right Stick] Shooter Running");
        }else{
            turret.setPower(0);
            telemetry.addLine("(B) [Right Stick] Run Shooter");
        }

        if(gamepad2.x) {
            PollenKicker.setPower(1);
            telemetry.addLine("Kicker Running");
        }else{
            PollenKicker.setPower(0);
            telemetry.addLine("(X) Run Kicker");
        }


        telemetry.update();
    }
}