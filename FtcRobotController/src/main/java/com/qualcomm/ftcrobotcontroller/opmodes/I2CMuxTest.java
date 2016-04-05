package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;

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
        telemetry.addData("status", "init'ing sensors");
        mux = new Wire(hardwareMap, "mux", 2*0x70);

        color1 = new AdafruitColorSensor(hardwareMap, "ground", "cdim", -1, 0, mux);
        color2 = new AdafruitColorSensor(hardwareMap, "beacon", "cdim", -1, 1, mux);

        telemetry.addData("status", "waiting for start");

        waitForStart();

        while(opModeIsActive()) {
            telemetry.addData("status", "reading sensors");

            color1.isColorUpdate();
            telemetry.addData("color1", String.format("%d, %d, %d",
                    color1.getRed(),
                    color1.getGreen(),
                    color1.getBlue()));
//            else {
//                telemetry.addData("color1", "not connected");
//            }
            Thread.sleep(10);

            color2.isColorUpdate();
            telemetry.addData("color2", String.format("%d, %d, %d",
                    color2.getRed(),
                    color2.getGreen(),
                    color2.getBlue()));
//            else {
//                telemetry.addData("color2", "not connected");
//            }
            Thread.sleep(10);
        }
    }
}
