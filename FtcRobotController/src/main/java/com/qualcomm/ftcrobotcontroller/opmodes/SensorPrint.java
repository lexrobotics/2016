package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import lib.BotInit;
import lib.DriveTrain;
import lib.FourWheelDrive;
import lib.HelperFunctions;
import lib.Robot;
import lib.SensorState;
import lib.TwoWheelDrive;

/**
 * Created by lhscompsci on 1/19/16.
 */
public class SensorPrint extends LinearOpMode{

    @Override
    public void runOpMode() throws InterruptedException{
        BotInit.bot2(hardwareMap, telemetry, this);
        waitForStart();
        HelperFunctions.bot2SensorPrint(this);


    }


    //
//        // first case: in case the gyro actually starts calibrating a short time after g.calibrate(), due to thread things.
//        // need to have the isCalibrating condition in though, in case it calibrates quickly.
//        while (timer.time() <= 0.5) {
//            if (g.isCalibrating()) {
//                calibrated = true;
//                telemetry.addData("Calibrating time", timer.time());
//                break;
//            }
//            Thread.sleep(1);
//        }
}
