package org.firstinspires.ftc.teamcode;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;

import static com.pedropathing.ivy.Scheduler.isScheduled;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.conditional;
import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.deadline;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.sequential;

import com.pedropathing.revhub.localizers.Encoder;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

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

    public Command ToggleShooting;
    public Command StartShooting;
    public Command StopShooting;
    public Command ToggleSpinUp;
    public Command ToggleKick;
    public Command ToggleForceKick;

    public Servo NectarGate;
    public  double NectarGateSecondPos= 0.3;
    public  double NectarGateFirstPos= 0.1;

    public HardwareMap hwMap;

    public Command ShootForMoment;
    // Pass HardwareMap into the constructor
    public Robot(HardwareMap hwMap, Boolean IsRedAlliance){
        this.IsRedAlliance = IsRedAlliance;
        this.hwMap = hwMap;

        NectarGate = hwMap.get(Servo.class, "nectarGate");

        IntakeManager = new IntakeTransferManager(
                hwMap.get(DcMotor.class, "intake"),
                hwMap.get(DcMotor.class, "transfer"));

        PollenTurret = new Turret(hwMap.get(DcMotorEx.class, "PollenTurret"), hwMap.get(CRServo.class, "pollenKicker"), false);
        NectarTurret = new Turret(hwMap.get(DcMotorEx.class, "NectarTurret"), hwMap.get(CRServo.class, "nectarKicker"), true);

        aimSystem = new AimSystem(hwMap.get(CRServo.class, "Swivel"), hwMap.get(AnalogInput.class, "SwivelTurn"), hwMap.get(Limelight3A.class, "limelight"), IsRedAlliance);



        // Robot level commands
        StartShooting = parallel(PollenTurret.StartShooting, NectarTurret.StartShooting);
        StopShooting  = parallel(PollenTurret.StopShooting,  NectarTurret.StopShooting);
        ToggleShooting = conditional(() -> PollenTurret.IsShooting, StopShooting, StartShooting);

        Command ShootForMoment = sequential(
                StartShooting,
                waitMs(500),
                StopShooting
        );

        Command spinUpAll = parallel(PollenTurret.SpinUp, NectarTurret.SpinUp);
        Command kickAll = parallel(PollenTurret.KickWhenReady, NectarTurret.KickWhenReady, IntakeManager.startTransfer, instant(() -> {NectarGate.setPosition(NectarGateSecondPos);}));
        Command forceKickAll = parallel(PollenTurret.ForceKick, NectarTurret.ForceKick, IntakeManager.startTransfer, instant(() -> {NectarGate.setPosition(NectarGateSecondPos);}));
        Command stopKickAll = parallel(PollenTurret.StopKicking, NectarTurret.StopKicking, IntakeManager.stopTraransfer, instant(() -> {NectarGate.setPosition(NectarGateFirstPos);}));

        // B: spin up, or stop flywheel AND kicker
        ToggleSpinUp = conditional(() -> PollenTurret.IsShooting, StopShooting, spinUpAll);

        // X: start a (speed-gated) kick, or stop kicking / cancel a queued kick
        ToggleKick = conditional(
                () -> PollenTurret.IsKicking || PollenTurret.KickRequested,
                stopKickAll, kickAll);

        // Back: failsafe raw kick toggle,
        ToggleForceKick = conditional(() -> PollenTurret.IsKicking, stopKickAll, forceKickAll);
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
