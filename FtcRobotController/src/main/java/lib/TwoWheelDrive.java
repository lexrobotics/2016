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

    private final double TURN_SCALAR = 0.23;

    // Using SensorState, we would not need to keep a reference to Robot
    public TwoWheelDrive (DcMotor leftMotor, boolean leftRev, DcMotor rightMotor, boolean rightRev, double wheel_diameter) {
        this.wheel_circumference = wheel_diameter * Math.PI;
//        this.robot = robot;
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
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

    public int angleDist(int deg1, int deg2)
    {
        int absDist = (360 + deg2 - deg1) % 360;
        if (absDist > 180)
            absDist -= 360;
        return absDist;
    }

    public void turnWithGyro(int degrees, String name, GyroSensor jiro) {
//        PID turnPID = new PID(.010,.02,0,true, 0.1);
//        PID turnPID = new PID(.02,0.02,0,true, 0.1,20); // 0.003
        PID turnPID = new PID(100,0.1,0.0,true, 0.1); // 0.003
        PID speedPID = new PID(0.05, 0, 0, false, 0);

        //need target thresh
        turnPID.setTarget(degrees);
        turnPID.setMaxOutput(5000);
        turnPID.setMinOutput(-5000);
        speedPID.setMaxOutput(0.4);
        speedPID.setMinOutput(-0.4);

        double initDistance = degrees - Robot.state.getSensorReading(name);
        double error;
        double maxSpeed = 1;
        double minSpeed = 0.2;
        double base = 1.5;
        double centerC = 0.5;

        double prevReading;
        double power;
        double angle = Robot.state.getSensorReading(name);
        prevReading = angle;
        ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);


//        while (Robot.waiter.opModeIsActive()&& !turnPID.isAtTarget()) {
          while(Robot.waiter.opModeIsActive()) {
              double dt = timer.time(); // get time since last update
              angle = Robot.state.getSensorReading(name);

//            speed = turnPID.update(Robot.state.getSensorReading(name));
              speedPID.setTarget(10);
               power = speedPID.update((angle-prevReading)/dt);
              prevReading =angle;
              Robot.tel.addData("power ", power);
              Robot.tel.addData("rot ", (angle-prevReading)/dt);
              Robot.tel.addData("dt ", dt);


//            Log.i("PID", String.format("update: %f, integral: %f, gyro: %f", update, turnPID.getITerm(), Robot.state.getSensorReading(name)));
            leftMotor.setPower(Range.clip(0.5 + power, -1, 1));
            rightMotor.setPower(Range.clip(-(0.5 + power), -1, 1));
              timer.reset();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }

    double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

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
        return dScale;
    }

    /*
    public void oldTurnWithGyro(double power, int degrees, String name) {
        int goal = (360 + (int)Robot.state.getSensorReading(name) + degrees) % 360;
        int prevReading = (int)Robot.state.getSensorReading(name);
        int currReading = prevReading;
        leftMotor.setPower(-power);
        rightMotor.setPower(power);
        if (power > 0) {
            while (Robot.waiter.opModeIsActive()) {
            // when our reading range "passes over" goal
                prevReading = currReading;
                currReading = (int)Robot.state.getSensorReading(name);
                if (angleDist(prevReading, goal) <= 180 && angleDist(currReading, goal) >= 180)
                    break;
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            while (Robot.waiter.opModeIsActive()) {
                prevReading = currReading;
                currReading = (int)Robot.state.getSensorReading(name);
                if (angleDist(prevReading, goal) >= 180 && angleDist(currReading, goal) <= 180)
                    break;
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        leftMotor.setPower(0);
        rightMotor.setPower(0);
        while (Robot.waiter.opModeIsActive()){
            Robot.tel.addData("Reading", Robot.state.getSensorReading(name));
        }
    }
    */
}
