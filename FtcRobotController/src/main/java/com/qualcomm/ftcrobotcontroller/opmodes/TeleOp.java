package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
        import com.qualcomm.robotcore.hardware.Servo;

        import com.qualcomm.robotcore.util.Range;

/**
 * Created by lhscompsci on 9/28/15.
 */
public class TeleOp extends OpMode {

    DcMotor leftFrontDrive, leftRearDrive;
    DcMotor rightFrontDrive, rightRearDrive;
    DcMotor noodler, armTilter, liftStageOne, liftStageTwo;
    Servo divider, rightZipline, leftZipline, buttonPusher, climberDropper;
    Servo redDoor, blueDoor;
    Servo rightLimitServo, leftLimitServo;

    // The arm_locked and climber_drop variables say whether the climber or arm should currently be activated.
    // The toggle is aided by a_was_down and b_was_down.
    Servo armLock;
    boolean arm_locked;
    boolean b_was_down;

    boolean driveInverted = false;
    boolean bWasDown = false;
    boolean climber_drop;
    boolean a_was_down;

    ColorSensor ground;

    @Override
    public void init() {
        climber_drop = false;
        arm_locked = false;
        a_was_down = true;
        b_was_down = true;

        leftFrontDrive = hardwareMap.dcMotor.get("leftFrontDrive");
        leftRearDrive = hardwareMap.dcMotor.get("leftRearDrive");
        rightFrontDrive = hardwareMap.dcMotor.get("rightFrontDrive");
        rightRearDrive = hardwareMap.dcMotor.get("rightRearDrive");
//        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftRearDrive.setDirection(DcMotor.Direction.REVERSE);
        rightRearDrive.setDirection(DcMotor.Direction.REVERSE);

        noodler = hardwareMap.dcMotor.get("noodler");
        armTilter = hardwareMap.dcMotor.get("armTilter");
        liftStageOne = hardwareMap.dcMotor.get("liftStageOne");
        liftStageTwo = hardwareMap.dcMotor.get("liftStageTwo");

        divider = hardwareMap.servo.get("divider");
        rightZipline = hardwareMap.servo.get("rightZipline");
        leftZipline = hardwareMap.servo.get("leftZipline");

        buttonPusher = hardwareMap.servo.get("buttonPusher");
        climberDropper = hardwareMap.servo.get("climberDropper");

        redDoor = hardwareMap.servo.get("redDoor");
        blueDoor = hardwareMap.servo.get("blueDoor");

        armLock = hardwareMap.servo.get("armLock");

        leftLimitServo = hardwareMap.servo.get("leftLimitServo");
        rightLimitServo = hardwareMap.servo.get("rightLimitServo");

        noodler.setPower(0);
        armTilter.setPower(0);
        liftStageOne.setPower(0);
        liftStageTwo.setPower(0);
        leftZipline.setPosition(0.5);
        rightZipline.setPosition(0.5);
        buttonPusher.setPosition(0.5);
        climberDropper.setPosition(0.5);
        redDoor.setPosition(1);
        blueDoor.setPosition(0);
        divider.setPosition(0.5);
        armLock.setPosition(1);

        leftLimitServo.setPosition(0);
        rightLimitServo.setPosition(1);

        ground = hardwareMap.colorSensor.get("ground");
    }

    @Override
    public void loop() {
        gamepad1.setJoystickDeadzone(0.1f);
        gamepad2.setJoystickDeadzone(0.1f);

        double leftPower = scaleInput(-gamepad1.left_stick_y);
        double rightPower = scaleInput(-gamepad1.right_stick_y);

        if (driveInverted) {
            double temp = -leftPower;
            leftPower = -rightPower;
            rightPower = temp;
        }

        leftFrontDrive.setPower(leftPower);
        leftRearDrive.setPower(leftPower);
        rightFrontDrive.setPower(rightPower);
        rightRearDrive.setPower(rightPower);

        telemetry.addData("rightPower", rightPower);
        telemetry.addData("leftPower", leftPower);

        if (gamepad1.b && !bWasDown) {
            driveInverted = !driveInverted;
            bWasDown = true;
        } else if (!gamepad1.b) {
            bWasDown = false;
        }

        if (gamepad1.right_trigger >= .1) {
            noodler.setPower(1);
        } else if (gamepad1.right_bumper) {
            noodler.setPower(-1);
        } else {
            noodler.setPower(0);
        }

        armTilter.setPower(scaleInput(gamepad2.left_stick_y));


        if (gamepad2.left_trigger >= .1) {
            liftStageOne.setPower(-gamepad2.left_trigger);
        }
        else if (gamepad2.left_bumper) {
            liftStageOne.setPower(1);
        }
        else {
            liftStageOne.setPower(0);
        }

        if (gamepad2.right_trigger >= .1) {
            liftStageTwo.setPower(gamepad2.right_trigger);
        }
        else if (gamepad2.right_bumper) {
            liftStageTwo.setPower(-1);
        }
        else {
            liftStageTwo.setPower(0);
        }

        // dpad
        if (gamepad2.dpad_left) {
            divider.setPosition(1);
        }
        else if (gamepad2.dpad_right) {
            divider.setPosition(0);
        }
        else {
            divider.setPosition(.5);
        }


        if(gamepad2.left_stick_x > 0.5) {
            rightZipline.setPosition(1);
        }
        else if(gamepad2.left_stick_x < -0.5) {
            leftZipline.setPosition(1);
        }
        else if(gamepad2.a) {
            leftZipline.setPosition(0);
            rightZipline.setPosition(0);
        }
        else {
            leftZipline.setPosition(0.5);
            rightZipline.setPosition(0.5);
        }


        if (gamepad2.x) {
            redDoor.setPosition(0);
            blueDoor.setPosition(1);
        } else {
            redDoor.setPosition(1);
            blueDoor.setPosition(0);
        }

        if(gamepad2.start) {
            buttonPusher.setPosition(0.7); // press button pusher
        }
        else {
            buttonPusher.setPosition(0.5); // press button pusher
        }

//        if (gamepad2.a) {
//            if (a_was_down){
//                climber_drop = !climber_drop;
//                a_was_down = false;
//            }
//        } else {
//            a_was_down = true;
//        }

        if (gamepad2.b) {
            if (b_was_down){
                arm_locked = !arm_locked;
                b_was_down = false;
            }
        } else {
            b_was_down = true;
        }

        if (arm_locked){
            climberDropper.setPosition(0.7);
        } else {
            climberDropper.setPosition(0.5);
        }

        if (arm_locked) {
            armLock.setPosition(0.7);
        } else {
            armLock.setPosition(1);
        }
    }

    /**
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
//        if(dVal > 0.1)
//            return 0.2;
//        if(dVal < -0.1)
//            return -0.2;
//        return 0;
    }
}
