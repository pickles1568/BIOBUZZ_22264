package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.CoaxialPodConfig;
import com.pedropathing.revhub.drivetrains.SwerveConfig;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants {
    public static Follower create(HardwareMap h) {
        // return new Follower(Drivetrain, Localizer, Foresight);
        return null;
    }

    //DT Defintions
    static double dtWidth = 438.35;
    static double dtlength = 402.00;

    public static SwerveConfig driveConfig = new SwerveConfig(
            c -> {
                c.zeroPowerBehavior.set(SwerveConfig.ZeroPowerBehavior.IGNORE_ANGLE_CHANGES);
                c.manualBrakeMode.set(true);
                c.voltageCompensation.set(false);
            }
    );

    public static CoaxialPodConfig rightBack = new CoaxialPodConfig(
            c -> {
                c.name.set("rightBack");
                c.motorName.set("rb"); // Port 0
                c.servoName.set("rbTurn");
                c.servoEncoderName.set("rbTurnEncoder");
                c.turnController.set(Controller.pid(0.3, 0, 0.005)
                        .plus(Controller.proportionalFeedforward(0)));
                c.driveDirection.set(DcMotorSimple.Direction.FORWARD);
                c.servoDirection.set(DcMotorSimple.Direction.FORWARD);
                c.analogMinVoltage.set(0.018);
                c.analogMaxVoltage.set(3.19);

                c.angleOffsetRad.set(4.143);
                c.podOffset.set(Vector2D.cartesian(-dtlength, dtWidth));
                c.encoderReversed.set(Boolean.TRUE);


            }
    );

    public static CoaxialPodConfig leftFront = new CoaxialPodConfig(
            c -> {
                c.name.set("leftFront");
                c.motorName.set("lf"); // Port 1
                c.servoName.set("lfTurn");
                c.servoEncoderName.set("lfTurnEncoder");
                c.turnController.set(Controller.pid(0.3, 0, 0.005)
                        .plus(Controller.proportionalFeedforward(0)));
                c.driveDirection.set(DcMotorSimple.Direction.FORWARD);
                c.servoDirection.set(DcMotorSimple.Direction.FORWARD);
                c.analogMinVoltage.set(0.019);
                c.analogMaxVoltage.set(3.21);

                c.angleOffsetRad.set(0.935);
                c.podOffset.set(Vector2D.cartesian(dtlength, dtWidth));
                c.encoderReversed.set(Boolean.TRUE);
            }
    );

    public static CoaxialPodConfig rightFront = new CoaxialPodConfig(
            c -> {
                c.name.set("rightFront");
                c.motorName.set("rf"); // Port 2
                c.servoName.set("rfTurn");
                c.servoEncoderName.set("rfTurnEncoder");
                c.turnController.set(Controller.pid(0.3, 0, 0.005)
                        .plus(Controller.proportionalFeedforward(0)));
                c.driveDirection.set(DcMotorSimple.Direction.FORWARD);
                c.servoDirection.set(DcMotorSimple.Direction.FORWARD);
                c.analogMinVoltage.set(0.014);
                c.analogMaxVoltage.set(3.164);

                c.angleOffsetRad.set(4.135);
                c.podOffset.set(Vector2D.cartesian(dtlength, -dtWidth));
                c.encoderReversed.set(Boolean.TRUE);
            }
    );

    public static CoaxialPodConfig leftBack = new CoaxialPodConfig(
            c -> {
                c.name.set("leftBack");
                c.motorName.set("lb"); // Port 3
                c.servoName.set("lbTurn");
                c.servoEncoderName.set("lbTurnEncoder");
                c.turnController.set(Controller.pid(0.3, 0, 0.0086)
                        .plus(Controller.proportionalFeedforward(0)));
                c.driveDirection.set(DcMotorSimple.Direction.FORWARD);
                c.servoDirection.set(DcMotorSimple.Direction.FORWARD);
                c.analogMinVoltage.set(0.017);
                c.analogMaxVoltage.set(3.178);

                c.angleOffsetRad.set(2.914);
                c.podOffset.set(Vector2D.cartesian(-dtlength, dtWidth));
                c.encoderReversed.set(Boolean.TRUE);
            }
    );

}