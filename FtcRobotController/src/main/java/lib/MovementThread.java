package lib;

import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * Created by luke on 10/28/15.
 */
public class MovementThread implements Runnable{

    private double expectedHeading, actualHeading;
    private GyroSensor gyro;
    private DriveTrain drivetrain;
    private String gyro_name;

    public MovementThread (GyroSensor gyro, DriveTrain drivetrain, String gyro_name) {
        this.gyro = gyro;
        this.drivetrain = drivetrain;
        this.gyro_name = gyro_name;
    }

    @Override
    public void run() {
        while (true) {
            try{
//                if
                Thread.sleep(50);

            } catch (InterruptedException ex){}
        }
    }

    public synchronized void setExpectedHeading(int angle) {
        expectedHeading = angle;
    }

    public void getActualHeading() {
        actualHeading = Robot.state.getSensorReading(gyro_name);
    }
}
