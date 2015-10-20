package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import lib.Robot;

/**
 * Created by luke on 10/7/15.
 */
public class ColorSweep extends LinearOpMode {
    // Demo class for the new Robot classes.

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        Robot dave = new Robot(hardwareMap, telemetry, this);
        dave.registerDriveMotors("left_motors", true, "right_motors", false);
        dave.registerColorSensor("mr");
        dave.colorSweep("red");
//        this.waitOneFullHardwareCycle();

    }


}
