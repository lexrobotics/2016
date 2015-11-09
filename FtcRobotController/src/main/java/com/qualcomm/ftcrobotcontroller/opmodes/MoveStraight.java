package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.TwoWheelDrive;

/**
 * Created by luke on 11/3/15.
 */
public class MoveStraight extends LinearOpMode {
    public void runOpMode() throws InterruptedException{
        waitForStart();

        TwoWheelDrive dave_train = new TwoWheelDrive(   hardwareMap.dcMotor.get("leftdrive"), true,
                hardwareMap.dcMotor.get("rightdrive"), false, 4);
        dave_train.move(-0.25);

        while (opModeIsActive()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex){
                Thread.currentThread().interrupt();
            }
        }
    }
}
