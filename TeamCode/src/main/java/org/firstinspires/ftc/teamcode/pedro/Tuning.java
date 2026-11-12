package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.firstinspires.ftc.teamcode.pedro.procedures.PinpointTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.Tests;

public class Tuning {
    @Tuner
    public static Procedure tests() {
        return new Tests(
                Constants::createDrivetrain,
                hardwareMap -> new PinpointLocalizer(hardwareMap, Constants.localizerConfig),
                null
        );
    }

    @Tuner
    public static Procedure pinpointTuner() {
        return new PinpointTuner();
    }
}
