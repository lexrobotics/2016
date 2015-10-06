package com.qualcomm.ftcrobotcontroller.opmodes;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;


/**
 * Created by luke on 10/5/15.
 */
public class ColorBasic extends LinearOpMode{
    ColorSensor sensorRGB;

    int x;
    public void runOpMode() throws InterruptedException{
        hardwareMap.logDevices();
        sensorRGB = hardwareMap.colorSensor.get("mr");

        sensorRGB.enableLed(true);
        waitOneFullHardwareCycle();
        waitForStart();

        while (opModeIsActive()){
            sensorRGB.enableLed(true);
            telemetry.addData("Clear", sensorRGB.alpha());
            telemetry.addData("Red  ", sensorRGB.red());
            telemetry.addData("Green", sensorRGB.green());
            telemetry.addData("Blue ", sensorRGB.blue());
            waitOneFullHardwareCycle();
        }

    }

//    public void loop() {
//        telemetry.addData("Clear", sensorRGB.alpha());
//        telemetry.addData("Red  ", sensorRGB.red());
//        telemetry.addData("Green", sensorRGB.green());
//        telemetry.addData("Blue ", sensorRGB.blue());
//    }

}
