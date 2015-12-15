package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import lib.Filter;
import lib.Robot;
import lib.SensorState;

/**
 * Created by luke on 10/27/15.
 */

public class SensorStateOpMode extends LinearOpMode {
    private Filter filter;
    private Thread state_thread;

    public void runOpMode() throws InterruptedException{
        Robot.state = new SensorState(hardwareMap, 1, 0);
        Robot.state.registerSensor("color", SensorState.SensorType.COLOR, true, 60);
        Robot.state.registerSensor("rearUltra", SensorState.SensorType.ULTRASONIC, true, 60);
        Robot.state.registerSensor("hero", SensorState.SensorType.GYRO, true, 60);
        Robot.state.setUltrasonicPin("ultraToggle");

        state_thread = new Thread(Robot.state);
        state_thread.start();

        waitForStart();

        //Start loop
        while (Robot.state.gyroIsCalibrating("hero")){
            Thread.sleep(10);
            telemetry.addData("calibrating", "");
        }
        Thread.sleep(60);

        filter  = Robot.state.getFilter("rearUltra");
        telemetry.addData("Filter_val", filter.getAvg());
        telemetry.addData("Color_name", Robot.state.getSensorsFromType(SensorState.SensorType.COLOR)[0]);
        telemetry.addData("Color", Robot.state.getColorData("mr"));
        telemetry.addData("Distance", Robot.state.getSensorReading("rearUltra"));
        telemetry.addData("Distance_Avg", Robot.state.getAvgSensorData("rearUltra"));
        try {
            Thread.sleep(10);
        } catch (InterruptedException ex){

        }
    }
}



//public enum SensorType { GYRO, ULTRASONIC, COLOR, LIGHT, ENCODER }
//
//public enum ColorType{ RED, BLUE, WHITE, CLEAR, NONE;
//    public static int toInt(ColorType c)
//    public static int[] toInt(ColorType[] c)
//    public static ColorType toColor(int i){
//    public static ColorType[] toColor(int[] i)
//}
//
//public SensorState(HardwareMap hmap, int milli_interval, int nano_interval)
//public synchronized void registerSensor(String name, SensorType type, boolean update, int data_length)
//public void setUltrasonicPin(String pin_name)
//public boolean gyroIsCalibrating(String gyro_name)
//public synchronized ColorType getColorData(String name)
//public synchronized double getSensorReading(String name)
//public synchronized void changeUpdateStatus(String name, boolean update)
//public synchronized Filter getFilter(String name)
//public synchronized double getAvgSensorData(String name)
//public synchronized String[] getSensorsFromType(SensorType type)
//public void run()
