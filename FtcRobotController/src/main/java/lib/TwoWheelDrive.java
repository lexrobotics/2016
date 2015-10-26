package lib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * Created by luke on 10/7/15.
 */
public class TwoWheelDrive implements DriveTrain {
    // Diameter and moveDistance should be measured in inches.

    private DcMotor leftMotor, rightMotor;
    private Robot robot;
    double wheel_circumference;
//    private GyroSensor gyro = (GyroSensor)robot.getSensors().get("gyro_sensors");
    private int robotHeading;

    public TwoWheelDrive (Robot robot, DcMotor leftMotor, boolean leftRev, DcMotor rightMotor, boolean rightRev, double wheel_diameter) {
        this.wheel_circumference = wheel_diameter * Math.PI;
        this.robot = robot;
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        if (leftRev) leftMotor.setDirection(DcMotor.Direction.REVERSE);
        if (rightRev) rightMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void move(float power) {
        this.leftMotor.setPower(power);
        this.rightMotor.setPower(power);
    }

//    public void move(float power)
//    {
//        leftMotor.setPower(power);
//        rightMotor.setPower(power);
//    }


    public void moveDistance(float power, double d){
        // 1120 ticks in the encoder
        double distance = (d/wheel_circumference) * 1120;

        while ((leftMotor.getCurrentPosition() + rightMotor.getCurrentPosition()) / 2 > distance){
            leftMotor.setPower(power);
            rightMotor.setPower(power);
        }
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
