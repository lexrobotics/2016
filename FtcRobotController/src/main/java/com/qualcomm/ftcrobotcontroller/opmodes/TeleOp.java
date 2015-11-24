package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by lhscompsci on 9/28/15.
 */
public class TeleOp extends OpMode {

    DcMotor leftDrivePair, rightDrivePair;
    DcMotor noodler, armTilter, liftStageOne, liftStageTwo;
    Servo allClearSignal, leftBallRelease, rightBallRelease;

    boolean driveInverted = false;
    boolean bWasDown = false;
    int closed = 0, rightClosed = 0;

    @Override
    public void init() {
        leftDrivePair = hardwareMap.dcMotor.get("leftdrive");
        rightDrivePair = hardwareMap.dcMotor.get("rightdrive");
        noodler = hardwareMap.dcMotor.get("noodler");
        armTilter = hardwareMap.dcMotor.get("armTilter");
        liftStageOne = hardwareMap.dcMotor.get("liftOne");
        liftStageTwo = hardwareMap.dcMotor.get("liftTwo");

        rightDrivePair.setDirection(DcMotor.Direction.REVERSE);

        allClearSignal = hardwareMap.servo.get("allClear");
        leftBallRelease = hardwareMap.servo.get("leftDump");
        rightBallRelease = hardwareMap.servo.get("rightDump");
    }

    @Override
    public void loop() {
        gamepad1.setJoystickDeadzone(0.1f);
        gamepad2.setJoystickDeadzone(0.1f);

        double leftPower = scaleInput(-gamepad1.left_stick_y);
        double rightPower = scaleInput(-gamepad1.right_stick_y);
        ballRelease.setPosition(90);

        if (driveInverted) {
            double temp = -leftPower;
            leftPower = -rightPower;
            rightPower = temp;
        }

        leftDrivePair.setPower(leftPower);
        rightDrivePair.setPower(rightPower);

//        if(gamepad1.right_trigger > 0.1)
//            noodler.setPower(-1*scaleInput(gamepad1.right_trigger));
//        else if(gamepad1.left_bumper)
//            noodler.setPower(NOODLER_POWER/2);                            noodler stuff
//        else if(gamepad1.right_bumper)
//            noodler.setPower(NOODLER_POWER);
//        else
//            noodler.setPower(0);


        if (gamepad1.b && !bWasDown) {
            driveInverted = !driveInverted;
            bWasDown = true;
        } else if (!gamepad1.b) {
            bWasDown = false;
        }

        if (gamepad1.right_trigger >= .1)
            noodler.setPower(gamepad1.right_trigger);
        else if (gamepad1.left_trigger >= .1)
            noodler.setPower(-gamepad1.left_trigger);
        else
            noodler.setPower(0);


        if (gamepad2.dpad_up)
            armTilter.setPower(-.4);
        else if (gamepad2.dpad_down)
            armTilter.setPower(.4);
        else
            armTilter.setPower(0);


        if (gamepad2.left_trigger <= .1)
            liftStageOne.setPower(gamepad2.left_trigger);
        else if(gamepad2.left_bumper)
            liftStageOne.setPower(-1);
        else
            liftStageOne.setPower(0);

        if (gamepad2.right_trigger <= .1)
            liftStageTwo.setPower(gamepad2.right_trigger);
        else if(gamepad2.right_bumper)
            liftStageTwo.setPower(-1);
        else
            liftStageTwo.setPower(0);

        if (gamepad1.dpad_left)
            allClearSignal.setPosition(1);
        else if (gamepad1.dpad_right)
            allClearSignal.setPosition(0);
        else
            allClearSignal.setPosition(.5);

        if(gamepad2.x) {
            if (closed == 0) {
                leftBallRelease.setPosition(90);
                closed--;
            }
            else
                leftBallRelease.setPosition(0);
                closed++;
        }

        if(gamepad2.b) {
            if (rightClosed == 0) {
                rightBallRelease.setPosition(90);
                rightClosed--;
            }
            else
                rightBallRelease.setPosition(0);
            rightClosed++;
        }

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
