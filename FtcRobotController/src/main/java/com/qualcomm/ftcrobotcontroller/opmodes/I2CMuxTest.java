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
    GyroSensor gyro;
    Wire mux;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("status", "init'ing sensors");
        mux = new Wire(hardwareMap, "mux", 2*0x70);

        mux.write(0, 1<<2);
        while(mux.responseCount() < 1);
        mux.getResponse();
        color1 = new AdafruitColorSensor(hardwareMap, "color1", "cdim", 0, 2, mux);
        mux.write(0, 1<<3);
        while(mux.responseCount() < 1);
        mux.getResponse();
        color2 = new AdafruitColorSensor(hardwareMap, "color2", "cdim", 0, 3, mux);
        gyro = hardwareMap.gyroSensor.get("gyro");
        telemetry.addData("status", "waiting for start");

        gyro.calibrate();
        while(gyro.isCalibrating()) {
            Thread.sleep(10);
        }

        waitForStart();

        while(opModeIsActive()) {
            telemetry.addData("status", "reading sensors");

            mux.write(0, 1 << 2);
            while(mux.responseCount() < 1);
            mux.getResponse();
            color1.isColorUpdate();
            telemetry.addData("color1", String.format("%d, %d, %d",
                    color1.getRed(),
                    color1.getGreen(),
                    color1.getBlue()));
//            else {
//                telemetry.addData("color1", "not connected");
//            }
            Thread.sleep(10);

            mux.write(0, 1<<3);
            while(mux.responseCount() < 1);
            mux.getResponse();
            color2.isColorUpdate();
            telemetry.addData("color2", String.format("%d, %d, %d",
                    color2.getRed(),
                    color2.getGreen(),
                    color2.getBlue()));
//            else {
//                telemetry.addData("color2", "not connected");
//            }
            Thread.sleep(10);

            telemetry.addData("gyro", gyro.getHeading());
        }
    }
}
