package simplelib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * Movement Thread Rewritten
 *
 * Written by Vivek Bhupatiraju & Luke West
 */

public class MoveCorrect implements Runnable {

    private DriveTrain drivetrain;
    private double power;
    private double minthresh, maxthresh;
    private double scalar;


}