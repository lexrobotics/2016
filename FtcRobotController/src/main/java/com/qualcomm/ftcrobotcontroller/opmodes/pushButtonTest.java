package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import lib.BotInit;
import lib.Menu;
import lib.Robot;

/**
 * Created by noah on 4/29/16.
 */
public class pushButtonTest extends LinearOpMode {
    public void runOpMode() throws InterruptedException {
        BotInit.bot2(hardwareMap, telemetry, this);
        waitForStart();

        Robot.pushButton("beaconToucher", -1);

        while (opModeIsActive()) {
            Thread.sleep(1);
        }
    }
}
