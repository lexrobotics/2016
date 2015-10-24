package lib;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by luke on 10/7/15.
 */
public class FourWheelDrive implements DriveTrain{

    private DcMotor frontLeftMotor, frontRightMotor,
                    backLeftMotor, backRightMotor;
    private Robot robot;

    public FourWheelDrive (Robot robot,
                           DcMotor frontLeftMotor, boolean frontLeftRev,
                           DcMotor frontRightMotor, boolean frontRightRev,
                           DcMotor backLeftMotor, boolean backLeftRev,
                           DcMotor backRightMotor, boolean backRightRev) {
        this.robot = robot;
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
}
