package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import org.firstinspires.ftc.teamcode.helpers.Button;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Robot;

@TeleOp(name = "Turret Tuner", group = "Tuning")
public class TurretTuners extends LinearOpMode {

    Robot robot;

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot(hardwareMap);

        Button Ybutton = new Button();
        boolean pollenSelected = true;

        telemetry.addLine("Initialized. Ready to tune! 6766767676767776767667667676767676767667676767676776767667667676");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Controls", "Y : Toggle Turret Selection");
            if (Ybutton.wasJustPressed(gamepad1.y) && pollenSelected){ pollenSelected = false;}
            else if (Ybutton.wasJustPressed(gamepad1.y) && !pollenSelected) {pollenSelected = true;}

            if(pollenSelected){telemetry.addLine("Pollen Turret Selected!");
                robot.PollenTurret.handleTuningInput(gamepad1);
                robot.PollenTurret.sendTelemetry(telemetry);
            }
            else{telemetry.addLine("Nectar Turret Selected!");
                robot.NectarTurret.handleTuningInput(gamepad1);
                robot.NectarTurret.sendTelemetry(telemetry);
            }

        }
    }
}
