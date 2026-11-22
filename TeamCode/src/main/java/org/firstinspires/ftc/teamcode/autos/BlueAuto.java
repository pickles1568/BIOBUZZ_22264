package org.firstinspires.ftc.teamcode.autos;

import static com.pedropathing.api.Paths.*;
import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "Bad Blue Auto", group = "Autonomous")
public class BlueAuto extends LinearOpMode {

    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(85.75, 130.75, 270);
    private final Pose pollen1 = poseFactory.of(85.75, 100.75, 270);
    private final Pose alignStart = poseFactory.of(85.75, 100.75, 270);
    private final Pose align = poseFactory.of(100.75, 132.75, 0);
    private final Pose intake = poseFactory.of(132.75, 132.75, 0);
    private final Pose pollen2Start = poseFactory.of(132.75, 132.75, 0);
    private final Pose pollen2 = poseFactory.of(85.75, 100.75, 270);
    private final Pose park = poseFactory.of(125.75, 49.75, 270);
    private final Pose parkControl1 = poseFactory.of(138.75, 110.75, 0);

    private Robot robot;

    // Autonomous routine
    public Command autoRoutine() {
        return sequential(
                robot.ShootForMoment,

                follow(follower, pollen1()),
                waitMs((long) 500),
                follow(follower, align()),
                waitMs((long) 500),

                robot.IntakeManager.StartCollecting,
                follow(follower, intake()),
                waitMs((long) 500),
                robot.IntakeManager.StopCollecting,

                follow(follower, pollen2()),
                waitMs((long) 500),
                robot.ShootForMoment,

                follow(follower, park()),
                waitMs((long) 500)
        );
    }

    @Override
    public void runOpMode() {
        robot = new Robot(hardwareMap, true);

        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();
        robot.update();

        waitForStart();
        schedule(autoRoutine());

        while (opModeIsActive()) {
            follower.update();
            Scheduler.execute();

            telemetry.addData("x", follower.pose().x());
            telemetry.addData("y", follower.pose().y());
            telemetry.addData("heading", follower.pose().heading());

            if (follower.currentPath() != null) {
                telemetry.addData("Current path distance remaining", follower.distanceToEndpoint());
                telemetry.addData("Path number", follower.pathIndex());
            }

            telemetry.update();
        }
    }

    public Path pollen1() {
        return Paths.line(start, pollen1).linear(start, pollen1);
    }

    public Path align() {
        return Paths.line(alignStart, align).linear(alignStart, align);
    }

    public Path intake() {
        return Paths.line(align, intake).constant(intake);
    }

    public Path pollen2() {
        return Paths.line(pollen2Start, pollen2).linear(pollen2Start, pollen2);
    }

    public Path park() {
        return Paths.curve(pollen2, parkControl1, park).constant(park);
    }
}
