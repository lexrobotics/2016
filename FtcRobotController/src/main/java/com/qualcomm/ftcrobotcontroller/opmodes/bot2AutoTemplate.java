package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.BotInits;
import lib.FourWheelDrive;
import lib.Robot;
import lib.HelperFunctions;

/**
 * Created by lhscompsci on 1/11/16.
 */
public class bot2AutoTemplate extends LinearOpMode {

    public void runOpMode() throws InterruptedException{
        Robot dave = BotInits.bot2(hardwareMap, telemetry, this);
        waitForStart();
        while (Robot.state.gyroIsCalibrating("hero"));
        HelperFunctions.moveEncoderTest(dave,this);
    }
}
