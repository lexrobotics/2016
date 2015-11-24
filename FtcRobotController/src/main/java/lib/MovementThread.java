package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * Created by luke on 10/28/15.
 */
public class MovementThread implements Runnable {

    private double expectedHeading, actualHeading;
    private DriveTrain drivetrain;
    private String gyro_name;
    private double power;
    private double minthresh;
    private double turnthresh;
    private LinearOpMode waiter;

    // Must always be less than 1
    private double scalingfactor;


    public MovementThread (DriveTrain drivetrain, String gyro_name, int expectedHeading, LinearOpMode waiter, double power) {
        this.drivetrain = drivetrain;
        this.gyro_name = gyro_name;
        this.power = power;
        this.waiter = waiter;
    }

    public synchronized void setPower(double power){
        this.power = power;
    }

    public int distToZero(int angle1){
        if(angle1>180){
            return 360 - angle1;
        }
        else{
            return angle1;
        }
    }

    public int angleDist(int deg1, int deg2)
    {
        int absDist = (360 + deg2 - deg1) % 360;
        if (absDist > 180)
            absDist -= 360;
        return absDist;
    }

    @Override
    public void run() {
        double motorPower;
        synchronized(this) {
            motorPower = this.power;
        }
        drivetrain.setLeftMotors(motorPower);
        drivetrain.setRightMotors(motorPower);

        while (!Thread.currentThread().isInterrupted() && waiter.opModeIsActive()) {
            synchronized(this) {
                motorPower = this.power;
            }

            try {
                actualHeading = Robot.state.getSensorReading(gyro_name);
                int offset = angleDist((int)actualHeading, (int)expectedHeading);

                if (Math.abs(offset) > minthresh && Math.abs(offset) < turnthresh) {
                    if (offset > 0) {
                        drivetrain.setLeftMotors(motorPower);
                        drivetrain.setRightMotors(motorPower / Math.cos(offset));
                    }
                    else {
                        drivetrain.setLeftMotors(motorPower / Math.cos(offset));
                        drivetrain.setRightMotors(motorPower);
                    }
                }
                else if (Math.abs(offset) > turnthresh) {
                    while (Math.abs(actualHeading - expectedHeading) > minthresh) {
                        drivetrain.setLeftMotors(motorPower * Math.signum(expectedHeading - actualHeading) * -1);
                        drivetrain.setRightMotors(motorPower * Math.signum(expectedHeading - actualHeading));
                    }
                }
                else {
                    drivetrain.setLeftMotors(motorPower);
                    drivetrain.setRightMotors(motorPower);
                }
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        drivetrain.setLeftMotors(0);
        drivetrain.setRightMotors(0);
    }
}