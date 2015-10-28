package lib;

import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * Created by luke on 10/28/15.
 */
public class MovementThread implements Runnable{

    private double expectedHeading, actualHeading;
    private GyroSensor gyro;
    private DriveTrain drivetrain;

    public MovementThread (GyroSensor gyro, DriveTrain drivetrain) {
        this.gyro = gyro;
        this.drivetrain = drivetrain;
    }

    @Override
    public void run() {

    }

    public synchronized void setExpectedHeading(int angle) {
        expectedHeading = angle;
    }

    public void getActualHeading() {
        actualHeading = gyro.getRotation();
    }
}
