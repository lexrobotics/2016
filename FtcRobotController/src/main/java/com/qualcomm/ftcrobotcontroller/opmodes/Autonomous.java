package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.Menu;

/**
 * Created by lhscompsci on 4/15/16.
 */
public class Autonomous extends LinearOpMode {
    Menu menu;
    @Override
    public void runOpMode() throws InterruptedException {
        while(!opModeIsActive() || !menu.configEntered()) {
            menu.update();
        }

        waitForStart();
//        Thread.sleep(menu.getDelay() * 1000);
    }
}
