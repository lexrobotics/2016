package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;
//import com.qualcomm.robotcore.hardware.GyroSensor;

import lib.SensorState;

/**
 * Created by luke on 11/9/15.
 */
public class GyroTest extends LinearOpMode {
//    SensorState state;

    @Override
    public void runOpMode() throws InterruptedException{
        waitForStart();
//        state = new SensorState(hardwareMap, 1, 0);
//        state.registerSensor("gyro", SensorState.SensorType.GYRO, true, 40);
//        Thread state_thread = new Thread(state);
//        state_thread.start();
        GyroSensor gyro = hardwareMap.gyroSensor.get("gyro");
        gyro.calibrate();
        while (gyro.isCalibrating()){
            try{
                Thread.sleep(10);
            } catch (InterruptedException ex){}
            telemetry.addData("Calibrating", true);
        }

        while (opModeIsActive()){
            telemetry.addData("1. x", String.format("%03d", gyro.rawX()));
            telemetry.addData("2. y", String.format("%03d", gyro.rawY()));
            telemetry.addData("3. z", String.format("%03d", gyro.rawZ()));
            telemetry.addData("4. h", String.format("%03d", gyro.getHeading()));
//            telemetry.addData("Gyro", gyro.getHeading());
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex){
                Thread.currentThread().interrupt();
                break;
            }
        }
//        state_thread.interrupt();
//        GyroSensor x;
//        x.
    }
}
