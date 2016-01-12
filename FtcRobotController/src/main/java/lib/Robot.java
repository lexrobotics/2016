package lib;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
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
    public HashMap<String, DcMotor> motors;
    private HashMap<String, Servo> servos;
    public UltraServoHelper ultraservohelper;
    public static LinearOpMode waiter;

    public Robot (HardwareMap hmap, Telemetry tel, LinearOpMode opm) {
        this.hmap = hmap;
        this.tel = tel;
        this.servos = new HashMap<String, Servo>();
        this.motors = new HashMap<String, DcMotor>();
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

    public void registerServo(String servoName, double initial_position) {
        if (!servos.keySet().contains(servoName)){
            hmap.servo.get(servoName).setPosition(initial_position);
            servos.put(servoName, hmap.servo.get(servoName));
        }
    }

    public void registerMotor (String motorName, double initial_power) {
        if (!motors.containsKey(motorName)){
            hmap.dcMotor.get(motorName).setPower(initial_power);
            motors.put(motorName, hmap.dcMotor.get(motorName));
        }
    }

    public void registerMotor (String motorName) {
        registerMotor(motorName, 0.0);
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

    public void registerUltrasonicServo(String sensorName, String servoName, double center) {
        if(servos.containsKey(servoName)) {
            ultraservohelper.registerServo(sensorName, servos.get(servoName), center);
        }
        else{
            this.registerServo(servoName, 0);
            ultraservohelper.registerServo(sensorName, servos.get(servoName), center);
        }
    }

    public void tillSense(String sensorName, int servoPosition, double power, int distance, int filterlength) {
        PID ultraPID = new PID(0.05, 0.005, 0, false,0.1);
        ultraPID.setTarget(distance);
        ultraPID.setMinOutput(-1);
        ultraPID.setMaxOutput(1);
        ultraservohelper.setPosition(sensorName, servoPosition);
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        drivetrain.move(power,"hero",waiter);
        while(!ultraPID.isAtTarget() && waiter.opModeIsActive()){
            power = ultraPID.update(state.getAvgSensorData(sensorName));
            drivetrain.move(power);

            Log.i("AvgUSDistance", "" + state.getAvgSensorData(sensorName));
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
            tel.addData("frontAvg", Robot.state.getAvgSensorData("frontUltra"));
            tel.addData("rearAvg", Robot.state.getAvgSensorData("rearUltra"));
            tel.addData("frontReading", Robot.state.getSensorReading("frontUltra"));
            tel.addData("rearReading", Robot.state.getSensorReading("rearUltra"));

            diff = (state.getAvgSensorData(sensorNameA) - state.getAvgSensorData(sensorNameB));
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

    // tillSense for colors. If the first color we detect is the color argument (our teams color)
    // Then we will hit that button.
    // Otherwise, we go to the next light.
//    public void colorSweep(SensorState.ColorType color, double low_threshold, double high_threshold, String lightname, String colorname, double power) {
//
//        SensorState.ColorType stored_color = SensorState.ColorType.NONE;               // First detected color
//        SensorState.ColorType dominant = state.getColorData(colorname);   // Current dominant color detected
//        double average = 0.0;                     // Average of light values
//        double reading = 0.0;
//
////        drivetrain.move(power,"hero", waiter);
//
//        average = state.getAvgSensorData(lightname);
//
//        while (waiter.opModeIsActive()) {
//            reading = state.getAvgSensorData(lightname);
//            tel.addData("Reading", reading);
//            tel.addData("Average", average);
//
//            if (average + low_threshold <= reading && reading <= average + high_threshold){
////                break;
//            }
//            try{
//                Thread.sleep(1);
//            } catch (InterruptedException ex){}
//        }
//
//        drivetrain.stopMove();
//
//
//        if (dominant == color){
//            tel.addData("Color", "CORRECT");
////            drivetrain.move(0.0);
////            ((TwoWheelDrive)drivetrain).moveDistance(0.18, 8);
//        }
//
//        // First color detected is wrong color, so hit other button, which must be the right button.
//        else {
//            tel.addData("Color", "WRONG");
////            drivetrain.move(0.0);
////            ((TwoWheelDrive)drivetrain).moveDistance(-0.18, 8);
//        }
//    }
    public void colorSweep(SensorState.ColorType color, String lightname, String colorname, double power) {

        SensorState.ColorType stored_color = SensorState.ColorType.NONE;               // First detected color
        SensorState.ColorType dominant = state.getColorData(colorname);   // Current dominant color detected
        double average = 0.0;                     // Average of light values
        double reading = 0.0;

//        drivetrain.move(power,"hero", waiter);

        reading = state.getAvgSensorData(lightname);
        drivetrain.move(power,"hero", this.waiter);

        int bump_counter =0;
        double prev_reading = reading;
        boolean bump_flag = false;
        while(bump_counter<3 && waiter.opModeIsActive()){
            reading = state.getAvgSensorData(lightname);
            Robot.tel.addData("reading",reading);

            if(reading-prev_reading>0.5 && bump_flag == false){
                Robot.tel.addData("bump detected","");
                bump_flag= true;
                bump_counter ++;
            }
            if(reading-prev_reading<=0){
                bump_flag= false;
            }
            prev_reading = reading;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        drivetrain.stopMove();


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
