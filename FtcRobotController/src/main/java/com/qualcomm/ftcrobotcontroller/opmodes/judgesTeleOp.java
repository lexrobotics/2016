package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.robocol.Telemetry;

import lib.TeleOp;

/**
 * Created by noah on 4/22/16.
 */
public class judgesTeleOp extends TeleOp {
    DcMotor leftFrontDrive, leftRearDrive;
    DcMotor rightFrontDrive, rightRearDrive;

    public void init() {
        leftFrontDrive = hardwareMap.dcMotor.get("leftFrontDrive");
        leftRearDrive = hardwareMap.dcMotor.get("leftRearDrive");
        rightFrontDrive = hardwareMap.dcMotor.get("rightFrontDrive");
        rightRearDrive = hardwareMap.dcMotor.get("rightRearDrive");
    }
    
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

        leftFrontDrive.setPower(0);
        leftRearDrive.setPower(0);
        rightFrontDrive.setPower(0);
        rightRearDrive.setPower(0);
    }
}

