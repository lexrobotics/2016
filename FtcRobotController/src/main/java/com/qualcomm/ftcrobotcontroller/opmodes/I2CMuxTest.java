package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.robocol.Telemetry;

import lib.AdafruitColorSensor;
import lib.Wire;

/**
 * Created by noah on 3/23/16.
 */
public class I2CMuxTest extends LinearOpMode {
    AdafruitColorSensor color1, color2;
    Wire mux;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("status", "init'ing");
        mux = new Wire(hardwareMap, "mux", 2*0x70);
        telemetry.addData("status", "mux initialized");
        color1 = new AdafruitColorSensor(hardwareMap, "color1", "cdim", 0, 0, mux);
        telemetry.addData("status", "mux, color1 initialized");
        color2 = new AdafruitColorSensor(hardwareMap, "color2", "cdim", 0, 1, mux);
        telemetry.addData("status", "waiting for start");

        while(opModeIsActive()) {
            telemetry.addData("status", "reading sensors");
            if(color1.isColorUpdate()) {
                telemetry.addData("color1", System.out.format("%d, %d, %d",
                        color1.getRed(),
                        color1.getGreen(),
                        color1.getBlue()));
            }
            if(color2.isColorUpdate()) {
                telemetry.addData("color2", System.out.format("%d, %d, %d",
                        color2.getRed(),
                        color2.getGreen(),
                        color2.getBlue()));
            }
            Thread.sleep(50);
        }
    }
}
