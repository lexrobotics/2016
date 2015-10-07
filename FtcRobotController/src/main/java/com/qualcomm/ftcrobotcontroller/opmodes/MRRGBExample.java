package com.qualcomm.ftcrobotcontroller.opmodes;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

/*
 *
 * This is an example LinearOpMode that shows how to use
 * a Modern Robotics Color Sensor.
 *
 * The op mode assumes that the color sensor
 * is configured with a name of "mr".
 *
 * You can use the X button on either gamepad to turn the LED on and off.
 *
 */
public class MRRGBExample extends LinearOpMode {

    ColorSensor sensorRGB;


    @Override
    public void runOpMode() throws InterruptedException {

        // write some device information (connection info, name and type)
        // to the log file.
        hardwareMap.logDevices();

        // get a reference to our ColorSensor object.
        sensorRGB = hardwareMap.colorSensor.get("mr");

        // bEnabled represents the state of the LED.
//        boolean bEnabled = true;

        // turn the LED on in the beginning, just so user will know that the sensor is active.
        sensorRGB.enableLed(true);

        // wait one cycle.
        waitOneFullHardwareCycle();

        // wait for the start button to be pressed.
        waitForStart();

        // hsvValues is an array that will hold the hue, saturation, and value information.
//        float hsvValues[] = {0F,0F,0F};

        // values is a reference to the hsvValues array.
//        final float values[] = hsvValues;

        // get a reference to the RelativeLayout so we can change the background
        // color of the Robot Controller app to match the hue detected by the RGB sensor.
//        final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(R.id.RelativeLayout);

        // bPrevState and bCurrState represent the previous and current state of the button.
//        boolean bPrevState = false;
//        boolean bCurrState = false;

        // while the op mode is active, loop and read the RGB data.
        // Note we use opModeIsActive() as our loop condition because it is an interruptible method.
        while (opModeIsActive()) {
            // check the status of the x button on either gamepad.
//            bCurrState = gamepad1.x || gamepad2.x;
            sensorRGB.enableLed(true);

            // convert the RGB values to HSV values.
            //Color.RGBToHSV((sensorRGB.red() * 8), (sensorRGB.green() * 8), (sensorRGB.blue() * 8), hsvValues);
//            Color.RGBToHSV(sensorRGB.red()*8, sensorRGB.green()*8, sensorRGB.blue()*8, hsvValues);

            // send the info back to driver station using telemetry function.
            telemetry.addData("Clear", sensorRGB.alpha());
            telemetry.addData("Red  ", sensorRGB.red());
            telemetry.addData("Green", sensorRGB.green());
            telemetry.addData("Blue ", sensorRGB.blue());
//            telemetry.addData("Hue", hsvValues[0]);

            // change the background color to match the color detected by the RGB sensor.
            // pass a reference to the hue, saturation, and value array as an argument
            // to the HSVToColor method.
//            relativeLayout.post(new Runnable() {
//                public void run() {
//                    relativeLayout.setBackgroundColor(Color.HSVToColor(0xff, values));
//                }
//            });

            // wait a hardware cycle before iterating.
            waitOneFullHardwareCycle();
        }
    }
}