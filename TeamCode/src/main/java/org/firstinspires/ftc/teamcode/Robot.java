package org.firstinspires.ftc.teamcode;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;

import static com.pedropathing.ivy.Scheduler.isScheduled;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.conditional;
import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.sequential;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.subsystems.AimSystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeTransferManager;
import org.firstinspires.ftc.teamcode.subsystems.Turret;

public class Robot {
    Boolean IsRedAlliance;

    // Public as to allow outside reference (67)
    public IntakeTransferManager IntakeManager;
    public Turret PollenTurret;
    public Turret NectarTurret;
    public AimSystem aimSystem;

     Command ToggleShooting;
    public Command StartShooting;
    public Command StopShooting;

    // Pass HardwareMap into the constructor
    public Robot(HardwareMap hwMap, Boolean IsRedAlliance){
        this.IsRedAlliance = IsRedAlliance;

        IntakeManager = new IntakeTransferManager(
                hwMap.get(DcMotor.class, "intake"),
                hwMap.get(DcMotor.class, "transfer"));

        PollenTurret = new Turret(hwMap.get(DcMotorEx.class, "PollenTurret"), hwMap.get(CRServo.class, "pollenKicker"));
        NectarTurret = new Turret(hwMap.get(DcMotorEx.class, "NectarTurret"), hwMap.get(CRServo.class, "nectarKicker"));

        aimSystem = new AimSystem(hwMap.get(CRServo.class, "Swivel"), IsRedAlliance);

        // Robot level commands
        StartShooting = parallel(PollenTurret.StartShooting, NectarTurret.StartShooting);
        StopShooting  = parallel(PollenTurret.StopShooting,  NectarTurret.StopShooting);
        ToggleShooting = conditional(() -> PollenTurret.IsShooting, StopShooting, StartShooting);
    }
    public Robot(HardwareMap hwMap) {this(hwMap, true);} //if no boolean passed in assume true

    public void update(){
        aimSystem.update();   // first, so ReqRPM is fresh

        PollenTurret.setAimRPM(aimSystem.ReqRPM);
        NectarTurret.setAimRPM(aimSystem.ReqRPM);

        PollenTurret.applyShootVel();
        NectarTurret.applyShootVel();

        PollenTurret.updateVel();
        NectarTurret.updateVel();
    }
}
