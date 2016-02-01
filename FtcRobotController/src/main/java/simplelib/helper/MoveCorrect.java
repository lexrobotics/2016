package simplelib.helper;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;

import simplelib.SimpleRobot;

/**
 * Movement Thread Rewritten
 *
 * Written by Vivek Bhupatiraju & Luke West
 */

public class MoveCorrect implements Runnable {

    // change < minthresh, no change
    // minthresh <= change <= maxthresh, small turn
    // maxthresh < change, big turn
    private double minthresh, maxthresh;
    private double power, scalar;

    public MoveCorrect(double power) {
        this.power = power;
        this.scalar = 0.1;
        this.minthresh = 1;
        this.maxthresh = 10;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public int angleDist(int deg1, int deg2)
    {
        int absDist = (360 + deg2 - deg1) % 360;
        if (absDist > 180)
            absDist -= 360;
        return absDist;
    }

    public void run()
    {
        SimpleRobot.drivetrain.setLeftMotors(power);
        SimpleRobot.drivetrain.setRightMotors(power);

        while (!Thread.currentThread().isInterrupted() && SimpleRobot.opm.opModeIsActive()) {

        }

    }
}