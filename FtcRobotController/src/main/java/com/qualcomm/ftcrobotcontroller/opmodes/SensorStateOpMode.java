package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import lib.Filter;
import lib.Robot;
import lib.SensorState;

/**
 * Created by luke on 10/27/15.
 */

public class SensorStateOpMode extends OpMode {
    private Filter filter;
    private Thread state_thread;

    @Override
    public void loop(){
        filter  = Robot.state.getFilter("rearUltra");
        telemetry.addData("Filter val", filter.getAvg());
        telemetry.addData("Color name", Robot.state.getSensorsFromType(SensorState.SensorType.COLOR)[0]);
        telemetry.addData("Calibrating", Robot.state.gyroIsCalibrating("hero"));
        telemetry.addData("Color", Robot.state.getColorData("mr"));
        telemetry.addData("Distance", Robot.state.getSensorReading("rearUltra"));
        telemetry.addData("Distance_Avg", Robot.state.getAvgSensorData("rearUltra"));
        filter  = Robot.state.getFilter("rearUltra");

        try {
            Thread.sleep(10);
        } catch (InterruptedException ex){

        }
    }

    @Override
    public void init(){
        Robot.state = new SensorState(hardwareMap, 1, 0);
        Robot.state.registerSensor("color", SensorState.SensorType.COLOR, true, 60);
        Robot.state.registerSensor("ultra", SensorState.SensorType.ULTRASONIC, true, 60);
        Robot.state.registerSensor("hero", SensorState.SensorType.GYRO, true, 60);
        Robot.state.setUltrasonicPin("usPin");

        state_thread = new Thread(Robot.state);
        state_thread.start();
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
