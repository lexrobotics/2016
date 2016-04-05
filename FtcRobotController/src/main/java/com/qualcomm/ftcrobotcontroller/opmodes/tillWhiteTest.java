package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.BotInit;
import lib.HelperFunctions;
import lib.Robot;

/**
 * Created by noah on 3/18/16.
 */
public class tillWhiteTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException{
        BotInit.bot2(hardwareMap, telemetry, this);
        waitForStart();
//        Robot.tillWhite(0.2, "ground", "beacon");
        Robot.tillWhiteJumpThresh (-0.175, "ground", "beacon", "red");

    }
}

