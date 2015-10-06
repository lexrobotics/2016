package com.qualcomm.ftcrobotcontroller.opmodes;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.LED;

public class VivekSensor extends LinearOpMode{
    ColorSensor colorSensor;
    DeviceInterfaceModule cdim;
//    int count;
    LED led;
//    TouchSensor t;

    public void runOpMode() throws InterruptedException{
//        count = 0;
        hardwareMap.logDevices();

        cdim = hardwareMap.deviceInterfaceModule.get("dim");
        colorSensor = hardwareMap.colorSensor.get("mr");

//        led = hardwareMap.led.get("led");
//        t = hardwareMap.touchSensor.get("t");

        waitForStart();


        float hsvValues[] = {0F,0F,0F};
        final float values[] = hsvValues;
        final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(R.id.RelativeLayout);
        colorSensor.enableLed(true);

        while (opModeIsActive()) {

//            enableLed(t.isPressed());
            Color.RGBToHSV(colorSensor.red() * 8, colorSensor.green() * 8, colorSensor.blue() * 8, hsvValues);

            telemetry.addData("Red", colorSensor.red());
            telemetry.addData("Blue", colorSensor.blue());
            telemetry.addData("Green", colorSensor.green());

            relativeLayout.post(new Runnable() {
                public void run() {
                    relativeLayout.setBackgroundColor(Color.HSVToColor(0xff, values));
                }
            });
            waitOneFullHardwareCycle();
        }
    }
//
//    private void enableLed(boolean value) {
//        colorSensor.enableLed(value);
//    }
}
