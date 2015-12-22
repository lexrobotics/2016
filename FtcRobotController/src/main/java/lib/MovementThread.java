package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * Created by luke on 10/28/15.
 */
public class MovementThread implements Runnable {

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
        minthresh = 1;
        turnthresh = 10;
    }

    public void setPower(double power){
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
                int offset = angleDist((int)drivetrain.getActualHeading(gyro_name),
                        (int)drivetrain.getExpectedHeading());

                if (Math.abs(offset) > minthresh && Math.abs(offset) < turnthresh) {
                    if (offset > 0) {
                        drivetrain.setLeftMotors(motorPower * 0.1);
                        drivetrain.setRightMotors(1 - (1 - motorPower) * 0.1);
                    }
                    else {
                        drivetrain.setLeftMotors(1 - (1 - motorPower) * 0.1);
                        drivetrain.setRightMotors(motorPower * 0.1);
                    }
                }
                else if (Math.abs(offset) > turnthresh) {

                    Thread.sleep(200);

                    while (Math.abs(offset) > minthresh && waiter.opModeIsActive()) {
                        Robot.tel.addData("IN TURNING", "");
                        offset = angleDist((int)drivetrain.getActualHeading(gyro_name),
                                (int)drivetrain.getExpectedHeading());
                        drivetrain.setLeftMotors((1 - (1 - motorPower) * 0.1) * Math.signum(offset) * -1);
                        drivetrain.setRightMotors((1 - (1 - motorPower) * 0.1) * Math.signum(offset));

                        Thread.sleep(1);
                    }
                    Thread.sleep(200);
                }
                else {
                    drivetrain.setLeftMotors(motorPower);
                    drivetrain.setRightMotors(motorPower);
                }
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        drivetrain.setLeftMotors(0);
        drivetrain.setRightMotors(0);
    }
}