package com.qualcomm.ftcrobotcontroller.opmodes;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


/**
 * Created by luke on 10/5/15.
 */
public class ColorBasic extends OpMode{
    ColorSensor sensorRGB;

    public void init() {
        hardwareMap.logDevices();
        sensorRGB = hardwareMap.colorSensor.get("mr");

        sensorRGB.enableLed(true);
    }

    public void loop() {
        telemetry.addData("Clear", sensorRGB.alpha());
        telemetry.addData("Red  ", sensorRGB.red());
        telemetry.addData("Green", sensorRGB.green());
        telemetry.addData("Blue ", sensorRGB.blue());
    }

}
