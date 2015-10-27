package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import lib.Robot;
//import lib.SensorData;
import lib.SensorState;

/**
 * Created by luke on 10/27/15.
 */
public class SensorStateOpMode extends LinearOpMode {
    private SensorState state;

    @Override
    public void runOpMode() throws InterruptedException{
        Thread.sleep(1000);

        state = new SensorState(hardwareMap, 100);
        state.registerSensor("mr", SensorState.sensorType.COLOR, true, -1);
        telemetry.addData("Update", state.sensors.get("mr").update);

//        SensorState.SensorData data

//        new Thread(state).start();
//        SensorData data;
//
//        // Testing registration and retrieval
//        state.registerSensor("mr")
//
//        while (opModeIsActive()){
//            data = state.getSensorData("mr");
//            telemetry.addData("red", data.values[1]);
//            telemetry.addData("green", data.values[2]);
//            telemetry.addData("blue", data.values[3]);
//            Thread.sleep(10);
//        }
    }
}
