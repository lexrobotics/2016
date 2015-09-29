package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by lhscompsci on 9/28/15.
 */
public class TeleOp extends OpMode {
    DcMotor motorFrontLeft, motorFrontRight;
    DcMotor motorBackLeft, motorBackRight;

    @Override
    public void init() {
        motorFrontLeft = hardwareMap.dcMotor.get("left1");
        motorFrontRight = hardwareMap.dcMotor.get("right1");
        motorBackLeft = hardwareMap.dcMotor.get("left2");
        motorBackRight = hardwareMap.dcMotor.get("right2");

        motorBackLeft.setDirection(DcMotor.Direction.REVERSE);
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        float frontLeftPower, frontRightPower;
        float backLeftPower, backRightPower;

        gamepad1.setJoystickDeadzone(0.1f);

        frontRightPower =  gamepad1.right_stick_y;
        backRightPower =  gamepad1.right_stick_y;
        frontLeftPower = gamepad1.left_stick_y;
        backLeftPower =  gamepad1.left_stick_y;

        frontRightPower = Range.clip(frontRightPower, -1, 1);
        backRightPower = Range.clip(backRightPower, -1, 1);
        frontLeftPower = Range.clip(frontLeftPower, -1, 1);
        backLeftPower = Range.clip(backLeftPower, -1, 1);

        motorFrontLeft.setPower(frontLeftPower);
        motorBackRight.setPower(backRightPower);
        motorBackLeft.setPower(backLeftPower);
        motorFrontRight.setPower(frontRightPower);
    }

}
