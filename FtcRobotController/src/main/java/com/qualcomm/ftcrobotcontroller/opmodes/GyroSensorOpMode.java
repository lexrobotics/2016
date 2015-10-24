package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.GyroSensor;

import lib.Robot;
import lib.UltraSonic;

/**
 * Created by lhscompsci on 10/19/15.
 */
public class GyroSensorOpMode extends LinearOpMode {

    GyroSensor gyro;

    @Override
    public void runOpMode() throws InterruptedException {
        Robot dave = new Robot(hardwareMap, telemetry, this);
        dave.registerGyroSensor("hero");
        gyro = (GyroSensor)dave.getSensors().get("gyro_sensors");

        while (opModeIsActive())
        {
            telemetry.addData("Gyro Heading", gyro.getRotation());
            waitOneFullHardwareCycle();
        }

    }

}
