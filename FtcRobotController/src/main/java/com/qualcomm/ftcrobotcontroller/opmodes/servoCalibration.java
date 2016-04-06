package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.BotInit;
import lib.HelperFunctions;
import lib.BotInit;
import lib.HelperFunctions;
import lib.Robot;

/**
 * Created by lhscompsci on 4/6/16.
 */
public class servoCalibration extends LinearOpMode{
    @Override

    public void runOpMode() throws InterruptedException {
        BotInit.bot2(hardwareMap, telemetry, this);
//        int delayTime = (int) Robot.delaySet("delayDial", "beaconToucher");

        waitForStart();


//        Robot.closeSkirts();
            HelperFunctions.calibrateServos("rightZipline", "leftZipline", 0.4, 0, hardwareMap, gamepad1, this, telemetry);
    }

}
