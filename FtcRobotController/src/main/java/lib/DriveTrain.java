package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by luke on 10/7/15.
 */
public class DriveTrain {
    // DriveTrain is the base class for TwoWheelDrive and FourWheelDrive, which only differ on the most
    // basic levels of wheel control.

    //OVERRIDDEN:
    public void setLeftMotors(double power) {}
    public void setRightMotors(double power) {}

    public void resetEncoders() {}
    public int getEncoders() {return 0;}
    // END OF OVERRIDDEN

    protected double wheel_circumference;
    protected int rightEncoder, leftEncoder;

    protected Thread move_thread;
    protected MovementThread mover;
    protected boolean thread_running;

    protected double expectedHeading;
    protected final double TURN_SCALAR = 0.23;

    public double getActualHeading(String name) {
        return Robot.state.getSensorReading(name);
    }

    public double getExpectedHeading() { return expectedHeading; }

    public double angleDist(double deg1, double deg2) {
        double absDist = (360 + deg2 - deg1) % 360;
        if (absDist > 180)
            absDist -= 360;
        return absDist;
    }

    public void moveDistance(double power, double distance){
        distance = (distance / wheel_circumference) * 1120;

        while (Math.abs(getEncoders()) < distance) {
            this.setLeftMotors(power);
            this.setRightMotors(power);
        }

        setLeftMotors(0);
        setRightMotors(0);
    }

    public void move(double power){
        setLeftMotors(power);
        setRightMotors(power);
    }

    public void moveDistanceWithCorrections(double power, String gyro_name, double distance, LinearOpMode waiter){
        // 1120 ticks in the encoder
        resetEncoders();
        distance = (distance / wheel_circumference) * 1120;

        move(power, gyro_name, waiter);
        while (Math.abs(getEncoders()) < distance && waiter.opModeIsActive()) {}
        stopMove();

        setLeftMotors(0);
        setRightMotors(0);
    }

    public void move(double power, String gyro_name, LinearOpMode waiter){
        if(!thread_running) {

            mover = new MovementThread(this, gyro_name, 0, waiter, power);
            move_thread = new Thread(mover);
            move_thread.start();
            thread_running = true;
        }

        else {
            mover.setPower(power);
        }
    }

    public void stopMove() {
        if (thread_running){
            move_thread.interrupt();
            thread_running = false;
        }
    }

    public void turnWithEncoders(double power, double angle) {
        resetEncoders();
        double target = TURN_SCALAR * (angle/wheel_circumference) * 1120;

        while(Math.abs(getEncoders()) < target) {
            setLeftMotors(-power);
            setRightMotors(power);
        }

        setLeftMotors(0);
        setRightMotors(0);
    }

    public void turnWithGyro(int degrees, String name) {
        // Stay positive
        expectedHeading += degrees + 360;
        expectedHeading %= 360;

        /*
        In this system, the turnPID's target is the final rotation angle.
        It sends rotational speed targets to the speedPID, which outputs motor values to add to the
        existing motor values to reach those speeds.
         */

        // Tuned values
        PID turnPID = new PID(1.6, 0, 0, true, 1.1);
        PID speedPID = new PID(0.00071, 0, 0.0002, false, 0);

        degrees= ((360 - (int) expectedHeading) + 360) % 360;
        turnPID.setTarget(degrees);

        turnPID.setMaxOutput(75);
        turnPID.setMinOutput(-75);
        speedPID.setMaxOutput(0.1);
        speedPID.setMinOutput(-0.1);

        double reading = Robot.state.getSensorReading(name);
        double prevReading;
        double power = 0.9;
        double time;
        double angle;
        double currentSpeed;
        double angVel;

        Filter filter = new Filter(20);

        prevReading = reading;
        ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);

        while(Robot.waiter.opModeIsActive() && !turnPID.isAtTarget(10)) {
            time = timer.time();
            angle = Robot.state.getSensorReading(name);
            currentSpeed = angleDist(angle , prevReading)/time;
            filter.update(currentSpeed);

            angVel = turnPID.updateWithError(angleDist(angle,degrees));

            // Don't want it to be able to slow down too much. Lower cap at 3, + or -
            if (Math.abs(angVel) < 3) {
                speedPID.setTarget(Math.signum(angVel)*3);
            } else{
                speedPID.setTarget(angVel);
            }

            power += speedPID.update(filter.getAvg());
            power = Range.clip(power, -1, 1);

            setLeftMotors(power);
            setRightMotors(-power);

            prevReading = angle;
            timer.reset();

            try {
                Thread.sleep(25);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        setLeftMotors(0);
        setRightMotors(0);
    }
}
