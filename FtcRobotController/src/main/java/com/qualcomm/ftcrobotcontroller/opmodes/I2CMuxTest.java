package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.AdafruitColorSensor;

/**
 * Created by noah on 3/23/16.
 */
public class I2CMuxTest extends LinearOpMode {
    AdafruitColorSensor color1, color2;

    @Override
    public void runOpMode() throws InterruptedException {
        color1 = new AdafruitColorSensor(hardwareMap, "color1", "cdim", 0, 0, "mux");
        color2 = new AdafruitColorSensor(hardwareMap, "color1", "cdim", 0, 1, "mux");

        while(opModeIsActive()) {
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
