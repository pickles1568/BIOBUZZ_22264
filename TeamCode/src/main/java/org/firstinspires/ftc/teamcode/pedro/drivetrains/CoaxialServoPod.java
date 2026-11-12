package org.firstinspires.ftc.teamcode.pedro.drivetrains;

import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.SwervePod;
import com.pedropathing.utils.Angle;
import com.pedropathing.utils.Utils;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.slf4j.helpers.Util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CoaxialServoPod implements SwervePod {

    //All functions in this are to meet SwervePod requirements.

    //Variable Declaration
    private final String name;
    private final Servo turnServo;
    private final DcMotorEx driveMotor;
    private final AnalogInput turnEncoder;
    private final CoaxialServoPodConfig config;
    private double lastDrivePower = 0.0;
    private double lastServoPosition = -1.0;

    public CoaxialServoPod(HardwareMap hardwareMap, CoaxialServoPodConfig config) {
        this.config = config;
        this.name = config.name.get();
        this.driveMotor = hardwareMap.get(DcMotorEx.class, config.motorName.get());
        this.turnServo = hardwareMap.get(Servo.class, config.servoName.get());
        String encoderName = config.servoEncoderName.get();
        this.turnEncoder = createAnalogEncoder(hardwareMap, encoderName);
        setToFloat();
        driveMotor.setDirection(config.driveDirection.get());
        turnServo.setDirection(config.servoDirection.get());
        //Immediately align turn servo straight
        alignToZero();
    }

    public void alignToZero() {
        //Lot of math to just set position to zero
        double minPos = config.servoMinPosition.get();
        double maxPos = config.servoMaxPosition.get();
        double minAngle = config.servoMinAngleRad.get();
        double maxAngle = config.servoMaxAngleRad.get();

        double target0 = Angle.normalize(0.0 + config.angleOffsetRad.get());
        double norm0 = Utils.clamp((target0 - minAngle) / (maxAngle - minAngle), 0.0, 1.0);
        double initPos = minPos + norm0 * (maxPos - minPos);
        lastServoPosition = Utils.clamp(initPos, minPos, maxPos);
        turnServo.setPosition(lastServoPosition);
    }

    private static AnalogInput createAnalogEncoder(HardwareMap hardwareMap, String encoderName) {
        if (encoderName != null && !encoderName.trim().isEmpty()) {
            try {
                return hardwareMap.get(AnalogInput.class, encoderName.trim());
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }

    public boolean hasEncoder() {
        return turnEncoder !=  null;
    }

    public double getVoltage() {
        return turnEncoder != null ? turnEncoder.getVoltage() : -1.0;
    }

    public void setServoPosition(double position) {
        lastServoPosition = Utils.clamp(position, 0.0, 1.0);
        turnServo.setPosition(lastServoPosition);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Vector2D getOffset() {
        return config.podOffset.get();
    }

    public double getRawAngleRad() {
        //returns the raw angle in radians
        if (turnEncoder != null) {
            double v = turnEncoder.getVoltage();
            double minV = config.analogMinVoltage.get();
            double maxV = config.analogMaxVoltage.get();
            double range = maxV - minV;
            if (range <= 0) return 0.0;
            double normalized =  Utils. clamp((v - minV) / range, 0.0, 1.0);
            return normalized * (2.0 * Math.PI);
        } else {
            double minPos = config.servoMinPosition.get();
            double maxPos = config.servoMaxPosition.get();
            double minAngle = config.servoMinAngleRad.get();
            double maxAngle = config.servoMaxAngleRad.get();
            double currentPos = (lastServoPosition >= 0) ? lastServoPosition : turnServo.getPosition();
            double posRatio =  (currentPos - minPos) / (maxPos - minPos);
            double clampedRatio = Utils.clamp(posRatio, 0.0, 1.0);
            return minAngle + clampedRatio * (maxAngle - minAngle);
        }
    }

    @Override
    public double getAngle() {
        return getAngleAfterOffsetRad();
    }

    public double getAngleAfterOffsetRad() {
        return getRawAngleRad() - config.angleOffsetRad.get();
    }

    @Override
    public double adjustThetaForEncoder(double wheelTheta) {
        boolean encoderReversed = config.encoderReversed.get();
        double t = encoderReversed ? (Math.PI / 2.0 - wheelTheta) : (wheelTheta - Math.PI / 2.0);
        return Angle.normalize(t);
    }

    @Override
    public void move(double targetAngleRad, double drivePower, boolean ignoreAngleChanges) {
        boolean encoderReversed = config.encoderReversed.get();

        //Define Servo Bounds and current position
        double minPos = config.servoMinPosition.get();
        double maxPos = config.servoMaxPosition.get();
        double minAngle = config.servoMinAngleRad.get();
        double maxAngle = config.servoMaxAngleRad.get();

        double currentPos = (lastServoPosition >= 0) ? lastServoPosition : turnServo.getPosition();
        double clampedCurrentPos = Utils.clamp(currentPos, minPos, maxPos);

        //Compute possible target angles(1 is positive and 2 is negative)
        double baseTargetRad = encoderReversed ? (Math.PI / 2.0 - targetAngleRad) : (targetAngleRad - Math.PI / 2.0);
        double target1 = Angle.normalize(baseTargetRad + config.angleOffsetRad.get());
        double target2 = Angle.normalize(target1 + Math.PI);

        //Map both to servo limits
        double norm1 = Utils.clamp((target1 - minAngle) / (maxAngle - minAngle), 0.0, 1.0);
        double norm2 = Utils.clamp((target2 - minAngle) / (maxAngle - minAngle), 0.0, 1.0);

        double pos1 = minPos + norm1 * (maxPos - minPos);
        double pos2 = minPos + norm2 * (maxPos - minPos);

        //Measure distance
        double dist1 = Math.abs(pos1 - clampedCurrentPos);
        double dist2 = Math.abs(pos2 - clampedCurrentPos);

        //Select candidate
        double targetPos;
        double finalDrivePower;

        if (dist1 <= dist2) {
            targetPos = pos1;
            finalDrivePower = drivePower;
        } else {
            targetPos = pos2;
            finalDrivePower = -drivePower;
        }

        //Update hardware with caching thresholds
        double servoCachingThreshold = config.servoCachingThreshold.get();
        double motorCachingThreshold = config.motorCachingThreshold.get();

        if (!ignoreAngleChanges) {
            if (lastServoPosition < 0 || Math.abs(targetPos - lastServoPosition) > servoCachingThreshold) {
                lastServoPosition = targetPos;
                turnServo.setPosition(targetPos);
            }
        }
        if (Math.abs(finalDrivePower - lastDrivePower) > motorCachingThreshold || (finalDrivePower == 0 && lastDrivePower !=0)) {
            lastDrivePower = finalDrivePower;
            driveMotor.setPower(finalDrivePower);
        }



    }

    @Override
    public void setToFloat() {
        driveMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    @Override
    public void setToBreak() {
        driveMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public Map<String, Object> debug() {
        Map<String, Object> map = new HashMap<>();
        map.put("servoName", config.servoName.get());
        map.put("rawAngleRad", getRawAngleRad());
        map.put("angleAfterOffsetRad", getAngleAfterOffsetRad());
        map.put("servoPosition", turnServo.getPosition());
        map.put("drivePower", driveMotor.getPower());
        return map;
    }
}
