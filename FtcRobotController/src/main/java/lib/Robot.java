package lib;

import android.hardware.Sensor;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.io.FileNotFoundException;
import java.util.HashMap;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by luke on 10/7/15.
 */

public class Robot {
    // SensorState to store sensor values. Universal access point.
    public static SensorState state;

    // Thread to run the state class.
    public static Thread state_thread;

    // We really only ever need one gyro, so we can keep it here.
    public static String gyroName;

    // Hardware map pulls device Objects from the robot.
    public static HardwareMap hmap;

    // Drivetrain handles functions specific to our drive type (four-wheel, two-wheel, treads, etc).
    public static DriveTrain drivetrain;

    // Telemetry lets any class anywhere print messages through Robot
    public static Telemetry tel;

    // Store the objects corresponding to the devices of the robot (motors, sensors, servos) in HashMaps.
    public static HashMap<String, DcMotor> motors;
    public static HashMap<String, Servo> servos;

    // Store ultrasonics and their corresponding servos. Servos still need to be registered normally, this just
    // provides access to their names.
    public static HashMap<String, AnalogInput> ultras;
    public static HashMap<String, String> ultra_servo;

    // Allows us to detect when the opmode has stopped.
    public static LinearOpMode waiter;

    // Prevents instantiation
    private Robot(){}

    public static void init (HardwareMap hmap,
                             Telemetry tel,
                             LinearOpMode waiter,
                             DriveTrain drivetrain,
                             String gyroName) {
        Robot.hmap = hmap;
        Robot.tel = tel;
        Robot.servos = new HashMap<String, Servo>();
        Robot.motors = new HashMap<String, DcMotor>();
        Robot.waiter = waiter;
        Robot.drivetrain = drivetrain;
        Robot.gyroName = gyroName;
    }

    public static void registerServo(String servoName, double initial_position) {
        Servo s;

        if (!servos.keySet().contains(servoName)){
            try {
                s = hmap.servo.get(servoName);
                s.setPosition(initial_position);
            } catch (Exception ex){
                throw new RuntimeException("Robot.registerServo: Failed to get servo " + servoName + " from HardwareMap.");
            }

            servos.put(servoName, s);
        } else {
            throw new RuntimeException("Robot.registerServo: servo " + servoName + " already registered.");
        }
    }

    public static void registerMotor (String motorName) {
        DcMotor m;

        if (!motors.keySet().contains(motorName)){
            try {
                m = hmap.dcMotor.get(motorName);
                m.setPower(0.0);
            } catch (Exception ex){
                throw new RuntimeException("Robot.registerMotor: Failed to get motor " + motorName + " from HardwareMap.");
            }

            motors.put(motorName, m);
        } else {
            throw new RuntimeException("Robot.registerMotor: motor " + motorName + " already registered.");
        }
    }

    public static void registerUltrasonic (String ultraName){
        AnalogInput u;

        if (!ultras.keySet().contains(ultraName)){
            try {
                u = hmap.analogInput.get(ultraName);
            } catch (Exception ex){
                throw new RuntimeException("Robot.registerUltrasonic: Failed to get ultrasonic " + ultraName + " from HardwareMap.");
            }

            ultras.put(ultraName, u);
        } else {
            throw new RuntimeException("Robot.registerUltrasonic: ultrasonic " + ultraName + " already registered.");
        }
    }

    // In case we ever wanted to change the gyro.
    public static void registerGyro(String gyroName){
        Robot.gyroName = gyroName;
    }

    // Both ultrasonic and servo must have been previously registered with Robot.
    public static void registerUltraServo (String ultraName, String servoName) {
        if (ultras.keySet().contains(ultraName) && servos.keySet().contains(servoName) && !ultra_servo.keySet().contains(ultraName)){
            ultra_servo.put(ultraName, servoName);
        }
    }

    public static void setServoPosition(String servoName, double position) {
        if (servos.keySet().contains(servoName)) {
            servos.get(servoName).setPosition(position / 180.0);
        } else {
            throw new RuntimeException("Robot.setServoPosition: servo " + servoName + " has not been registered.");
        }
    }

    public static void setUltraServo(String ultraName, double position){
        if (ultra_servo.keySet().contains(ultraName)){
            servos.get(ultra_servo.get(ultraName)).setPosition(position / 180.0);
        }
    }

    public static void tillLimitSwitch  (String limitName,
                                         String servoName,
                                         double power,
                                         double positionActive,
                                         double positionInactive,
                                         int killTime // Time until the function is ended
                                        ) throws InterruptedException {
        drivetrain.move(power, waiter);
        ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
        hmap.servo.get(servoName).setPosition(positionActive);
        Thread.sleep(500);

        timer.reset();

        while (!hmap.digitalChannel.get(limitName).getState() && timer.time() < killTime && waiter.opModeIsActive()){
            waiter.waitOneFullHardwareCycle();
        }

        hmap.servo.get(servoName).setPosition(positionInactive);
        drivetrain.stopMove();
        Thread.sleep(500);
    }

    public static void pushButtonBlue(String switchName, SensorState.ColorType color) throws InterruptedException{
        int direction;
        int colorbinary;
        SensorState.ColorType dominant;
        dominant = tillWhite(-.15, "ground", "beacon");
        colorbinary = -1;

        if(dominant == SensorState.ColorType.RED){
            direction = -1;
        }
        else {
            direction = 1;
        }
        DigitalChannel beaconToucher = hmap.digitalChannel.get(switchName);

        if(direction == -1) {
            drivetrain.moveDistanceWithCorrections(0.175, 3);
            Robot.servos.get("climberDropper").setPosition(0.3);
            Thread.sleep(1000);
            Robot.servos.get("climberDropper").setPosition(0.6);
            drivetrain.moveDistanceWithCorrections(-0.175, 3);
        }

        boolean initialContact = false;
        ElapsedTime presstimer = new ElapsedTime();
        Robot.servos.get("buttonPusher").setPosition(0.2); // press button pusher

        while((presstimer.time() <= 0.75 || !initialContact) && Robot.waiter.opModeIsActive()) {
            Robot.tel.addData("timeout", presstimer.time());
            if(beaconToucher.getState()) { // switch is depressed :(
                if(!initialContact)
                    initialContact = true;
                Robot.drivetrain.move(direction * 0.15, Robot.waiter);
                Robot.servos.get("buttonPusher").setPosition(0.5); // stop button pusher
                presstimer.reset(); // hold presstimer at 0
            }
            else { // switch is open
                Robot.drivetrain.stopMove();
                Robot.servos.get("buttonPusher").setPosition(0.2); // press button pusher
            }
            Thread.sleep(10);
        }

        Robot.servos.get("buttonPusher").setPosition(0.5); // stop button pusher

        if(direction == 1) {
            Robot.servos.get("climberDropper").setPosition(0.3);
            Thread.sleep(1000);
            Robot.servos.get("climberDropper").setPosition(0.6);
        }

//        if(direction == 1) {
//            Robot.servos.get("climberDropper").setPosition(0.3);
//            Thread.sleep(1000);
//            Robot.servos.get("climberDropper").setPosition(0.6);
//        }
//        else {
//            Robot.servos.get("buttonPusher").setPosition(0.8);
//            Thread.sleep(500);
//            Robot.servos.get("buttonPusher").setPosition(0.5); // stop button pusher
//
//            tillWhite(colorbinary * .15, "ground", "beacon");
//            while((presstimer.time() <= 1 || !initialContact) && Robot.waiter.opModeIsActive()) {
//                Robot.tel.addData("timeout", presstimer.time());
//                if(beaconToucher.getState()) { // switch is depressed :(
//                    if(!initialContact)
//                        initialContact = true;
//                    Robot.drivetrain.move(colorbinary * 0.15, Robot.waiter);
//                    Robot.servos.get("buttonPusher").setPosition(0.5); // stop button pusher
//                    presstimer.reset(); // hold presstimer at 0
//                }
//                else { // switch is open
//                    Robot.drivetrain.stopMove();
//                    Robot.servos.get("buttonPusher").setPosition(0.2); // press button pusher
//                }
//
//            }
    }

    public static void pushButton(String switchName, SensorState.ColorType color) throws InterruptedException{
        int direction;
        int colorbinary;
        SensorState.ColorType dominant;
        if(color == SensorState.ColorType.RED) {
            dominant = tillWhite(.15, "ground", "beacon");
            colorbinary = 1;
        }
        else if(color == SensorState.ColorType.BLUE) {
            dominant = tillWhite(-.15, "ground", "beacon");
            colorbinary = -1;

        }
        else {
            return;
        }

        if(dominant == SensorState.ColorType.RED){
            direction = -1;
        }
        else {
            direction = 1;
        }
        DigitalChannel beaconToucher = hmap.digitalChannel.get(switchName);

        if(direction == -1) {
            drivetrain.moveDistanceWithCorrections(0.175, 3);
            Robot.servos.get("climberDropper").setPosition(0.3);
            Thread.sleep(1000);
            Robot.servos.get("climberDropper").setPosition(0.6);
            drivetrain.moveDistanceWithCorrections(-0.175, 3);
        }
//
        boolean initialContact = false;
        ElapsedTime presstimer = new ElapsedTime();
        Robot.servos.get("buttonPusher").setPosition(0.2); // press button pusher

        while((presstimer.time() <= 0.75 || !initialContact) && Robot.waiter.opModeIsActive()) {
            Robot.tel.addData("timeout", presstimer.time());
            if(beaconToucher.getState()) { // switch is depressed :(
                if(!initialContact)
                    initialContact = true;
                Robot.drivetrain.move(direction * 0.15, Robot.waiter);
                Robot.servos.get("buttonPusher").setPosition(0.5); // stop button pusher
                presstimer.reset(); // hold presstimer at 0
            }
            else { // switch is open
                Robot.drivetrain.stopMove();
                Robot.servos.get("buttonPusher").setPosition(0.2); // press button pusher
            }
            Thread.sleep(10);
        }

        Robot.servos.get("buttonPusher").setPosition(0.5); // stop button pusher

        if(direction == 1) {
            Robot.servos.get("climberDropper").setPosition(0.3);
            Thread.sleep(1000);
            Robot.servos.get("climberDropper").setPosition(0.6);
        }

//        if(direction == 1) {
//            Robot.servos.get("climberDropper").setPosition(0.3);
//            Thread.sleep(1000);
//            Robot.servos.get("climberDropper").setPosition(0.6);
//        }
//        else {
//            Robot.servos.get("buttonPusher").setPosition(0.8);
//            Thread.sleep(500);
//            Robot.servos.get("buttonPusher").setPosition(0.5); // stop button pusher
//
//            tillWhite(colorbinary * .15, "ground", "beacon");
//            while((presstimer.time() <= 1 || !initialContact) && Robot.waiter.opModeIsActive()) {
//                Robot.tel.addData("timeout", presstimer.time());
//                if(beaconToucher.getState()) { // switch is depressed :(
//                    if(!initialContact)
//                        initialContact = true;
//                    Robot.drivetrain.move(colorbinary * 0.15, Robot.waiter);
//                    Robot.servos.get("buttonPusher").setPosition(0.5); // stop button pusher
//                    presstimer.reset(); // hold presstimer at 0
//                }
//                else { // switch is open
//                    Robot.drivetrain.stopMove();
//                    Robot.servos.get("buttonPusher").setPosition(0.2); // press button pusher
//                }
//
//            }
    }

    public static void scoreEverything(String servoName) throws InterruptedException{
        scoreEverything(servoName, 2000, 100, 2000);
    }

    public static void scoreEverything(String servoName, int outduration, int pause, int induration) throws InterruptedException{
        servos.get(servoName).setPosition(0);
        Robot.servos.get("climberDropper").setPosition(0.3);
        Thread.sleep(outduration);
        Robot.servos.get("climberDropper").setPosition(0.6);
        servos.get(servoName).setPosition(0.5);
        Thread.sleep(pause);
        servos.get(servoName).setPosition(1);
        Thread.sleep(induration);
        servos.get(servoName).setPosition(0.5);
    }

    public static SensorState.ColorType tillColor(String colorName, double power, SensorState.ColorType... colors) throws InterruptedException{
        drivetrain.move(power, waiter);
        SensorState.ColorType real_color = null;

        main:
        while (waiter.opModeIsActive()) {
            real_color = state.getColorData(colorName);

            for (SensorState.ColorType color : colors) {
                if (real_color == color) {
                    break main;
                }
            }

            waiter.waitOneFullHardwareCycle();
        }

        drivetrain.stopMove();
        return real_color;
    }

    public static SensorState.ColorType tillWhite(double power, String groundName, String beaconName) throws InterruptedException {


        ColorSensor ground = hmap.colorSensor.get(groundName);
        SensorState.ColorType dominant = null;
        int threshold = 6;
        drivetrain.move(power, waiter);


        while (!(ground.alpha() >= threshold && ground.red() >= threshold && ground.green() >= threshold && ground.blue() >= threshold) && waiter.opModeIsActive()){


            if (dominant == null && (state.redVsBlue(beaconName) == SensorState.ColorType.RED || state.redVsBlue(beaconName) == SensorState.ColorType.BLUE)) {
                dominant = state.redVsBlue(beaconName);
            }

            Thread.sleep(10);
        }
        drivetrain.stopMove();

        return dominant;

    }


    public static void tillSense(String sensorName, double servoPosition, double power, int distance, int filterlength, boolean overshootExit) throws InterruptedException{

        Thread.sleep(400);
        drivetrain.move(power, waiter);
        while((Math.abs(distance-state.getAvgSensorData(sensorName)) > 0.5) && waiter.opModeIsActive() ){

            tel.addData("AvgUSDistance", state.getAvgSensorData(sensorName));
            Thread.sleep(10);
        }
        drivetrain.stopMove();
    }

    public static void parallel(String sensorNameA, String sensorNameB, double power, double thresh, int filterlength) throws InterruptedException{
        double diff;
        int count = 0;

        do {
            diff = (state.getAvgSensorData(sensorNameA) - state.getAvgSensorData(sensorNameB));

            if(Math.signum(diff) == 1 && count==0) {
                drivetrain.setLeftMotors(-power);
                drivetrain.setRightMotors(power);
            } else if(Math.signum(diff) == -1 && count==0) {
                drivetrain.setLeftMotors(power);
                drivetrain.setRightMotors(-power);
            }

            // If count isn't 0, then we're waiting to see if the robot slips at all, which means
            // We need to keep the motors at 0.
            else{
                drivetrain.setLeftMotors(0);
                drivetrain.setRightMotors(0);
            }

            // Wait to see if the robot slips
            if(Math.abs(diff) < thresh){
                count ++;
            } else{
                count = 0;
            }
                Thread.sleep(5);

        }while (count<15 && waiter.opModeIsActive());

        drivetrain.move(0);

    }
}
