package com.qualcomm.ftcrobotcontroller.opmodes;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

//COLOR VALUES
//with blue light r:0 g:0 b:1
//with red light r:1 g:0 b:0
//with wall r:0 g:0 b:0
//with ceiling r:1 g:2 b:3
//max dist 6 inches




/**
 * Created by luke on 10/5/15.
 */
public class ColorBasic extends LinearOpMode{
    ColorSensor sensorRGB;

    @Override
    public void runOpMode() throws InterruptedException{
        hardwareMap.logDevices();
        sensorRGB = hardwareMap.colorSensor.get("mr");

        sensorRGB.enableLed(false);
        waitOneFullHardwareCycle();
        waitForStart();

        while (opModeIsActive()){
            sensorRGB.enableLed(false);
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
