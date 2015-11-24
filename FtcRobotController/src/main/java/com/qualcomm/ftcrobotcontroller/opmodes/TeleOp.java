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
    final static double NOODLER_REVERSE_POWER = -1.0;
    final static double DUMP_SPEED_FORWARDS_SLOW = 0.55;
    final static double DUMP_SPEED_REVERSE_SLOW = 0.45;
    final static double DUMP_SPEED_FORWARDS = 0.6;
    final static double DUMP_SPEED_REVERSE = 0.4;
    final static double LIFT_MAX_RANGE = 1.0;
    final static double LIFT_MIN_RANGE = 0.0;

    final static double LIFT_DELTA_DOWN = 0.03;
    final static double LIFT_DELTA_UP = 0.01;

    DcMotor leftDrivePair, rightDrivePair;
    DcMotor noodler;
    Servo dumpServo1, dumpServo2, liftServo, climber;

    boolean driveInverted = false;
    boolean bWasDown = false;
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
        climber = hardwareMap.servo.get("climber");
    }

    @Override
    public void loop() {
        gamepad1.setJoystickDeadzone(0.1f);
        gamepad2.setJoystickDeadzone(0.1f);

        double leftPower = scaleInput(-gamepad1.left_stick_y);
        double rightPower = scaleInput(-gamepad1.right_stick_y);
        if(driveInverted) {
            double temp = -leftPower;
            leftPower = -rightPower;
            rightPower = temp;
        }

        leftDrivePair.setPower(leftPower);
        rightDrivePair.setPower(rightPower);

        if(gamepad1.right_trigger > 0.1)
            noodler.setPower(-1*scaleInput(gamepad1.right_trigger));
        else if(gamepad1.left_bumper)
            noodler.setPower(NOODLER_POWER/2);
        else if(gamepad1.right_bumper)
            noodler.setPower(NOODLER_POWER);
        else
            noodler.setPower(0);

        if(gamepad1.b && !bWasDown) {
            driveInverted = !driveInverted;
            bWasDown = true;
        }
        else if(!gamepad1.b) {
            bWasDown = false;
        }

        if(gamepad2.b)
            climber.setPosition(0);
        else
            climber.setPosition(1);

        if(gamepad2.dpad_up)
            liftServoPosition += LIFT_DELTA_UP;
        else if(gamepad2.dpad_down)
            liftServoPosition -= LIFT_DELTA_DOWN;

        if (gamepad2.a) {
            if(gamepad2.left_bumper || gamepad2.right_bumper)
                dumpServoPosition = DUMP_SPEED_FORWARDS_SLOW;
            else
                dumpServoPosition = DUMP_SPEED_FORWARDS;
        }
        else if (gamepad2.y) {
            if(gamepad2.left_bumper || gamepad2.right_bumper)
                dumpServoPosition = DUMP_SPEED_REVERSE_SLOW;
            else
                dumpServoPosition = DUMP_SPEED_REVERSE;
        }
        else if(liftServoPosition > 0.4)
            dumpServoPosition = 0.48;
        else
            dumpServoPosition = 0.5;

        liftServoPosition = Range.clip(liftServoPosition, LIFT_MIN_RANGE, LIFT_MAX_RANGE);

        liftServo.setPosition(liftServoPosition);
        setDumpServos(dumpServoPosition);

        telemetry.addData("liftServoPosition", liftServoPosition);
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

    /*
     * This method scales the joystick input so for low joystick values, the
     * scaled value is less than linear.  This is to make it easier to drive
     * the robot more precisely at slower speeds.
     */
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
