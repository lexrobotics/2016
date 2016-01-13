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
    public int getEncoders() {
        Robot.tel.addData("BAD GETENCODERS CALLED", "");
        return 0;
    }
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
        this.resetEncoders();
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
        while (Math.abs(this.getEncoders()) < distance && waiter.opModeIsActive()) {
            Robot.tel.addData("encoder", this.getEncoders());
        }
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
        this.setLeftMotors(0);
        this.setRightMotors(0);

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
        int i=0;
//        PID turnPID = new PID(.03,0.0078,0.005,true, 1.1, 20); // 0.003
        PID turnPID = new PID(0.011, 0.01, 0, true, 0, -1); // 0.003
        //need target thresh
        turnPID.setTarget(degrees);
        turnPID.setMaxOutput(1);
        turnPID.setMinOutput(-1);
        double update;
        while (Robot.waiter.opModeIsActive()&& !turnPID.isAtTarget()) {
            update = turnPID.update(Robot.state.getSensorReading(name));
            Robot.tel.addData("PID update", scaleInput(update));
            Robot.tel.addData("PID error", turnPID.getError());
            Robot.tel.addData("loops", i);
            this.setRightMotors(scaleInput(update));
            this.setLeftMotors(scaleInput(-update));
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
        this.setLeftMotors(0);
        this.setRightMotors(0);
    }

    public void turn(double speed) {
        this.setRightMotors(speed);
        this.setLeftMotors(-speed);
    }
    /*
 * This method scales the joystick input so for low joystick values, the
 * scaled value is less than linear.  This is to make it easier to drive
 * the robot more precisely at slower speeds.
 */
    private double scaleInput(double dVal)  {
        double direction = Math.signum(dVal);
        dVal = Math.abs(dVal);

//        double[] scaleArray = { 0.0, 0.2, 0.34, 0.44, 0.52, 0.59, 0.65, 0.70,
//                0.75, 0.79, 0.83, 0.86, 0.89, 0.92, 0.95, 1, 1.00 };
        double[] scaleArray = { 0.0, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5,
                0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 1, 1.00 };

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);

        // index should be positive.
        if (index < 0) {
            index = -index;
        }

        // index cannot exceed size of array minus 1.
        if (index > 16) {
            index = 16;
        }

        // get value from the array.
        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        // return scaled value.
        return direction * dScale;
    }

    public void dumbGyroTurn(double power, double angle, String name){
        double goal = (Robot.state.getSensorReading(name) + Math.signum(power) * angle + 360) % 360;
        this.setLeftMotors(power);
        this.setRightMotors(-power);

        double diff = angleDist(goal, Robot.state.getSensorReading(name));
        double prevDiff = diff;

        while (angleDist(goal, Robot.state.getSensorReading(name)) < 2.1 && Robot.waiter.opModeIsActive()){
            diff = angleDist(goal, Robot.state.getSensorReading(name));
            if (diff - prevDiff > 0){
                break;
            }
        }

        this.setLeftMotors(0);
        this.setRightMotors(0);

        expectedHeading = (expectedHeading + Math.signum(power) * angle + 360) % 360;
    }

}
