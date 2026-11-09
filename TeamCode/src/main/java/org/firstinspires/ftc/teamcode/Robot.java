package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.subsystems.IntakeTransferManager;
import org.firstinspires.ftc.teamcode.subsystems.Turret;

public class Robot {
    Boolean IsRedAlliance;

    // Public as to allow outside reference (67)
    public IntakeTransferManager IntakeManager;
    public Turret PollenTurret;
    public Turret NectarTurret;

    // Pass HardwareMap into the constructor
    public Robot(HardwareMap hwMap, Boolean IsRedAlliance){
        this.IsRedAlliance = IsRedAlliance;

        IntakeManager = new IntakeTransferManager(
                hwMap.get(DcMotor.class, "intake"),
                hwMap.get(DcMotor.class, "transfer"));

        PollenTurret = new Turret(hwMap.get(DcMotorEx.class, "PollenTurret"), hwMap.get(CRServo.class, "pollenKicker"));
        NectarTurret = new Turret(hwMap.get(DcMotorEx.class, "NectarTurret"), hwMap.get(CRServo.class, "nectarKicker"));
    }

    public Robot(HardwareMap hwMap) {this(hwMap, true);}
}
