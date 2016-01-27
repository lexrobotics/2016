package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

/**
 * Created by lhscompsci on 1/27/16.
 */
public class TwoColorSensorTest extends OpMode {
    ColorSensor color1;
    ColorSensor color2;

    @Override
    public void init() {
        color1 = hardwareMap.colorSensor.get("color1");
        color2 = hardwareMap.colorSensor.get("color2");
        color2.setI2cAddress(0x42);
    }

    @Override
    public void loop() {
       // telemetry.addData("color1", color1.());
        telemetry.addData("color2", color2.argb());
    }
}
