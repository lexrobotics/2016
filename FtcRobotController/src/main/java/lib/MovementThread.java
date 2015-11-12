package lib;

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

    // Must always be less than 1
    private double scalingfactor;


    public MovementThread (DriveTrain drivetrain, String gyro_name) {
        this.drivetrain = drivetrain;
        this.gyro_name = gyro_name;
        this.power = 0;
    }

    public void setPower(double power){
        this.power = power;
    }

    @Override
    public void run() {
        drivetrain.setLeftMotors(power);
        drivetrain.setRightMotors(power);

        while (!Thread.currentThread().isInterrupted()) {
            try{
                // MAKE SURE MORE THAN 10 VALUES ARE STORED
                actualHeading = Robot.state.getAvgSensorData(gyro_name, 10);

                if (Math.abs(actualHeading - expectedHeading)>minthresh && Math.abs(actualHeading - expectedHeading)<turnthresh){
                    double scalar = (actualHeading - expectedHeading)/360;

                    // Scale down the scalar's distance from 1
                    scalar = 1 - (1 - scalar) * scalingfactor;

                    // Scale down the power's distance from 1
                    double left_power = Math.signum(power) * (1 - (1 - Math.abs(power))) * scalar;
                    double right_power = power * scalar;

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
                        drivetrain.setRightMotors(power * Math.signum(actualHeading - expectedHeading));
                        drivetrain.setLeftMotors(power * Math.signum(actualHeading - expectedHeading) * -1);
                    }
                }
                else{
                    drivetrain.setLeftMotors(power);
                    drivetrain.setRightMotors(power);
                }

                Thread.sleep(10);
            } catch (InterruptedException ex){
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public synchronized void setExpectedHeading(int angle) {
        expectedHeading = angle;
    }
}