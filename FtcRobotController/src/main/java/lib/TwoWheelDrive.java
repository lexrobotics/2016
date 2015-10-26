package lib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * Created by luke on 10/7/15.
 */
public class TwoWheelDrive implements DriveTrain {

    private DcMotor leftMotor, rightMotor;
    private Robot robot;
//    private GyroSensor gyro = (GyroSensor)robot.getSensors().get("gyro_sensors");
    private int robotHeading;

    public TwoWheelDrive (Robot robot, DcMotor leftMotor, boolean leftRev, DcMotor rightMotor, boolean rightRev) {
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

    public void moveDistance(float power, int distance){
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
