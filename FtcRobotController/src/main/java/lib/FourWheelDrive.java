package lib;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by luke on 10/7/15.
 */
public class FourWheelDrive extends DriveTrain{
    // All lengths are measured in inches.

    private DcMotor frontLeftMotor, frontRightMotor,
            backLeftMotor, backRightMotor;


    private int frontRightEncoder, frontLeftEncoder, backRightEncoder, backLeftEncoder;

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

        frontRightEncoder = frontRightMotor.getCurrentPosition();
        frontLeftEncoder = frontLeftMotor.getCurrentPosition();
        backRightEncoder = backRightMotor.getCurrentPosition();
        backLeftEncoder = backLeftMotor.getCurrentPosition();

        if (frontLeftRev) frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        if (frontRightRev) frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        if (backLeftRev) backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        if (backRightRev) backRightMotor.setDirection(DcMotor.Direction.REVERSE);

        resetEncoders();
    }

    @Override
    public void setLeftMotors(double power){
        this.backLeftMotor.setPower(power);
        this.frontLeftMotor.setPower(power);
    }

    @Override
    public void setRightMotors(double power){
        this.backRightMotor.setPower(power);
        this.frontRightMotor.setPower(power);
    }


    @Override
    public void resetEncoders() {
        frontRightEncoder = frontRightMotor.getCurrentPosition();
        frontLeftEncoder = frontLeftMotor.getCurrentPosition();
        backRightEncoder = backRightMotor.getCurrentPosition();
        backLeftEncoder = backLeftMotor.getCurrentPosition();
    }

    @Override
    public int getEncoders() {
        return (Math.abs(
            (frontLeftMotor.getCurrentPosition() - frontLeftEncoder) +
            (frontRightMotor.getCurrentPosition() - frontRightEncoder)
        ) / 2);
    }

    public void outputEncoders() {
        Robot.tel.addData("frontLeft", frontLeftMotor.getCurrentPosition());
        Robot.tel.addData("frontRight", frontRightMotor.getCurrentPosition());
        Robot.tel.addData("backLeft", backLeftMotor.getCurrentPosition());
        Robot.tel.addData("backRight", backRightMotor.getCurrentPosition());
    }

    public boolean isAMotorZero() {
        return (
                frontLeftMotor.getPower() == 0 ||
                frontRightMotor.getPower() == 0 ||
                backLeftMotor.getPower() == 0 ||
                backRightMotor.getPower() == 0
        );
    }

}
