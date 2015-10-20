package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;

import lib.UltraSonic;

/**
 * Created by lhscompsci on 10/19/15.
 */
public class UltraSonicOpMode extends LinearOpMode {

    AnalogInput us;

    @Override
    public void runOpMode() throws InterruptedException {
        hardwareMap.logDevices();
        us = hardwareMap.analogInput.get("USA");
        UltraSonic ultra  = new UltraSonic(us);

        waitOneFullHardwareCycle();
        waitForStart();

        while (opModeIsActive())
        {
            telemetry.addData("Distance", ultra.getDistance());
            waitOneFullHardwareCycle();
        }
    }

}
