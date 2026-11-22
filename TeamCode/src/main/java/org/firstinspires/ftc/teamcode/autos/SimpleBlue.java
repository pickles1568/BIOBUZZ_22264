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

@Autonomous(name = "SimpleBlue", group = "Autonomous")
public class SimpleBlue extends LinearOpMode {

    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(55.75, 7.75, 180);
    private final Pose shootAndGo = poseFactory.of(8.75, 7.75, 180);
    private final Pose goBackFromIntakeStart = poseFactory.of(8.75, 7.75, 180);
    private final Pose goBackFromIntake = poseFactory.of(55.75, 7.75, 180);
    private final Pose parkPt1Start = poseFactory.of(55.75, 7.75, 180);
    private final Pose parkPt1 = poseFactory.of(55.75, 22.75, 180);
    private final Pose parkPt2Start = poseFactory.of(55.75, 22.75, 180);
    private final Pose parkPt2 = poseFactory.of(15.75, 22.75, 180);
    private final Pose parkPt1_2 = poseFactory.of(16.75, 89.75, 90);
    private Robot robot;

    // Autonomous routine
    public Command autoRoutine() {
        return sequential(
                robot.ShootForMoment,

                follow(follower, shootAndGo()),
                robot.IntakeManager.StartCollecting,
                waitMs((long) 500),

                follow(follower, goBackFromIntake()),
                robot.IntakeManager.StopCollecting,
                waitMs((long) 500),

                follow(follower, parkPt1()),
                waitMs((long) 500),
                follow(follower, parkPt2()),
                waitMs((long) 500),
                follow(follower, parkPt1_2()),
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
        //robot.update();

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


    public Path shootAndGo() {
        return Paths.line(start, shootAndGo).linear(start, shootAndGo);
    }

    public Path goBackFromIntake() {
        return Paths.line(goBackFromIntakeStart, goBackFromIntake).linear(goBackFromIntakeStart, goBackFromIntake);
    }

    public Path parkPt1() {
        return Paths.line(parkPt1Start, parkPt1).linear(parkPt1Start, parkPt1);
    }

    public Path parkPt2() {
        return Paths.line(parkPt2Start, parkPt2).linear(parkPt2Start, parkPt2);
    }

    public Path parkPt1_2() {
        return Paths.line(parkPt2, parkPt1_2).constant(parkPt1_2);
    }
}
