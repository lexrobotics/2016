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
    DcMotor motorArmExtend;
    DcMotor motorArmAngle;

    @Override
    public void init() {
        motorFrontLeft = hardwareMap.dcMotor.get("left1");
        motorFrontRight = hardwareMap.dcMotor.get("right1");
        motorBackLeft = hardwareMap.dcMotor.get("left2");
        motorBackRight = hardwareMap.dcMotor.get("right2");
        motorArmExtend = hardwareMap.dcMotor.get("armExtend");
        motorArmAngle = hardwareMap.dcMotor.get("armAngle");

        motorBackLeft.setDirection(DcMotor.Direction.REVERSE);
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        gamepad1.setJoystickDeadzone(0.1f);
        gamepad2.setJoystickDeadzone(0.1f);

        motorArmAngle.setPower(gamepad2.right_stick_y);
        motorArmExtend.setPower(gamepad2.right_stick_x);
        motorFrontLeft.setPower(-gamepad1.left_stick_y);
        motorBackRight.setPower(-gamepad1.right_stick_y);
        motorBackLeft.setPower(-gamepad1.left_stick_y);
        motorFrontRight.setPower(-gamepad1.right_stick_y);
    }

}
