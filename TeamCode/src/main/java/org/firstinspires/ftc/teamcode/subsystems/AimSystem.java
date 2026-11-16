package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

public class AimSystem {

    //EXPORTS
    public boolean CanSeeHive;
    public float AngleToShoot;
    public float DistanceToShoot;
    public float ReqRPM;

    Servo Swivel;
    public AimSystem(Servo Swivel, Boolean IsRedAlliance){
        this.Swivel = Swivel;
        this.CanSeeHive = false;
        this.ReqRPM = 0;
    }

    public void TurnSwivel(double Pos){
        Swivel.setPosition(Pos);
    }

    public float CalcRPM(float distance){
        return distance * 2;   // TODO - replace w/ Trey's math
    }

    public float CalcAngle(float YOff){
        return 0;// use actual limelight at some point
    }


    public void update() {
        //Limelight.Update
        if (CanSeeHive){
            AngleToShoot = CalcAngle(0);
            //Swivel.SetAngle = AngleToShoot;
            ReqRPM = CalcRPM(DistanceToShoot);
        }else{
            ReqRPM = 0;
            AngleToShoot = 0;
        }
        //the part where it updates
    }
}
