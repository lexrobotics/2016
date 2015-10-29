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
//    private SensorState color_state;
    private SensorState state;
//    private SensorState.SensorData data;
    SensorState.ColorType data;

    @Override
    public void loop(){
//        data = color_state.getSensorDataArray("mr");
        data = state.getColorData("color");
//        telemetry.addData("r / g / b", data.values[1] + " / " + data.values[2] + " / " + data.values[2]);
        telemetry.addData("Color", data);
        try {
            Thread.sleep(10);
        } catch (InterruptedException ex){

        }
    }

    @Override
    public void init(){
        state = new SensorState(hardwareMap, 0, 500000);
        state.registerSensor("color", SensorState.SensorType.COLOR, true, 60);

//        sensor_state = new SensorState(hardwareMap, 50, 0);22
//        sensor_state.registerSensor()

//        SensorState.SensorData data;
        try {
            Thread.sleep(10);
        } catch (InterruptedException ex){}
//        (new Thread(state)).start();
//        new Thread(color_state).start();
//        new Thread(sensor_state).start();
    }
}