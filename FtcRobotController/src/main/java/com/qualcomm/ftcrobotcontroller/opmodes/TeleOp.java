package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import java.lang.InterruptedException;

/**
 * Created by lhscompsci on 9/28/15.
 */
public class TeleOp extends OpMode {
    DcMotor motors_left, motors_right;
    DcMotor lift_1, lift_2;
    DcMotor tilt;

    double left_power, right_power;

    @Override
    public void init() {
        motors_left = hardwareMap.dcMotor.get("left");
        motors_right = hardwareMap.dcMotor.get("right");

        lift_1 = hardwareMap.dcMotor.get("lift1");
        lift_2 = hardwareMap.dcMotor.get("lift2");

        tilt = hardwareMap.dcMotor.get("tilt");

        motors_left.setDirection(DcMotor.Direction.REVERSE);
        lift_1.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        gamepad1.setJoystickDeadzone(0.1f);

        left_power = scaleInput(-gamepad1.left_stick_y);
        right_power = scaleInput(-gamepad1.right_stick_y);

        motors_left.setPower(left_power);
        motors_right.setPower(right_power);

        if(gamepad2.dpad_up){
            lift_1.setPower(0.5);
            lift_2.setPower(0.5);
        } else if (gamepad2.dpad_down){
            lift_1.setPower(-0.5);
            lift_2.setPower(-0.5);
        } else {
            lift_1.setPower(0);
            lift_2.setPower(0);
        }

        if (gamepad2.x){
            tilt.setPower(0.5);
        } else if (gamepad2.b){
            tilt.setPower(0.5);
        } else {
            tilt.setPower(0);
        }

        try {
            Thread.sleep(15);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

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
