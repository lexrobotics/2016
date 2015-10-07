package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by lhscompsci on 9/28/15.
 */
public class TeleOp extends OpMode {
    final static double NOODLER_POWER = 1.0;
    final static double DUMP_MAX_RANGE = 0.7;
    final static double DUMP_MIN_RANGE = 0.3;
    final static double LIFT_MAX_RANGE = 1.0;
    final static double LIFT_MIN_RANGE = 0.0;

    final static double LIFT_DELTA = 0.03;

    DcMotor leftDrivePair, rightDrivePair;
    DcMotor noodler;
    Servo dumpServo1, dumpServo2, liftServo;

    double dumpServoPosition = 0.5;
    double liftServoPosition = 0.3;

    @Override
    public void init() {
        leftDrivePair = hardwareMap.dcMotor.get("leftdrive");
        rightDrivePair = hardwareMap.dcMotor.get("rightdrive");
        noodler = hardwareMap.dcMotor.get("noodler");

        rightDrivePair.setDirection(DcMotor.Direction.REVERSE);

        liftServo = hardwareMap.servo.get("lift");
        dumpServo1 = hardwareMap.servo.get("dump1");
        dumpServo2 = hardwareMap.servo.get("dump2");
    }

    @Override
    public void loop() {
        gamepad1.setJoystickDeadzone(0.1f);
        gamepad2.setJoystickDeadzone(0.1f);

        leftDrivePair.setPower(-gamepad1.left_stick_y);
        rightDrivePair.setPower(-gamepad1.right_stick_y);

        if(gamepad1.right_bumper)
            noodler.setPower(NOODLER_POWER);
        else
            noodler.setPower(0);

        if(gamepad2.dpad_up)
            liftServoPosition += LIFT_DELTA;
        else if(gamepad2.dpad_down)
            liftServoPosition -= LIFT_DELTA;

        if(gamepad2.a)
            dumpServoPosition = DUMP_MAX_RANGE;
        else if(gamepad2.y)
            dumpServoPosition = DUMP_MIN_RANGE;
        else
            dumpServoPosition = 0.5;

        liftServoPosition = Range.clip(liftServoPosition, LIFT_MIN_RANGE, LIFT_MAX_RANGE);

        liftServo.setPosition(liftServoPosition);
        setDumpServos(dumpServoPosition);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setDumpServos(double pos) {
        dumpServo1.setPosition(pos);
        dumpServo2.setPosition(1.0 - pos);
    }
}
