package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;

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

    public void turnWithGyro(double power, int degrees, String name) {
        int prevReading = (int)Robot.state.getSensorReading(name);
        int currReading = prevReading;
        int sum = 0;
        leftMotor.setPower(-power);
        rightMotor.setPower(power);
        while (Robot.waiter.opModeIsActive()) {
            prevReading = currReading;
            currReading = (int)Robot.state.getSensorReading(name);
            sum += angleDist(prevReading, currReading);
            int offset = Math.abs(degrees) - Math.abs(sum);
            //if (offset < 10) {
            //    leftMotor.setPower(power * offset / -10);
            //    rightMotor.setPower(power * offset / 10);
            //}
            if (offset <= 0)
                break;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        leftMotor.setPower(0);
        rightMotor.setPower(0);
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
