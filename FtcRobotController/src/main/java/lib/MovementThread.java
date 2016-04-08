package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;







/*
IF THIS HAS A PROBLEM, MAKE SURE THAT THE STATIC VARIABLES IN ROBOT ARE ACTUALLY INSTANTIATED
ALSO, I REMOVED SOME THREAD INTERRUPT CHECKS IN TURN, BECAUSE THEY SEEMED UNNECESSARY. THEY MIGHT NOT BE.
ALSO CHANGED PID TO BE ABLE TO RECREATE ITSELF

KpDivisor, Ki, and Kd were 3.6, 0.05, 0.01 in the old system
 */









/**
 * Created by luke on 10/28/15.
 */
public class MovementThread implements Runnable {

    private double power;
    private double turnThresh;
    private PID pid;
    private double KpDivisor, Ki, Kd;

    public MovementThread (double power, int min, int turn, double KpDivisor, double Ki, double Kd) {
        this.power = power;

        turnThresh = turn;

        this.KpDivisor = KpDivisor;
        this.Ki = Ki;
        this.Kd = Kd;

        pid = new PID(0, 0, 0);
    }

    public synchronized void setPower(double power){
        this.power = power;
    }

    public double angleDist(double deg1, double deg2) {
        double absDist = (360 + deg2 - deg1) % 360;
        if (absDist > 180)
            absDist -= 360;
        return absDist;
    }

    private double getOffset() {
        return angleDist(   Robot.drivetrain.getActualHeading(Robot.gyroName),
                            Robot.drivetrain.getExpectedHeading()           );
    }

    private void stopBothMotors() {
        Robot.drivetrain.setLeftMotors(0);
        Robot.drivetrain.setRightMotors(0);
    }

    private void turn() throws InterruptedException {
        double offset;

        do {
            offset = getOffset();
            Robot.tel.addData("offset", offset);
            Robot.drivetrain.setRightMotors(0.3 * Math.signum(offset) * -1);
            Robot.drivetrain.setLeftMotors(0.3 * Math.signum(offset));
            Thread.sleep(50);

        } while (Math.abs(offset) > turnThresh && Robot.waiter.opModeIsActive() && !Thread.currentThread().isInterrupted() && !Robot.drivetrain.isAMotorZero());
    }


    @Override
    public void run() {
        // Distance from goal angle
        double offset;

        // Prevents power from changing halfway through a loop
        double currentPower;

        // Determines how much to alter power for each motor to put the robot back on track
        double correction;

//        synchronized (this){
            currentPower = power;
//        }

        double maxOutput = Math.min(1 - Math.abs(currentPower), Math.abs(currentPower));
//        PID correctionPID = new PID(maxOutput/3.6, 0.05, 0.01);
        pid.recreate(KpDivisor, Ki, Kd);

        pid.setMaxOutput(maxOutput);
        pid.setMinOutput(-1 * maxOutput);
        Robot.drivetrain.setLeftMotors(currentPower);
        Robot.drivetrain.setRightMotors(currentPower);

        while (!Thread.currentThread().isInterrupted() && Robot.waiter.opModeIsActive()) {
            synchronized (this) {
                currentPower = power;
            }

            if(currentPower == 0){
                stopBothMotors();
                Thread.currentThread().interrupt();
                break;
            }

            Robot.tel.addData("expHead: " + Robot.drivetrain.getExpectedHeading() + " active: " + Robot.waiter.opModeIsActive(), "");

            try {
                offset = getOffset();
                Robot.tel.addData("offset", offset);

                if (Math.abs(offset) < turnThresh && !Robot.drivetrain.isAMotorZero()) {
                    correction = pid.updateWithError(offset);
                    Robot.drivetrain.setLeftMotors(currentPower + correction);
                    Robot.drivetrain.setRightMotors(currentPower - correction);
                }

                // The offset is too great, so we have to stop and do a controlled turn back to the right value.
                else if (Math.abs(offset) > turnThresh) {
                    Thread.sleep(100);
                    turn();
                    Thread.sleep(100);
                }

                Thread.sleep(50);

            } catch (InterruptedException ex) {
                stopBothMotors();
                Thread.currentThread().interrupt();
                break;
            }
        }

        stopBothMotors();
    }
}