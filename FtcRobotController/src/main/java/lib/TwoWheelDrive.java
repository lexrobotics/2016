package lib;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import static java.lang.Thread.*;

/**
 * Created by luke on 10/7/15.
 */
public class TwoWheelDrive implements DriveTrain {
    // Diameter and moveDistance should be measured in inches.

    private DcMotor leftMotor, rightMotor;
//    private Robot robot;
    double wheel_circumference;
//    private GyroSensor gyro = (GyroSensor)robot.getSensors().get("gyro_sensors");
    private int robotHeading;
    private int rightEncoder, leftEncoder;
    private Thread move_thread;
    private double expectedHeading;

    private final double TURN_SCALAR = 0.23;

    // Using SensorState, we would not need to keep a reference to Robot
    public TwoWheelDrive (DcMotor leftMotor, boolean leftRev, DcMotor rightMotor, boolean rightRev, double wheel_diameter) {
        this.wheel_circumference = wheel_diameter * Math.PI;
//        this.robot = robot;
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        expectedHeading = 0;
        if (leftRev) leftMotor.setDirection(DcMotor.Direction.REVERSE);
        if (rightRev) rightMotor.setDirection(DcMotor.Direction.REVERSE);

        resetEncoders();
    }

    @Override
    public void setLeftMotors(double power){
        this.leftMotor.setPower(power);
    }

    @Override
    public void setRightMotors(double power){
        this.rightMotor.setPower(power);
    }

    @Override
    public void move(double power) {
        this.leftMotor.setPower(power);
        this.rightMotor.setPower(power);
    }

    public void move(double power, String gyro_name, LinearOpMode waiter) {
        move_thread = new Thread(new MovementThread(this, gyro_name, 0, waiter,0.2));
        move_thread.start();
    }

    public void stopMove(){
        move_thread.interrupt();
    }

    public void resetEncoders() {
        rightEncoder = rightMotor.getCurrentPosition();
        leftEncoder = leftMotor.getCurrentPosition();
    }

    public int getEncoders() {

      return (Math.abs(rightMotor.getCurrentPosition() - rightEncoder));
    }

    @Override
    public void moveDistance(double power, double d) {
        // 1120 ticks in the encoder
        resetEncoders();
        double distance = (d/wheel_circumference) * 1120;

        while (Math.abs(getEncoders()) < distance) {
            leftMotor.setPower(power);
            rightMotor.setPower(power);
        }

        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }

    public void moveDistanceWithCorrections(double power, double d) {
        // 1120 ticks in the encoder
        resetEncoders();
        double distance = (d/wheel_circumference) * 1120;
        move(power,"hero",Robot.waiter);
        while (Math.abs(getEncoders()) < distance && Robot.waiter.opModeIsActive()) {

        }
        stopMove();

        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }

    @Override
    public void turnWithEncoders(double power, double angle) {
        resetEncoders();
        double target = TURN_SCALAR * (angle/wheel_circumference) * 1120;

        while(Math.abs(getEncoders()) < target) {
            leftMotor.setPower(-power);
            rightMotor.setPower(power);
        }

        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }

    public int distToZero(int angle1){
        angle1=Math.abs(angle1)%360;
        if(angle1>180){
            return 360 - angle1;
        }
        else{
            return angle1;
        }
    }

    public double angleDist(double deg1, double deg2)
    {
        double absDist = (360 + deg2 - deg1) % 360;
        if (absDist > 180)
            absDist -= 360;
        return absDist;
    }

    public void turnWithGyro(int degrees, String name, GyroSensor jiro) {
        expectedHeading += degrees + 360;
        expectedHeading %= 360;

        // near perfect
        PID turnPID = new PID(1.6, 0, 0, true, 1.1);
//        PID turnPID = new PID(2, 0.15, 0.35, true, 1, 0.75, 5);
        PID speedPID = new PID(0.00071, 0, 0.0002, false, 0);
        degrees= ((360 - (int)expectedHeading) + 360) % 360;

        turnPID.setTarget(degrees);
        turnPID.setMaxOutput(75);
        turnPID.setMinOutput(-75);
        speedPID.setMaxOutput(0.1);
        speedPID.setMinOutput(-0.1);
        double prevReading;
        double reading = Robot.state.getSensorReading(name);

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
            if(Math.abs(angVel) < 3) {
                speedPID.setTarget(Math.signum(angVel)*3);
            }
            else{
                speedPID.setTarget(angVel);
            }
            power += speedPID.update(filter.getAvg());
            power = Range.clip(power, -1, 1);

            leftMotor.setPower(power);
            rightMotor.setPower(-power);

            Robot.tel.addData("angle", Robot.state.getSensorReading(name));
            Robot.tel.addData("speed",filter.getAvg());
            Robot.tel.addData("pid", speedPID.update(filter.getAvg()));
            Robot.tel.addData("power", power);

            prevReading = angle;
            timer.reset();

            try {
                Thread.sleep(25);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        leftMotor.setPower(0);
        rightMotor.setPower(0);

    }

    public double getActualHeading(String name) {
        return Robot.state.getSensorReading(name);
    }

    public double getExpectedHeading() {
        return expectedHeading;
    }
}
