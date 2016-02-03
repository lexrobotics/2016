package simplelib.helper;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;

import simplelib.SimpleRobot;

/**
 * Movement Thread Rewritten
 */

public class MoveCorrect implements Runnable {

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

    public double angleDist(double deg1, double deg2)
    {
        double absDist = (360 + deg2 - deg1) % 360;
        if (absDist > 180)
            absDist -= 360;
        return absDist;
    }

    public void run()
    {
        PID correctionPID = new PID(1, 0, 0);
        double maxOutput = Math.min(1 - Math.abs(power), Math.abs(power));
        correctionPID.setMaxOutput(maxOutput);
        correctionPID.setMinOutput(-1 * maxOutput);

        double offset;
        while (!Thread.currentThread().isInterrupted() && SimpleRobot.opm.opModeIsActive()) {

            try {

                offset = angleDist(SimpleRobot.getActualHeading(), SimpleRobot.getExpectedHeading());
                SimpleRobot.tel.addData("Current Offset", offset);

                if (Math.abs(offset) > minthresh && Math.abs(offset) < maxthresh) {
                    double correction = correctionPID.updateWithError(offset);
                    SimpleRobot.drivetrain.setLeftMotors(power + correction);
                    SimpleRobot.drivetrain.setRightMotors(power - correction);
                }

                else if (Math.abs(offset) > maxthresh) {

                    Thread.sleep(200);

                    while (Math.abs(offset) > maxthresh && SimpleRobot.opm.opModeIsActive()) {
                        offset = angleDist(SimpleRobot.getActualHeading(), SimpleRobot.getExpectedHeading());
                        SimpleRobot.drivetrain.setRightMotors(0.4 * Math.signum(offset) * -1);
                        SimpleRobot.drivetrain.setLeftMotors(0.4 * Math.signum(offset));

                        Thread.sleep(1);
                    }

                    Thread.sleep(200);
                }

                else {
                    SimpleRobot.drivetrain.setLeftMotors(power);
                    SimpleRobot.drivetrain.setRightMotors(power);
                }

            } catch (InterruptedException ex) {
                SimpleRobot.drivetrain.setLeftMotors(0);
                SimpleRobot.drivetrain.setRightMotors(0);
                Thread.currentThread().interrupt();
                break;
            }
        }

        SimpleRobot.drivetrain.setLeftMotors(0);
        SimpleRobot.drivetrain.setRightMotors(0);
    }
}