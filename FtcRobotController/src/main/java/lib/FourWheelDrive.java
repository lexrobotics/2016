package lib;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by luke on 10/7/15.
 */
public class FourWheelDrive implements DriveTrain{
    // All lengths are measured in inches.

    private DcMotor frontLeftMotor, frontRightMotor,
                    backLeftMotor, backRightMotor;
    private double wheel_circumference;

    public FourWheelDrive (DcMotor frontLeftMotor, boolean frontLeftRev,
                           DcMotor frontRightMotor, boolean frontRightRev,
                           DcMotor backLeftMotor, boolean backLeftRev,
                           DcMotor backRightMotor, boolean backRightRev,
                           double wheel_diameter) {
        this.wheel_circumference = Math.PI * wheel_diameter;
        this.frontLeftMotor = frontLeftMotor;
        this.frontRightMotor = frontRightMotor;
        this.backLeftMotor = backLeftMotor;
        this.backRightMotor = backRightMotor;

        if (frontLeftRev) frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        if (frontRightRev) frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        if (backLeftRev) backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        if (backRightRev) backRightMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void move(float power) {
        this.frontLeftMotor.setPower(power);
        this.frontRightMotor.setPower(power);
        this.backLeftMotor.setPower(power);
        this.backRightMotor.setPower(power);
    }

    public void moveDistance(double power, double distance){
        // 1120 ticks in the encoder
        distance = (distance/wheel_circumference) * 1120;

        while ((backLeftMotor.getCurrentPosition() +
                backRightMotor.getCurrentPosition() +
                frontLeftMotor.getCurrentPosition() +
                frontRightMotor.getCurrentPosition()) / 4 > distance){
            backLeftMotor.setPower(power);
            backRightMotor.setPower(power);
            frontLeftMotor.setPower(power);
            frontRightMotor.setPower(power);
        }
    }

    public void turnWithEncoders(float power, int degrees)
    {

    }

}
