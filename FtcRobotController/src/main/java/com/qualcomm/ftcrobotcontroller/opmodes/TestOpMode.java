package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.BotInit;
import lib.DriveTrain;
import lib.FourWheelDrive;
import lib.HelperFunctions;
import lib.Robot;
import lib.SensorState;
import lib.TwoWheelDrive;

/**
 * Created by lhscompsci on 1/19/16.
 */
public class TestOpMode extends LinearOpMode{

    @Override
    public void runOpMode() throws InterruptedException{
        BotInit.bot2(hardwareMap, telemetry, this);
        waitForStart();
        while (Robot.state.gyroIsCalibrating("hero")) {
            waitOneFullHardwareCycle();
        }
        HelperFunctions.bot2SensorPrint(this);
    }
}
