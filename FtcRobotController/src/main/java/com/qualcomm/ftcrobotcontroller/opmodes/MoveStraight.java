package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.TwoWheelDrive;

/**
 * Created by luke on 11/3/15.
 */
public class MoveStraight extends LinearOpMode {

        @Override
        public void runOpMode() throws InterruptedException{
            waitForStart();
            TwoWheelDrive drivetrain = new TwoWheelDrive(   hardwareMap.dcMotor.get("leftdrive"), true,
                    hardwareMap.dcMotor.get("rightdrive"), false, 4);
            drivetrain.moveDistance(-0.25F, 78);
        }
}
