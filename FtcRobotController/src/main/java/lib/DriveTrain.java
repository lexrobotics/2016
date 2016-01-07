package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

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
        PID turnPID = new PID(.031,0,0.05,true, 0.1); // 0.003

        //need target thresh
        turnPID.setTarget(degrees);
        turnPID.setMaxOutput(1);
        turnPID.setMinOutput(-1);
        double update;
        while (Robot.waiter.opModeIsActive()&& !turnPID.isAtTarget()) {
            update = turnPID.update(Robot.state.getSensorReading(name));
            this.setLeftMotors(update);
            this.setRightMotors(-update);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.setLeftMotors(0);
        this.setRightMotors(0);
    }
}
