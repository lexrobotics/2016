package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;

import lib.TeleOp;

/**
 * Created by lhscompsci on 4/8/16.
 */
public class dimitriIsAWhinyChief extends TeleOp {
    DcMotor rightDrive, leftDrive;

    @Override

    public void init() {
        leftDrive = hardwareMap.dcMotor.get("leftDrive");
        rightDrive = hardwareMap.dcMotor.get("rightDrive");

        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        gamepad1.setJoystickDeadzone(0.1f);
        gamepad2.setJoystickDeadzone(0.1f);


    }
    double leftPower;
    double rightPower;
    @Override
    public void loop() {


        leftDrive.setPower(scaleInput(-gamepad1.left_stick_y));
        rightDrive.setPower(scaleInput(-gamepad1.right_stick_y));
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
//        if(dVal > 0.1)
//            return 0.2;
//        if(dVal < -0.1)
//            return -0.2;
//        return 0;
//        Anthony Wiryaman
    }
}
