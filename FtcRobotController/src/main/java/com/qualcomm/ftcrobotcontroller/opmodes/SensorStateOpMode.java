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
    SensorState.SensorData data;

    @Override
    public void loop(){
//        data = color_state.getSensorDataArray("mr");
        data = state.getSensorDataObject("color");
//        telemetry.addData("r / g / b", data.values[1] + " / " + data.values[2] + " / " + data.values[2]);
        int index = data.index;
        int index1 = data.index - 1;
        int index2 = data.index - 2;
        int len = data.colors.length;

        if (index1 < 0){
            index1 = len + index1;
        }

        if (index2 < 0){
            index2 = len + index2;
        }

//        telemetry.addData("Index", data.index);
//        telemetry.addData("Index1", index1);
//        telemetry.addData("Index2", index2);
        telemetry.addData("Current", data.colors[data.index]);
        telemetry.addData("Color 1", data.colors[index]);
        telemetry.addData("Color 2", data.colors[index1]);
        telemetry.addData("Color 3", data.colors[index2]);
//        telemetry.addData("Test", (-5)%10);
//        telemetry.addData("name", state.getSensorsFromType(SensorState.SensorType.COLOR)[0]);
        try {
            Thread.sleep(10);
        } catch (InterruptedException ex){

        }
    }

    @Override
    public void init(){
        state = new SensorState(hardwareMap, 1, 0);
        state.registerSensor("color", SensorState.SensorType.COLOR, true, 60);

//        sensor_state = new SensorState(hardwareMap, 50, 0);22
//        sensor_state.registerSensor()

//        SensorState.SensorData data;

        (new Thread(state)).start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex){};
        state.changeUpdateStatus("color", true);
//        new Thread(color_state).start();
//        new Thread(sensor_state).start();
    }
}