package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.util.Range;

import lib.Robot;
import lib.TwoWheelDrive;


public class TurnWithEncoder extends LinearOpMode {
    // Demo class for the new Robot classes.

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        Robot dave = new Robot(hardwareMap, telemetry, this);
        TwoWheelDrive dave_train = new TwoWheelDrive(   hardwareMap.dcMotor.get("left_motors"), true,
                hardwareMap.dcMotor.get("right_motors"), false, 4);

        dave.registerDriveTrain(dave_train);
        dave.registerColorSensor("mr");
        dave.registerLightSensor("mrs");

        dave_train.turnWithEncoders(0.25F, 1120);
    }
}
