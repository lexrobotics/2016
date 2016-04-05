package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.BotInit;
import lib.HelperFunctions;
import lib.Robot;

/**
 * Created by lhscompsci on 3/2/16.
 */
public class pidCalibration extends LinearOpMode {
    @Override

    public void runOpMode() throws InterruptedException {
        BotInit.bot2(hardwareMap, telemetry, this);
//        int delayTime = (int) Robot.delaySet("delayDial", "beaconToucher");

        waitForStart();



        HelperFunctions.movementThreadCalibration(this);
    }
}
