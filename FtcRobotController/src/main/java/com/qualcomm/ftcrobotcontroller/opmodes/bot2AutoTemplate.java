package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.BotInits;
import lib.FourWheelDrive;
import lib.Robot;
import lib.HelperFunctions;
import lib.SensorState;

/**
 * Created by lhscompsci on 1/11/16.
 */
public class bot2AutoTemplate extends LinearOpMode {

    public void runOpMode() throws InterruptedException{
        Robot dave = BotInits.bot2(hardwareMap, telemetry, this);
        waitForStart();
//        for(double i=0; i<1; i+=0.01) {
//            dave.drivetrain.turn(i);
//            telemetry.addData("power", i);
//            try {
//                Thread.sleep(100);
//            } catch(InterruptedException e) {
//
//            }
//        }
        while (Robot.state.gyroIsCalibrating("hero"));
        HelperFunctions.bot2SensorPrint(dave, this);
//        dave.colorSweep(SensorState.ColorType.RED, "light", "color", 0.2);
//        telemetry.addData("done", "yes");
    }
}
