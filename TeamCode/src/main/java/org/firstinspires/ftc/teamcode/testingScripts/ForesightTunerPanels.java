package org.firstinspires.ftc.teamcode.testingScripts;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Vector2D;
import com.pedropathing.paths.Path;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.helpers.Button;
import org.firstinspires.ftc.teamcode.pedro.Constants;
@Disabled
@TeleOp(name = "Foresight Tuner (Interactive)", group = "2 - Testing")
public class ForesightTunerPanels extends LinearOpMode {

    // Tunable Parameters
    public double primaryTranslationalForward = 0.0;
    public double secondaryTranslationalForward = 0.0;
    public double primaryTranslationalLateral = 0.0;
    public double secondaryTranslationalLateral = 0.0;
    public double coastkV = 0.0;
    public double brakekV = 0.0;
    public double headingKP = 2.2;
    public double headingBrakeLinear = 0.0564;
    public double headingBrakeQuadratic = 0.0458;
    public double forwardLinearBrake = 0.0193;
    public double strafeLinearBrake = 0.0714;
    public double forwardQuadraticBrake = 0.0022;
    public double strafeQuadraticBrake = 0.0010;
    public double maxForwardVel = 25.0; // Gentle default speed (in/s)
    public double maxStrafeVel = 25.0;
    public double naturalForwardDecel = 30.0;
    public double naturalStrafeDecel = 30.0;
    public double testDistance = 48.0;
    public boolean holdAtEndpoint = false;
    public double globalSpeedScale = Constants.globalSpeedScale;

    private int selectedParamIndex = 0;
    private final String[] paramNames = {
            "globalSpeedScale", "coastkV", "brakekV", "headingKP",
            "maxForwardVel", "maxStrafeVel",
            "primaryTranslationalForward", "secondaryTranslationalForward",
            "primaryTranslationalLateral", "secondaryTranslationalLateral",
            "forwardLinearBrake", "forwardQuadraticBrake"
    };

    private double currentStepSize = 0.001;

    enum RobotMode {
        TESTING,
        MANUAL_DRIVE
    }
    private RobotMode mode = RobotMode.TESTING;

    @Override
    public void runOpMode() throws InterruptedException {
        Follower follower = new Follower(
                new PinpointLocalizer(hardwareMap, Constants.localizerConfig),
                Constants.createDrivetrain(hardwareMap),
                buildForesight()
        );

        follower.setPose(Pose.zero());

        Button btnA = new Button(() -> gamepad1.a);
        Button btnB = new Button(() -> gamepad1.b);
        Button btnX = new Button(() -> gamepad1.x);
        Button btnY = new Button(() -> gamepad1.y);
        Button btnBack = new Button(() -> gamepad1.back || gamepad1.options);
        Button dpadUp = new Button(() -> gamepad1.dpad_up);
        Button dpadDown = new Button(() -> gamepad1.dpad_down);
        Button dpadLeft = new Button(() -> gamepad1.dpad_left);
        Button dpadRight = new Button(() -> gamepad1.dpad_right);

        boolean goingForward = true;
        Path currentTestPath = null;
        String activeTestName = "IDLE";

        telemetry.addLine("Foresight Tuner (Interactive) Initialized.");
        telemetry.update();

        waitForStart();
        follower.setPose(Pose.zero());

        while (opModeIsActive()) {
            follower.setAlgorithm(buildForesight());

            // Toggle Mode via Back/Options button
            if (btnBack.wasJustPressed()) {
                if (mode == RobotMode.TESTING) {
                    mode = RobotMode.MANUAL_DRIVE;
                    follower.hold(follower.pose());
                } else {
                    mode = RobotMode.TESTING;
                }
            }

            // Step size adjustment via Triggers
            if (gamepad1.right_trigger > 0.5) {
                if (currentStepSize < 1.0) currentStepSize *= 10.0;
                sleep(250); // debounce
            }
            if (gamepad1.left_trigger > 0.5) {
                if (currentStepSize > 0.00001) currentStepSize /= 10.0;
                sleep(250); // debounce
            }

            // Parameter selection via DPad Left/Right
            if (dpadRight.wasJustPressed()) {
                selectedParamIndex = (selectedParamIndex + 1) % paramNames.length;
            }
            if (dpadLeft.wasJustPressed()) {
                selectedParamIndex = (selectedParamIndex - 1 + paramNames.length) % paramNames.length;
            }

            // Parameter adjustment via DPad Up/Down
            if (dpadUp.wasJustPressed()) {
                adjustParam(paramNames[selectedParamIndex], currentStepSize);
            }
            if (dpadDown.wasJustPressed()) {
                adjustParam(paramNames[selectedParamIndex], -currentStepSize);
            }

            if (mode == RobotMode.MANUAL_DRIVE) {
                activeTestName = "MANUAL DRIVE MODE";
                double forward = -gamepad1.left_stick_y;
                double lateral = gamepad1.left_stick_x;
                double turn = gamepad1.right_stick_x;
                follower.manual(new DrivePowers(forward, lateral, turn));
            } else {
                // TESTING MODE
                if (btnA.wasJustPressed()) {
                    activeTestName = "LINE";
                    goingForward = true;
                    currentTestPath = line(Pose.zero(), new Pose(testDistance, 0, 0)).constant(0);
                    follower.follow(currentTestPath);
                }

                if (btnB.wasJustPressed()) {
                    activeTestName = "CURVE";
                    goingForward = true;
                    currentTestPath = curve(
                            Pose.zero(),
                            new Pose(testDistance, 0),
                            new Pose(testDistance, testDistance)
                    ).tangent();
                    follower.follow(currentTestPath);
                }

                if (btnX.wasJustPressed()) {
                    activeTestName = "HOLD";
                    follower.hold(Pose.zero());
                }

                if (btnY.wasJustPressed()) {
                    activeTestName = "IDLE / RESET";
                    follower.setPose(Pose.zero());
                    follower.hold(Pose.zero());
                }

                if (follower.atParametricEnd()) {
                    if (holdAtEndpoint) {
                        if (currentTestPath != null) {
                            follower.hold(currentTestPath.endPose());
                        } else {
                            follower.hold(Pose.zero());
                        }
                        activeTestName = activeTestName + " (HOLDING)";
                    } else {
                        if ("LINE".equals(activeTestName)) {
                            if (goingForward) {
                                currentTestPath = line(new Pose(testDistance, 0, 0), Pose.zero()).constant(0);
                            } else {
                                currentTestPath = line(Pose.zero(), new Pose(testDistance, 0, 0)).constant(0);
                            }
                            goingForward = !goingForward;
                            follower.follow(currentTestPath);
                        } else if ("CURVE".equals(activeTestName)) {
                            if (goingForward) {
                                currentTestPath = curve(
                                        new Pose(testDistance, testDistance),
                                        new Pose(testDistance, 0),
                                        Pose.zero()
                                ).tangent();
                            } else {
                                currentTestPath = curve(
                                        Pose.zero(),
                                        new Pose(testDistance, 0),
                                        new Pose(testDistance, testDistance)
                                ).tangent();
                            }
                            goingForward = !goingForward;
                            follower.follow(currentTestPath);
                        }
                    }
                }
            }

            follower.update();

            // Telemetry
            telemetry.addData("Mode [Back/Select to Toggle]", mode);
            telemetry.addData("Active Test / State", activeTestName);
            telemetry.addData("Pose", follower.pose());
            telemetry.addData("--- TUNING [Step Size: %.5f] ---", currentStepSize);
            telemetry.addData("Controls", "DPad Lt/Rt=Select, Up/Dn=Modify, Lt/Rt Triggers=Change Step Size");
            for (String p : paramNames) {
                boolean selected = p.equals(paramNames[selectedParamIndex]);
                telemetry.addData((selected ? "> " : "  ") + p, "%.5f", getParam(p));
            }
            telemetry.update();
        }
    }

    private void adjustParam(String name, double delta) {
        switch (name) {
            case "globalSpeedScale":
                globalSpeedScale = Math.max(0.05, Math.min(1.0, globalSpeedScale + delta)); 
                Constants.globalSpeedScale = globalSpeedScale; 
                break;
            case "coastkV": coastkV = Math.max(0, coastkV + delta); break;
            case "brakekV": brakekV = Math.max(0, brakekV + delta); break;
            case "headingKP": headingKP = Math.max(0, headingKP + delta); break;
            case "maxForwardVel": maxForwardVel = Math.max(1, maxForwardVel + delta * 10); break;
            case "maxStrafeVel": maxStrafeVel = Math.max(1, maxStrafeVel + delta * 10); break;
            case "primaryTranslationalForward": primaryTranslationalForward = Math.max(0, primaryTranslationalForward + delta); break;
            case "secondaryTranslationalForward": secondaryTranslationalForward = Math.max(0, secondaryTranslationalForward + delta); break;
            case "primaryTranslationalLateral": primaryTranslationalLateral = Math.max(0, primaryTranslationalLateral + delta); break;
            case "secondaryTranslationalLateral": secondaryTranslationalLateral = Math.max(0, secondaryTranslationalLateral + delta); break;
            case "forwardLinearBrake": forwardLinearBrake = Math.max(0, forwardLinearBrake + delta); break;
            case "forwardQuadraticBrake": forwardQuadraticBrake = Math.max(0, forwardQuadraticBrake + delta); break;
        }
    }

    private double getParam(String name) {
        switch (name) {
            case "globalSpeedScale": return globalSpeedScale;
            case "coastkV": return coastkV;
            case "brakekV": return brakekV;
            case "headingKP": return headingKP;
            case "maxForwardVel": return maxForwardVel;
            case "maxStrafeVel": return maxStrafeVel;
            case "primaryTranslationalForward": return primaryTranslationalForward;
            case "secondaryTranslationalForward": return secondaryTranslationalForward;
            case "primaryTranslationalLateral": return primaryTranslationalLateral;
            case "secondaryTranslationalLateral": return secondaryTranslationalLateral;
            case "forwardLinearBrake": return forwardLinearBrake;
            case "forwardQuadraticBrake": return forwardQuadraticBrake;
            default: return 0;
        }
    }

    private Foresight buildForesight() {
        ForesightConfig config = new ForesightConfig(c -> {
            Controller primaryForward = Controller.proportional(primaryTranslationalForward);
            Controller secondaryForward = Controller.proportional(secondaryTranslationalForward);
            Controller primaryLateral = Controller.proportional(primaryTranslationalLateral);
            Controller secondaryLateral = Controller.proportional(secondaryTranslationalLateral);

            c.forwardTranslational.set(Controller.piecewise(secondaryForward).put(2.5, primaryForward));
            c.strafeTranslational.set(Controller.piecewise(secondaryLateral).put(2.5, primaryLateral));

            c.coast.set(Controller.proportionalFeedforward(coastkV));
            c.brake.set(Controller.proportionalFeedforward(brakekV));

            c.headingFeedback.set(Controller.proportional(headingKP));
            c.headingBrakeCoefficients.set(Vector2D.cartesian(headingBrakeLinear, headingBrakeQuadratic));

            c.linearBrakeCoefficients.set(Matrix.diag(forwardLinearBrake, strafeLinearBrake));
            c.quadraticBrakeCoefficients.set(Matrix.diag(forwardQuadraticBrake, strafeQuadraticBrake));

            c.maxAchievableForwardVelocity.set(maxForwardVel);
            c.maxAchievableStrafeVelocity.set(maxStrafeVel);
            c.naturalForwardDeceleration.set(naturalForwardDecel);
            c.naturalStrafeDeceleration.set(naturalStrafeDecel);
        });

        return new Foresight(config);
    }
}
