package lib;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by luke on 10/7/15.
 */
public class TwoWheelDrive extends DriveTrain {
    // Diameter and moveDistance should be measured in inches.

    private DcMotor leftMotor, rightMotor;
    private int rightEncoder, leftEncoder;

    public TwoWheelDrive (DcMotor leftMotor, boolean leftRev, DcMotor rightMotor, boolean rightRev, double wheel_diameter) {
        this.wheel_circumference = wheel_diameter * Math.PI;
        this.thread_running = false;
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.expectedHeading = 0;

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
    public void resetEncoders() {
        rightEncoder = rightMotor.getCurrentPosition();
        leftEncoder = leftMotor.getCurrentPosition();
    }

    @Override
    public int getEncoders() {
        return (Math.abs(rightMotor.getCurrentPosition() - rightEncoder));
    }
}
