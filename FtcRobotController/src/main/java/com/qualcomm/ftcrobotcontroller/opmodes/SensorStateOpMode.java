package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import lib.Robot;
//import lib.SensorData;
import lib.SensorState;

/**
 * Created by luke on 10/27/15.
 */
public class SensorStateOpMode extends OpMode {
    private SensorState color_state;
    private SensorState sensor_state;
    private SensorState.SensorData data;

    @Override
    public void loop(){
        data = color_state.getSensorDataArray("mr");
        telemetry.addData("r / g / b", data.values[1] + " / " + data.values[2] + " / " + data.values[2]);
        try {
            Thread.sleep(10);
        } catch (InterruptedException ex){

        }
    }

    @Override
    public void init(){
        color_state = new SensorState(hardwareMap, 0, 500000);
        color_state.registerSensor("mr", SensorState.SensorType.COLOR, true, -1);

//        sensor_state = new SensorState(hardwareMap, 50, 0);
//        sensor_state.registerSensor()

        SensorState.SensorData data;
        new Thread(color_state).start();
        new Thread(sensor_state).start();
    }
}
