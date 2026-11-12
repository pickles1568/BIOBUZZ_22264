package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;

public class AimSystem {

    CRServo Swivel;
    public AimSystem(CRServo Swivel){
        this.Swivel = Swivel;
    }

    public void TurnSwivel(double Pwr){
        Swivel.setPower(Pwr);
    }
}
