package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by lhscompsci on 11/3/15.
 */
public class botThree extends OpMode {

    DcMotor leftDrivePair, rightDrivePair, liftOne, liftTwo, tilt;

    @Override
    public void init() {

        leftDrivePair = hardwareMap. dcMotor.get("left");
        rightDrivePair = hardwareMap.dcMotor.get("right");
        liftOne = hardwareMap. dcMotor.get("lift1");
        liftTwo = hardwareMap.dcMotor.get("lift2");
        tilt = hardwareMap. dcMotor.get("tilt");

        // noodler = hardwareMap.dcMotor.get("noodler");

        liftOne.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        gamepad1.setJoystickDeadzone(0.1f);
        gamepad2.setJoystickDeadzone(0.1f);

        double leftPower = -gamepad1.left_stick_y;
        double rightPower = -gamepad1.right_stick_y;

//
        leftDrivePair.setPower(leftPower);
        rightDrivePair.setPower(rightPower);

        if (gamepad2.dpad_up)
        {
            liftOne.setPower(0.9f);
            liftTwo.setPower(0.9f);
        }

        else if (gamepad2.dpad_down)
        {
            liftOne.setPower(-0.9f);
            liftTwo.setPower(-0.9f);
        }

        else
        {
            liftOne.setPower(0f);
            liftTwo.setPower(0f);
        }


        if (gamepad2.x) tilt.setPower(1f);
        else if (gamepad2.b) tilt.setPower(-1f);
        else tilt.setPower(0f);


    }
}