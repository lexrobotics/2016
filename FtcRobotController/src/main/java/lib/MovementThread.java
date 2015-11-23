package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * Created by luke on 10/28/15.
 */
public class MovementThread implements Runnable{

    private double expectedHeading, actualHeading;
    private DriveTrain drivetrain;
    private String gyro_name;
    private double power;
    private double minthresh;
    private double turnthresh;
    private LinearOpMode waiter;

    // Must always be less than 1
    private double scalingfactor;


    public MovementThread (DriveTrain drivetrain, String gyro_name, int expectedHeading, LinearOpMode waiter) {
        this.drivetrain = drivetrain;
        this.gyro_name = gyro_name;
        this.power = 0;
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

    @Override
    public void run() {
//        drivetrain.setLeftMotors(power);
//        drivetrain.setRightMotors(power);
        double motorPower;

        while (!Thread.currentThread().isInterrupted() && waiter.opModeIsActive()) {
            synchronized(this){
                motorPower = this.power;
            }

            try{
                actualHeading = Robot.state.getSensorReading(gyro_name);
                Robot.tel.addData("Gyro heading", actualHeading);

                if (Math.abs(actualHeading - expectedHeading)>minthresh && Math.abs(actualHeading - expectedHeading)<turnthresh){
                    double scalar = (actualHeading - expectedHeading)/360;

                    // Scale down the scalar's distance from 1
                    scalar = 1 - (1 - scalar) * scalingfactor;

                    // Scale down the power's distance from 1
                    double left_power = Math.signum(motorPower) * (1 - (1 - Math.abs(motorPower))) * scalar;
                    double right_power = motorPower * scalar;

                    // switch if necessary. I don't actually know what the correct case is, so this might be wrong.
                     if (actualHeading > expectedHeading){
                         double temp = left_power;
                         left_power = right_power;
                         right_power = temp;
                     }

                    drivetrain.setLeftMotors(left_power);
                    drivetrain.setRightMotors(right_power);
                }
                else if((actualHeading - expectedHeading)>turnthresh){
                    while(Math.abs(actualHeading - expectedHeading)>minthresh) {
                        drivetrain.setRightMotors(motorPower * Math.signum(actualHeading - expectedHeading));
                        drivetrain.setLeftMotors(motorPower * Math.signum(actualHeading - expectedHeading) * -1);
                    }
                }
                else{
                    drivetrain.setLeftMotors(motorPower);
                    drivetrain.setRightMotors(motorPower);
                }

                Thread.sleep(10);
            } catch (InterruptedException ex){
                Thread.currentThread().interrupt();
                break;
            }
        }

        drivetrain.setLeftMotors(0);
        drivetrain.setRightMotors(0);
    }
}