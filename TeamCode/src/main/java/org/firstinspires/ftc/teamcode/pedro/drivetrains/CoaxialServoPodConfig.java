package org.firstinspires.ftc.teamcode.pedro.drivetrains;
import static com.pedropathing.config.Validator.nonnegative;
import com.pedropathing.config.ConfigVar;
import com.pedropathing.config.Configuration;
import com.pedropathing.math.Vector2D;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

//This File defines all requirements for a swerve pod.

public class CoaxialServoPodConfig {
    //Pod name and hardware names
    public final ConfigVar<String> name = ConfigVar.required();
    public final ConfigVar<String> motorName = ConfigVar.required();
    public final ConfigVar<String> servoName = ConfigVar.required();
    public final ConfigVar<String> servoEncoderName = ConfigVar.of("");

    //Hardware Directions and offsets
    public final ConfigVar<DcMotorSimple.Direction> driveDirection = ConfigVar.required();
    public final ConfigVar<Servo.Direction> servoDirection = ConfigVar.required();
    public final ConfigVar<Double> angleOffsetRad = ConfigVar.of(0.0);
    public final ConfigVar<Vector2D> podOffset = ConfigVar.required();

    //Analog Voltage(not used) and direction
    public final ConfigVar<Double> analogMinVoltage = ConfigVar.of(0.0, nonnegative());
    public final ConfigVar<Double> analogMaxVoltage = ConfigVar.of(0.0, nonnegative());
    public final ConfigVar<Boolean> encoderReversed = ConfigVar.of(false);

    //Servo Target Bounds & how they map to angles
    public final ConfigVar<Double> servoMinPosition = ConfigVar.of(0.0);
    public final ConfigVar<Double> servoMaxPosition = ConfigVar.of(0.0);
    public final ConfigVar<Double> servoMinAngleRad = ConfigVar.of(0.0);
    public final ConfigVar<Double> servoMaxAngleRad = ConfigVar.of(2.0 * Math.PI);

    //Servo and Motor caching levels
    public final ConfigVar<Double> motorCachingThreshold = ConfigVar.of(0.02, nonnegative());
    public final ConfigVar<Double> servoCachingThreshold = ConfigVar.of(0.005, nonnegative());

    //Object Creation
    public CoaxialServoPodConfig(Configuration<CoaxialServoPodConfig> config) {
        config.configure(this);
    }

}
