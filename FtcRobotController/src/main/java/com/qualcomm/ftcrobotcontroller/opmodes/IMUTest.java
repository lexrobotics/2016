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
import lib.Bno055;

/**
 * Created by lhscompsci on 1/19/16.
 */
public class IMUTest extends LinearOpMode {
    Bno055 bno;                                // The bno055 sensor object
    boolean initComplete = false;        // Flag to stop initialization

    Bno055.ScheduleItem     sensorData,fusionData,  // Data read schedules
            tempData,calibData,eulerData;

    public void runOpMode() throws InterruptedException {
        bno = new Bno055(hardwareMap,"bno055");
        bno.init();
        while(bno.isInitActive()){
            bno.init_loop();
            telemetry.addData("initting","yay");
        }
        waitForStart();

        sensorData = bno.startSchedule(Bno055.BnoPolling.SENSOR, 100);     // 10 Hz
        fusionData = bno.startSchedule(Bno055.BnoPolling.FUSION, 33);      // 30 Hz
        tempData = bno.startSchedule(Bno055.BnoPolling.TEMP, 200);       // 5 Hz
        calibData = bno.startSchedule(Bno055.BnoPolling.CALIB, 250);      // 4 H
        eulerData = bno.startSchedule(Bno055.BnoPolling.EULER, 15);
        while(opModeIsActive()){
            bno.loop();
            telemetry.addData("Z heading", bno.eulerZ());
            telemetry.addData("Y heading", bno.eulerY());
            telemetry.addData("X heading", bno.eulerX());
            telemetry.addData("temperature", bno.temperature());
        }
    }
}