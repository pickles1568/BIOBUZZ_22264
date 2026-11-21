package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.drivetrain.Drivetrain;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;

import java.util.Map;

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
        c.xPodOffset.set(4.1857765978715555);
        c.yPodOffset.set(2.341121613510012);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.03);
                Controller secondaryTranslationalForward = Controller.proportional(0.03);
                Controller primaryTranslationalLateral = Controller.proportional(0.03);
                Controller secondaryTranslationalLateral = Controller.proportional(0.03);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.04));
                c.brake.set(Controller.proportionalFeedforward(0.04));

                c.headingFeedback.set(Controller.proportional(3.2));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.0564, 0.0458));

                c.linearBrakeCoefficients.set(Matrix.diag(0.0303, 0.0714));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0020, 0.0010));

                c.maxAchievableForwardVelocity.set(25.0);
                c.maxAchievableStrafeVelocity.set(25.0);
                c.naturalForwardDeceleration.set(30.0);
                c.naturalStrafeDeceleration.set(30.0);
            }
    );

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
                c.servoMinAngleRad.set(-Math.PI);
                c.servoMaxAngleRad.set(Math.PI);
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
                c.servoMinAngleRad.set(-Math.PI);
                c.servoMaxAngleRad.set(Math.PI);
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
                c.servoMinAngleRad.set(-Math.PI);
                c.servoMaxAngleRad.set(Math.PI);
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
                c.servoMinAngleRad.set(-Math.PI);
                c.servoMaxAngleRad.set(Math.PI);
                c.analogMinVoltage.set(0.017);
                c.analogMaxVoltage.set(3.178);
                c.angleOffsetRad.set(0.0);
                c.podOffset.set(Vector2D.cartesian(-dtLength, dtWidth));
                c.encoderReversed.set(Boolean.FALSE);
            }
    );

    public static double globalSpeedScale = 0.5; // Full match speed for autonomous & teleop

    public static Drivetrain createDrivetrain(HardwareMap hardwareMap) {
        Swerve swerve = new Swerve(
                hardwareMap,
                driveConfig,
                new CoaxialServoPod(hardwareMap, leftFront),
                new CoaxialServoPod(hardwareMap, rightFront),
                new CoaxialServoPod(hardwareMap, leftBack),
                new CoaxialServoPod(hardwareMap, rightBack)
        );
        return new Drivetrain() {
            @Override
            public void drive(DrivePowers powers, boolean manual) {
                swerve.drive(new DrivePowers(
                        powers.forward() * globalSpeedScale,
                        powers.strafe() * globalSpeedScale,
                        -powers.turn() * globalSpeedScale
                ), manual);
            }

            @Override
            public double maxScaling(DrivePowers current, DrivePowers delta) {
                return swerve.maxScaling(
                        new DrivePowers(current.forward() * globalSpeedScale, current.strafe() * globalSpeedScale, -current.turn() * globalSpeedScale),
                        new DrivePowers(delta.forward() * globalSpeedScale, delta.strafe() * globalSpeedScale, -delta.turn() * globalSpeedScale)
                );
            }

            @Override
            public void stop() {
                swerve.stop();
            }

            @Override
            public void stop(boolean brake) {
                swerve.stop(brake);
            }

            @Override
            public Map<String, Object> debug() {
                return swerve.debug();
            }

            @Override
            public double interpolateVelocity(double xRadius, double yRadius, double theta) {
                return swerve.interpolateVelocity(xRadius, yRadius, theta);
            }
        };
    }

    public static Follower create(HardwareMap hardwareMap) {
        return new Follower(new PinpointLocalizer(hardwareMap, localizerConfig), createDrivetrain(hardwareMap), new Foresight(foresightConfig));

    }

}