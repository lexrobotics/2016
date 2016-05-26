package lib;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by lhscompsci on 5/26/16.
 */
public class MecanumDrive extends OpMode {
    DcMotor leftFrontDrive, leftRearDrive;
    DcMotor rightFrontDrive, rightRearDrive;


    @Override
    public void init() {
        leftFrontDrive = hardwareMap.dcMotor.get("leftFrontDrive");
        leftRearDrive = hardwareMap.dcMotor.get("leftRearDrive");
        rightFrontDrive = hardwareMap.dcMotor.get("rightFrontDrive");
        rightRearDrive = hardwareMap.dcMotor.get("rightRearDrive");

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftRearDrive.setDirection(DcMotor.Direction.REVERSE);
    }

    public void loop() {

       gamepad1.setJoystickDeadzone(0.1f);
       gamepad2.setJoystickDeadzone(0.1f);

       double leftFrontPower, leftRearPower;
       double rightFrontPower, rightRearPower;
       double x1, x2, y1, y2;

       x1 = scaleInput(-gamepad1.left_stick_x);
       x2 = scaleInput(-gamepad1.right_stick_x);
       y1 = scaleInput(-gamepad1.left_stick_y);
       y2 = scaleInput(-gamepad1.right_stick_y);

       leftFrontPower = y1 + x2;
       leftRearPower = y2 + x1;
       rightFrontPower = y1 - x1;
       rightRearPower = y2 + x1;

      leftFrontDrive.setPower(leftFrontPower);
      leftRearDrive.setPower(leftRearPower);
      rightFrontDrive.setPower(rightFrontPower);
      rightRearDrive.setPower(rightRearPower);
    }

    double scaleInput(double dVal) {
        double[] scaleArray = {0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00};

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
}