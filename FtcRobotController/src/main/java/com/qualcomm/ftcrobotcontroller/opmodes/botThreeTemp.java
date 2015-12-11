package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by lhscompsci on 12/9/15.
 */
public class botThreeTemp extends OpMode{
    DcMotor leftDrivePair, rightDrivePair, armAngle, armExtend;

    public void init() {
        leftDrivePair = hardwareMap.dcMotor.get("leftdrive");
        rightDrivePair = hardwareMap.dcMotor.get("rightdrive");
        armAngle = hardwareMap.dcMotor.get("armAngle");
        armExtend = hardwareMap.dcMotor.get("armExtend");

        rightDrivePair.setDirection(DcMotor.Direction.REVERSE);

    }

    @Override
    public void loop() {
        gamepad1.setJoystickDeadzone(0.1f);
        gamepad2.setJoystickDeadzone(0.1f);

//        double leftPower = scaleInput(-gamepad1.left_stick_y);
//        double rightPower = scaleInput(-gamepad1.right_stick_y);
//        if(driveInverted) {
//            double temp = -leftPower;
//            leftPower = -rightPower;
//            rightPower = temp;
//        }

        //arm extend

        leftDrivePair.setPower(-gamepad1.left_stick_y);
        rightDrivePair.setPower(-gamepad1.right_







































































































































                stick_y);

        if(gamepad1.right_trigger > 0.1)
            armExtend.setPower(-1);
        else if(gamepad1.right_bumper)
            armExtend.setPower(1);
        else
            armExtend.setPower(0);

        //arm angle

        if(gamepad1.left_trigger > 0.1)
            armAngle.setPower(-1);
        else if(gamepad1.left_bumper)
            armAngle.setPower(1);
        else
            armAngle.setPower(0);
    }
}
