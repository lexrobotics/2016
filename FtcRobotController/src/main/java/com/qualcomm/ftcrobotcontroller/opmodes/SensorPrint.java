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
        while (Robot.state.gyroIsCalibrating("hero")) {
            waitOneFullHardwareCycle();
        }
//        Robot.tillWhite(0.2, "ground", "beacon");
        HelperFunctions.bot2SensorPrint(this);

//        GyroSensor g = hardwareMap.gyroSensor.get("hero");
//        ElapsedTime timer = new ElapsedTime();
//        timer.reset();
//        boolean calibrated = false;
//        boolean calibrationGapExists = false;
//        double calibrationGapTime = -1;
//
//        g.calibrate();
//
//        while ((!g.isCalibrating()) && opModeIsActive()){
//            telemetry.addData("Not calibrating", timer.time());
//            calibrationGapExists = true;
//            Thread.sleep(0, 1);
//        }
//
//        calibrationGapTime = timer.time();
//
//
//        // now waits for the duration of calibration
//        while (g.isCalibrating() && opModeIsActive()) {
//            telemetry.addData("Calibrating time", timer.time());
//            calibrated = true;
//            Thread.sleep(1);
//        }
//
//        // ending: constantly displays whether it successfully calibrated, and the rotation to test.
//        while (opModeIsActive()) {
//            telemetry.addData("Ever calibrated?", calibrated);
//            telemetry.addData("Is there a calibration gap?", calibrationGapExists);
//            telemetry.addData("Time to start calibration", calibrationGapTime);
//            telemetry.addData("Hero", g.getHeading());
//            Thread.sleep(1);
//        }

//        looking for true, true, to confirm theorry


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
