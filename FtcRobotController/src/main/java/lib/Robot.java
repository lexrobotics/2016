package lib;
import android.util.Log;

import com.qualcomm.ftcrobotcontroller.opmodes.ColorSweep;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;
import java.util.HashMap;

/**
 * Created by luke on 10/7/15.
 */


public class Robot {
    public static SensorState state;

    // Hardware map pulls device Objects from the robot.
    // Drivetrain handles functions specific to our drive type (four-wheeld, two-wheel, treads, etc).
    private HardwareMap hmap;
    public DriveTrain drivetrain;
    public static Telemetry tel;
    // Store the objects corresponding to the devices of the robot (motors, sensors, servos) in hashmaps.
    private HashMap<String, Object> motors;
    private HashMap<String, Servo> servos;
    public UltraServoHelper ultraservohelper;
    public static LinearOpMode waiter;

    public Robot (HardwareMap hmap, Telemetry tel, LinearOpMode opm) {

        this.hmap = hmap;
        this.tel = tel;
        this.servos = new HashMap<String, Servo>();
        this.ultraservohelper = new UltraServoHelper();
        waiter = opm;

        // 100 millisecond delay between updates.
//        state = new SensorState(hmap, 100);
//        Thread state_thread = new Thread(state);
//        state_thread.start();
    }

    // If someone tries to get a device not registered in a hashmap.
    public class HardwareNotRegisteredException extends Exception {
        public HardwareNotRegisteredException() { super(); }
        public HardwareNotRegisteredException(String cause) { super(cause); }
    }
    public void registerServo(String servoName) {
        servos.put(servoName, hmap.servo.get(servoName));
    }

    public void setPosition(String name, int position) {
        servos.get(name).setPosition(position/180);
    }

    // REGISTRATION FUNCTIONS
    // It makes more sense to have the opmode construct a drivetrain and pass it to Robot than to repeat
    // constructors in Robot and the drivetrain classes.
    public void registerDriveTrain(DriveTrain d){
        this.drivetrain = d;
    }

    public void registerUltrasonicServo(String sensorName, String servoName) {
        if(servos.containsKey(servoName)) {
            ultraservohelper.registerServo(sensorName, servos.get(servoName));
        }
        else{
            this.registerServo(servoName);
            ultraservohelper.registerServo(sensorName, servos.get(servoName));
        }
    }

    public void tillSenseTowards(String sensorName, int servoPosition, double power, int distance, int filterlength) {
        ultraservohelper.setPosition(sensorName,servoPosition);
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        drivetrain.move(power);
        while(state.getAvgSensorData(sensorName,filterlength) >= distance && waiter.opModeIsActive()){
            Log.i("AvgUSDistance", "" + state.getAvgSensorData(sensorName, filterlength));
            try{
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        drivetrain.move(0);
    }

    public void parallel(String sensorNameA, String sensorNameB, double power, double thresh, int filterlength) {
        double diff;
        int count =0;
        do{
            tel.addData("frontAvg", Robot.state.getAvgSensorData("frontUltra", 60));
            tel.addData("rearAvg", Robot.state.getAvgSensorData("rearUltra", 60));
            tel.addData("frontReading", Robot.state.getSensorReading("frontUltra"));
            tel.addData("rearReading", Robot.state.getSensorReading("rearUltra"));

            diff = (state.getAvgSensorData(sensorNameA, filterlength) - state.getAvgSensorData(sensorNameB,filterlength));
            if(Math.signum(diff) == 1 && count==0){
                drivetrain.setLeftMotors(-power);
                drivetrain.setRightMotors(power);
            }
            else if(Math.signum(diff) == -1 && count==0) {
                drivetrain.setLeftMotors(power);
                drivetrain.setRightMotors(-power);
            }
            else{
                drivetrain.setLeftMotors(0);
                drivetrain.setRightMotors(0);
            }

            if(Math.abs(diff) < thresh){
                count ++;
            }
            else{
                count = 0;
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while (count<15 && waiter.opModeIsActive());

        drivetrain.move(0);

    }
    public void tillSenseAway(String sensorName,int servoPosition, double power, int distance, int filterlength){
        ultraservohelper.setPosition(sensorName,servoPosition);
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        drivetrain.move(power);
        while(state.getAvgSensorData(sensorName,filterlength) <= distance && waiter.opModeIsActive()){
            Log.i("AvgUSDistance", "" + state.getAvgSensorData(sensorName, filterlength));
            try{
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        drivetrain.move(0);
    }

    // This just gets the color reading from the color sensor. We can really only use it in one way,
    // so it doesn't really need its own class.
//    public String getDominantColor(String colorname) {
//        ColorSensor sen = (ColorSensor) sensors.get("color_sensor");
//        int r = sen.red(), b = sen.blue(), g = sen.green();
//
//        if ((r > 0) && (b + g == 0))
//            return "red";
//        if ((b > 0) && (r + g == 0))
//            return "blue";
//        if ((r == 1) && (b == 1) && (g == 1))
//            return "white";
//        if (r + g + b == 0)
//            return "clear";
//        return "none";
//    }

    // tillSense for colors. If the first color we detect is the color argument (our teams color)
    // Then we will hit that button.
    // Otherwise, we go to the next light.
    public void colorSweep(SensorState.ColorType color, double low_threshold, double high_threshold, String lightname, String colorname, double power) {
        // TODO: maybe add argument so that it can average over a specific number of points
        // average + low_threshold < Light detected < average + high_threshold


        SensorState.ColorType stored_color = SensorState.ColorType.NONE;               // First detected color
        SensorState.ColorType dominant = state.getColorData(colorname);   // Current dominant color detected
        double average = 0.0;                     // Average of light values
        double reading = 0.0;
//        int count = 0;

        drivetrain.move(power);

//        do {
//            if (!waiter.opModeIsActive()){
//                return;
//            }
//            dominant = state.getColorData(colorname);
//            tel.addData("Current color", dominant);
////            count++;
//            try {
//                Thread.sleep(0, 500000);
//            } catch (InterruptedException ex){}
//        } while (!(dominant == SensorState.ColorType.RED || dominant == SensorState.ColorType.BLUE));
//        drivetrain.move(0.0);


//        while (true){
//            tel.addData("Found color", dominant);
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        average = state.getAvgSensorData(lightname, 10);


        while (waiter.opModeIsActive()) {
            reading = state.getAvgSensorData(lightname, 10);
            tel.addData("Reading", reading);
            tel.addData("Average", average);

            if (average + low_threshold <= reading && reading <= average + high_threshold){
                break;
            }
            try{
                Thread.sleep(1);
            } catch (InterruptedException ex){}
        }
        drivetrain.move(0.0);

        for (int i = 0; i < 4000; i++){
            tel.addData("Reading", reading);
            tel.addData("Average", average);
            if (!waiter.opModeIsActive())
                break;
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex){}
        }
//
////        while (true){
////            tel.addData("test", "tester");
////            try {
////                Thread.sleep(10);
////            }
////            catch (InterruptedException ex){}
////        }
//
////        drivetrain.move(0f);
////        while (true){
////            tel.addData("test", "test");
////            tel.addData("Average", average);
////            tel.addData("Current average", state.getAvgSensorData(lightname, 10));
////            try {
////                Thread.sleep(10);
////            } catch (InterruptedException ex){}
//////            tel.addData("Worked", "yay");
////
////        }
//
//        drivetrain.move(0.0);
////
////        // It seems like the conversion is necessary because drivetrain was declared as the abstract parent DriveTrain.
////        // First color detected is team color, so get that button.
        if (dominant == color){
            tel.addData("Color", "CORRECT");
//            drivetrain.move(0.0);
//            ((TwoWheelDrive)drivetrain).moveDistance(0.18, 8);
        }

        // First color detected is wrong color, so hit other button, which must be the right button.
        else {
            tel.addData("Color", "WRONG");
//            drivetrain.move(0.0);
//            ((TwoWheelDrive)drivetrain).moveDistance(-0.18, 8);
        }

    }
}
