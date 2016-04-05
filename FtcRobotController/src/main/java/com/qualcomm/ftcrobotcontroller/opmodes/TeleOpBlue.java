package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;

import com.qualcomm.robotcore.util.Range;

import lib.PID;
import lib.TeleOp;

/**
 * Created by lhscompsci on 9/28/15.
 */
public class TeleOpBlue extends TeleOp {
    @Override
    public void loop() {
        super.loop();
        if (gamepad2.x) {
            blueDoor.setPosition(1);
        } else if (gamepad2.b) {
            redDoor.setPosition(0);
        } else if (gamepad2.y) {
            redDoor.setPosition(.45);
        } else {
            redDoor.setPosition(1);
            blueDoor.setPosition(0);
        }
    }
}
