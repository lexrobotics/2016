package lib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;

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

    public void move(double power, String gyro_name) {
        move_thread = new Thread(new MovementThread(this, gyro_name));
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

    public void turnWithGyro(double power, double degrees) {
//        double goal = gyro.getRotation + degrees;
//        while (gyro.getRotation() - initial < )
    }

//    public void turnWithGyro(float power, double heading)
//    {
//        while (gyro.getRotation() < heading)
//        {
//            leftMotor.setPower(power);
//            rightMotor.setPower(-power);
//        }
//    }
}
