package lib;

import android.hardware.Sensor;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
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

        ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
        hmap.servo.get(servoName).setPosition(positionActive);
        Thread.sleep(100);
        drivetrain.move(power, waiter);

        timer.reset();

        while (!hmap.digitalChannel.get(limitName).getState() && timer.time() < killTime && waiter.opModeIsActive()){
            waiter.waitOneFullHardwareCycle();
        }

        hmap.servo.get(servoName).setPosition(positionInactive);
        drivetrain.stopMove();
        Thread.sleep(100);
    }

    public static void dumpClimbers() throws InterruptedException {
        Robot.servos.get("climberDropper").setPosition(0);
        Thread.sleep(1000);
        Robot.servos.get("climberDropper").setPosition(1);
    }

    public static void dumpClimbers(int scootLength) throws InterruptedException {
        if(scootLength == 0) {
            dumpClimbers();
            return;
        }

        int direction = (int) Math.signum(scootLength);
        scootLength = Math.abs(scootLength);

        Robot.drivetrain.moveDistanceWithCorrections(direction * 0.15, scootLength);
        dumpClimbers();
        Robot.drivetrain.moveDistanceWithCorrections(direction * -0.15, scootLength);
    }

    public static boolean extendTillBeacon(String switchName) throws InterruptedException {
        boolean timeOutReached = false;

        Robot.drivetrain.stopMove();

        DigitalChannel beaconToucher = hmap.digitalChannel.get(switchName);
        Robot.servos.get("buttonPusher").setPosition(0); // press button pusher

        ElapsedTime pressTimer = new ElapsedTime();
        pressTimer.reset();

        while(beaconToucher.getState() && Robot.waiter.opModeIsActive() && !timeOutReached) {
            Thread.sleep(50);
            timeOutReached = pressTimer.time() > 4.0;
        }

        Robot.servos.get("buttonPusher").setPosition(0.5);

        return timeOutReached;
    }

    public static void retractButtonPusher() throws InterruptedException {
        DigitalChannel endStop = hmap.digitalChannel.get("buttonPusherEndStop");

        Robot.servos.get("buttonPusher").setPosition(1);

        ElapsedTime pressTimer = new ElapsedTime();
        pressTimer.reset();

        while(!endStop.getState() && Robot.waiter.opModeIsActive() && pressTimer.time() <= 7) {
            Robot.servos.get("buttonPusher").setPosition(1);
            Thread.sleep(1);
        }

        Robot.servos.get("buttonPusher").setPosition(0.5);
    }

    public static SensorState.ColorType tillColor(String sensorName, int direction) throws InterruptedException{
        drivetrain.move(direction * .175, waiter);
        SensorState.ColorType dominant;
        do {
            dominant = state.redVsBlue(sensorName);
            Thread.sleep(10);
        } while(dominant != SensorState.ColorType.RED && dominant != SensorState.ColorType.BLUE);
        drivetrain.stopMove();
        return dominant;

    }

    public static void pushButton(String switchName, int direction ) throws InterruptedException {
        if(direction == 0)
            return;

        DigitalChannel beaconToucher = hmap.digitalChannel.get(switchName);

        ElapsedTime presstimer = new ElapsedTime();
        Robot.drivetrain.move(direction * 0.15, Robot.waiter);
        while((presstimer.time() <= 0.15) && Robot.waiter.opModeIsActive()) {
            Robot.tel.addData("timer", presstimer.time());
            if(beaconToucher.getState() == false) { // switch is depressed :(
                Robot.servos.get("buttonPusher").setPosition(0.25); // less gently push, but still kinda gently
                presstimer.reset(); // hold presstimer at 0
            }
            else { // switch is open
                Robot.servos.get("buttonPusher").setPosition(0); // press button pusher
            }
            Thread.sleep(10);
        }
        Robot.drivetrain.stopMove();
        Robot.servos.get("buttonPusher").setPosition(0.5); // stop button pusher
        Thread.sleep(10);
    }


    public static double delaySet(String potName,String switchName) throws InterruptedException {
        DigitalChannel beaconToucher = hmap.digitalChannel.get(switchName);
        ColorSensor beacon = Robot.hmap.colorSensor.get("beacon");
//        ColorSensor ground = Robot.hmap.colorSensor.get("ground");

        int pot = 0;
        while(beaconToucher.getState() && !waiter.opModeIsActive() ) {
            pot = (int) Math.floor((1023 - hmap.analogInput.get(potName).getValue())/1023.0 * 15.0);
            tel.addData("Delay", pot);
            Robot.tel.addData("beacon r", beacon.red() + "  g: " + beacon.green() + "  b: " + beacon.blue() + "  alpha: " + beacon.alpha());
//            Robot.tel.addData("ground r", ground.red() + "  g: " + ground.green() + "  b: " + ground.blue() + "  alpha: " + ground.alpha());
            Robot.tel.addData("gyro", Robot.state.getSensorReading("hero"));
//            Robot.tel.addData("beacon RedVsBlue", Robot.state.redVsBlue("beacon"));
            Robot.tel.addData("beacon limit",Robot.hmap.digitalChannel.get("beaconToucher").getState());
            Robot.tel.addData("left limit", Robot.hmap.digitalChannel.get("leftLimit").getState());
            Robot.tel.addData("right limit",Robot.hmap.digitalChannel.get("rightLimit").getState());


            Thread.sleep(10);
        }
        tel.addData("Delay (LOCKED)", pot);
        return pot;
    }

    public static SensorState.ColorType oppositeDominantColorFusion(SensorState.ColorType runUp, SensorState.ColorType atBeacon){
        if(runUp == SensorState.ColorType.NONE){
            return atBeacon;
        }
        if(atBeacon == SensorState.ColorType.NONE){
            if(runUp == SensorState.ColorType.RED) {
                return SensorState.ColorType.BLUE;
            }
            else if(runUp == SensorState.ColorType.BLUE)  {
                return SensorState.ColorType.RED;
            }
            else{
                return SensorState.ColorType.NONE;
            }
        }
        else{
            return atBeacon;
        }
    }
    public static SensorState.ColorType sameDominantColorFusion(SensorState.ColorType runUp, SensorState.ColorType atBeacon){
        if(runUp == SensorState.ColorType.NONE){
            return atBeacon;
        }
        if(atBeacon == SensorState.ColorType.NONE){
            return runUp;
        }
        else{
            return atBeacon;
        }
    }

    public static void delayWithCountdown(int seconds) throws InterruptedException {
        for(int i=seconds; i>0; i--) {
            tel.addData("Countdown", i);
            Thread.sleep(1000);
        }
    }

    public static SensorState.ColorType tillWhite(double power, String groundName, String beaconName) throws InterruptedException {
        return tillWhite(power, groundName, beaconName, "");
    }

    public static SensorState.ColorType tillWhite(double power, String groundName, String beaconName, String colorToIgnore) throws InterruptedException {
        int RED_THRESH = 600;
        int GREEN_THRESH = 600;
        int BLUE_THRESH = 600;
        int maxRed = 0;
        int maxGreen = 0;
        int maxBlue = 0;
        if(!colorToIgnore.equals("red")){
            RED_THRESH = -1;
        }
        if(!colorToIgnore.equals("blue")){
            BLUE_THRESH = -1;
        }


        AdafruitColorSensor ground = new AdafruitColorSensor(Robot.hmap, groundName, "cdim", 5);
        SensorState.ColorType dominant = null;

        waiter.waitOneFullHardwareCycle();
        //
        Thread.sleep(10);
        drivetrain.move(power, waiter);
        do {
            while(!ground.isColorUpdate());

            if (dominant == null && (state.redVsBlue(beaconName) == SensorState.ColorType.RED || state.redVsBlue(beaconName) == SensorState.ColorType.BLUE)) {
                dominant = state.redVsBlue(beaconName);
            }
            Thread.sleep(1);
        } while ((ground.getRed() <= RED_THRESH ) ||
                (ground.getGreen() <= GREEN_THRESH) ||
                (ground.getBlue() <= BLUE_THRESH));

        drivetrain.stopMove();
        Thread.sleep(20);
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
