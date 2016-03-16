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
    private double scalar;

    // Must always be less than 1
    private double scalingfactor;


    public MovementThread (DriveTrain drivetrain, String gyro_name, LinearOpMode waiter, double power) {
        this.drivetrain = drivetrain;
        this.gyro_name = gyro_name;
        this.power = power;
        this.waiter = waiter;
        this.scalar = 0.1;
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
        double maxOutput = Math.min(1 - Math.abs(power), Math.abs(power));
        PID correctionPID = new PID(maxOutput/3.6, 0.05, 0.01);

        correctionPID.setMaxOutput(maxOutput);
        correctionPID.setMinOutput(-1 * maxOutput);
        // All of the scaling stuff in here scales down distance towards a particular value, 0, or 1
        // This means that they can never scale past 0 or past 1.

//        synchronized (this)

        drivetrain.setLeftMotors(power);
        drivetrain.setRightMotors(power);

        int offset;

        while (!Thread.currentThread().isInterrupted() && waiter.opModeIsActive()) {
            if(power==0){
                drivetrain.setLeftMotors(0);
                drivetrain.setRightMotors(0);

                Thread.currentThread().interrupt();
            }
            Robot.tel.addData("expHead: " + drivetrain.getExpectedHeading() + " active: " + waiter.opModeIsActive(), "");

            try {

                offset = angleDist((int)drivetrain.getActualHeading(gyro_name), (int)drivetrain.getExpectedHeading());
                Robot.tel.addData("offset",offset);

                if (Math.abs(offset) > minthresh && Math.abs(offset) < turnthresh && !drivetrain.isAMotorZero()) {
                    double correction = correctionPID.updateWithError(offset);
                    drivetrain.setLeftMotors(power + correction);
                    drivetrain.setRightMotors(power - correction);
                }

                // The offset is too great, so we have to stop and do a controlled turn back to the right value.
                else if (Math.abs(offset) > turnthresh) {


                    if(Thread.currentThread().isInterrupted()){ break;}
                        Thread.sleep(100);

                    while (Math.abs(offset) > turnthresh && waiter.opModeIsActive() && !Thread.currentThread().isInterrupted() && !drivetrain.isAMotorZero()) {
                        Robot.tel.addData("offset",offset);
                        offset = angleDist((int)drivetrain.getActualHeading(gyro_name), (int)drivetrain.getExpectedHeading());
                        drivetrain.setRightMotors(0.3 * Math.signum(offset) * -1);
                        drivetrain.setLeftMotors(0.3 * Math.signum(offset));

                        Thread.sleep(1);
                    }
                    if(Thread.currentThread().isInterrupted()){ break;}
                    Thread.sleep(100);
                }

                // Nothing's wrong, so we drive normally

                else {
                    drivetrain.setLeftMotors(power);
                    drivetrain.setRightMotors(power);
                }

                Thread.sleep(50);
            } catch (InterruptedException ex) {
                drivetrain.setLeftMotors(0);
                drivetrain.setRightMotors(0);
                Thread.currentThread().interrupt();
                break;
            }
        }

        drivetrain.setLeftMotors(0);
        drivetrain.setRightMotors(0);
    }
}