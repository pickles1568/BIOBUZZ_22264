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

@Autonomous(name = "Red Auto", group = "Autonomous")
public class RedAuto extends LinearOpMode {

    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(56, 9, 90);
    private final Pose pollen1 = poseFactory.of(56, 23, 90);
    private final Pose alignStart = poseFactory.of(56, 23, 90);
    private final Pose align = poseFactory.of(38.75, 9.75, 180);
    private final Pose intake = poseFactory.of(9, 9, 180);
    private final Pose pollen2Start = poseFactory.of(9, 9, 180);
    private final Pose pollen2 = poseFactory.of(56, 23, 90);
    private final Pose park = poseFactory.of(13.75, 91.75, 90);
    private final Pose parkControl1 = poseFactory.of(10.75, 40.75, 0);

    private Robot robot;

    // Autonomous routine
    public Command autoRoutine() {
        return sequential(
                follow(follower, pollen1()),
                robot.ShootForMoment,
                follow(follower, align()),
                robot.IntakeManager.StartCollecting,
                follow(follower, intake()),
                robot.IntakeManager.StopCollecting,
                follow(follower, pollen2()),
                robot.ShootForMoment,
                follow(follower, park())
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

            robot.update();

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
