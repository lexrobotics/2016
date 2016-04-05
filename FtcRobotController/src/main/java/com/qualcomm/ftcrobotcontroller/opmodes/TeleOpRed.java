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
public class TeleOpRed extends TeleOp {
    @Override
    public void loop() {
        super.loop();

        if (gamepad2.b) {
            blueDoor.setPosition(1);
        } else if (gamepad2.x) {
            redDoor.setPosition(0);
        } else if (gamepad2.y) {
            blueDoor.setPosition(.55);
        } else {
            blueDoor.setPosition(0);
            redDoor.setPosition(1);
        }
    }
}
