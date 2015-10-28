package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.TwoWheelDrive;

/**
 * Created by luke on 10/27/15.
 */
public class ScrimmageAuto extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException{
        waitForStart();
        TwoWheelDrive drivetrain = new TwoWheelDrive(   hardwareMap.dcMotor.get("left_motors"), true,
                hardwareMap.dcMotor.get("right_motors"), false, 4);
        drivetrain.moveDistance(-0.25F, 74);
    }
}
