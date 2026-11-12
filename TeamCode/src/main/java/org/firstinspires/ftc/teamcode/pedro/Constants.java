package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Vector2D;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.pedro.drivetrains.CoaxialServoPod;
import org.firstinspires.ftc.teamcode.pedro.drivetrains.CoaxialServoPodConfig;

import com.pedropathing.revhub.drivetrains.Swerve;
import com.pedropathing.revhub.drivetrains.SwerveConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;


public class Constants {


    //DT Defintions
    static double dtWidth = 438.35;
    static double dtLength = 402.00;

    public static SwerveConfig driveConfig = new SwerveConfig(
            c -> {
                c.zeroPowerBehavior.set(SwerveConfig.ZeroPowerBehavior.IGNORE_ANGLE_CHANGES);
                c.manualBrakeMode.set(true);
                c.voltageCompensation.set(false);
            }
    );

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(6.886046402097687);
        c.yPodOffset.set(-1.2856627637007108);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static CoaxialServoPodConfig rightBack = new CoaxialServoPodConfig(
            c -> {
                c.name.set("rightBack");
                c.motorName.set("rb"); // Port 0
                c.servoName.set("rbTurn");
                c.servoEncoderName.set("");
                c.driveDirection.set(DcMotorSimple.Direction.FORWARD);
                c.servoDirection.set(Servo.Direction.FORWARD);
                c.servoMinPosition.set(0.0);
                c.servoMaxPosition.set(1.0);
                c.angleOffsetRad.set(0.0);
                c.analogMinVoltage.set(0.018);
                c.analogMaxVoltage.set(3.19);


                c.podOffset.set(Vector2D.cartesian(-dtLength, -dtWidth));
                c.encoderReversed.set(Boolean.FALSE);


            }
    );

    public static CoaxialServoPodConfig leftFront = new CoaxialServoPodConfig(
            c -> {
                c.name.set("leftFront");
                c.motorName.set("lf"); // Port 1
                c.servoName.set("lfTurn");
                c.servoEncoderName.set(""); // Set to "" if using internal position control
                c.driveDirection.set(DcMotorSimple.Direction.FORWARD);
                c.servoDirection.set(Servo.Direction.FORWARD);
                c.servoMinPosition.set(0.0);
                c.servoMaxPosition.set(1.0);
                c.analogMinVoltage.set(0.019);
                c.analogMaxVoltage.set(3.21);
                c.angleOffsetRad.set(0.0);
                c.podOffset.set(Vector2D.cartesian(dtLength, dtWidth));
                c.encoderReversed.set(Boolean.FALSE);
            }
    );

    public static CoaxialServoPodConfig rightFront = new CoaxialServoPodConfig(
            c -> {
                c.name.set("rightFront");
                c.motorName.set("rf"); // Port 2
                c.servoName.set("rfTurn");
                c.servoEncoderName.set("");
                c.driveDirection.set(DcMotorSimple.Direction.FORWARD);
                c.servoDirection.set(Servo.Direction.FORWARD);
                c.servoMinPosition.set(0.0);
                c.servoMaxPosition.set(1.0);
                c.analogMinVoltage.set(0.014);
                c.analogMaxVoltage.set(3.164);
                c.angleOffsetRad.set(0.0);
                c.podOffset.set(Vector2D.cartesian(dtLength, -dtWidth));
                c.encoderReversed.set(Boolean.FALSE);
            }
    );

    public static CoaxialServoPodConfig leftBack = new CoaxialServoPodConfig(
            c -> {
                c.name.set("leftBack");
                c.motorName.set("lb"); // Port 3
                c.servoName.set("lbTurn");
                c.servoEncoderName.set("");
                c.driveDirection.set(DcMotorSimple.Direction.FORWARD);
                c.servoDirection.set(Servo.Direction.FORWARD);
                c.servoMinPosition.set(0.0);
                c.servoMaxPosition.set(1.0);
                c.analogMinVoltage.set(0.017);
                c.analogMaxVoltage.set(3.178);
                c.angleOffsetRad.set(0.0);
                c.podOffset.set(Vector2D.cartesian(-dtLength, dtWidth));
                c.encoderReversed.set(Boolean.FALSE);
            }
    );

    public static Swerve createDrivetrain(HardwareMap hardwareMap) {
        return new Swerve(
                hardwareMap,
                driveConfig,
                new CoaxialServoPod(hardwareMap, leftFront),
                new CoaxialServoPod(hardwareMap, rightFront),
                new CoaxialServoPod(hardwareMap, leftBack),
                new CoaxialServoPod(hardwareMap, rightBack)
        );
    }

    public static Follower create(HardwareMap hardwareMap) {
        return new Follower(new PinpointLocalizer(hardwareMap, localizerConfig), createDrivetrain(hardwareMap), null);

    }

}