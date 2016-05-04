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
import com.qualcomm.robotcore.util.Range;

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
    public static AdafruitColorSensor groundColorSensor;
    public static AdafruitColorSensor beaconColorSensor;
//    private static Wire mux;
    public static Wire mux;

    // Prevents instantiation
    private Robot(){}

    public static void init (HardwareMap hmap,
                             Telemetry tel,
                             LinearOpMode waiter,
                             DriveTrain drivetrain,
                             String gyroName) throws InterruptedException {
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

        tillLimitSwitch(limitName, servoName, power, positionActive, positionInactive, killTime, 0.05, false);
    }
    public static void tillLimitSwitch  (String limitName,
                                         String servoName,
                                         double power,
                                         double positionActive,
                                         double positionInactive,
                                         int killTime,
                                         double pressTimeOut,
                                         boolean inverted
    ) throws InterruptedException {

        ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
        ElapsedTime pressTimer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
        hmap.servo.get(servoName).setPosition(positionActive);
        drivetrain.move(power, waiter);

        timer.reset();
        pressTimer.reset();
        drivetrain.mover.divisor = 1.5;
        while (pressTimer.time() < pressTimeOut && timer.time() < killTime && waiter.opModeIsActive()){
            waiter.waitOneFullHardwareCycle();
            if(hmap.digitalChannel.get(limitName).getState() == inverted) {
                pressTimer.reset();
            }
        }
        drivetrain.mover.divisor = 1;

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
            timeOutReached = pressTimer.time() > 5.0;
        }

        Robot.servos.get("buttonPusher").setPosition(0.5);

        return timeOutReached;
    }

    public static void partiallyRetractButtonPusher() throws InterruptedException {
        Robot.servos.get("buttonPusher").setPosition(1);
        Thread.sleep(500);
        Robot.servos.get("buttonPusher").setPosition(0.5);
    }

    public static class ExtendButtonPusherThread implements Runnable {
        @Override
        public void run() {
            Robot.servos.get("buttonPusher").setPosition(0);
            try {
                Thread.sleep(1500);
            }
            catch(InterruptedException ie) {
                ie.printStackTrace();
            }
            Robot.servos.get("buttonPusher").setPosition(0.5);
        }
    }
    public static class RetractButtonPusherThread implements Runnable {
        @Override
        public void run() {
            DigitalChannel endStop = hmap.digitalChannel.get("buttonPusherEndStop");
            Robot.servos.get("climberDropper").setPosition(0.8);
            Robot.servos.get("buttonPusher").setPosition(1);

            ElapsedTime pressTimer = new ElapsedTime();
            pressTimer.reset();

            while(!endStop.getState() && Robot.waiter.opModeIsActive() && pressTimer.time() <= 7) {
                Robot.servos.get("buttonPusher").setPosition(1);
                try {
                    Thread.sleep(1);
                }
                catch(InterruptedException ie) {
                    break;
                }
            }

            Robot.servos.get("buttonPusher").setPosition(0.5);
            Robot.servos.get("climberDropper").setPosition(1);
        }
    }

    public static void retractButtonPusher() throws InterruptedException {
        DigitalChannel endStop = hmap.digitalChannel.get("buttonPusherEndStop");
        Robot.servos.get("climberDropper").setPosition(0.8);
        Robot.servos.get("buttonPusher").setPosition(1);

        ElapsedTime pressTimer = new ElapsedTime();
        pressTimer.reset();

        while(!endStop.getState() && Robot.waiter.opModeIsActive() && pressTimer.time() <= 7) {
            Robot.servos.get("buttonPusher").setPosition(1);
            Thread.sleep(1);
        }

        Robot.servos.get("buttonPusher").setPosition(0.5);
        Robot.servos.get("climberDropper").setPosition(1);
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
        ElapsedTime totaltimer = new ElapsedTime();
        totaltimer.reset();
        Robot.drivetrain.move(direction * .2, Robot.waiter);
        final double BEFORE_RAMP_UP = 1;
        totaltimer.reset();
        while((presstimer.time() <= .2) && Robot.waiter.opModeIsActive()) {
            Robot.tel.addData("timer", presstimer.time());
            if(beaconToucher.getState() == false) { // switch is depressed :(
                Robot.servos.get("buttonPusher").setPosition(0.2); // less gently push, but still kinda gently
                presstimer.reset(); // hold presstimer at 0
                if(totaltimer.time() > BEFORE_RAMP_UP + 5){
                    break;
                }
                if(totaltimer.time()>BEFORE_RAMP_UP){
                    Robot.drivetrain.move(Range.clip(direction*.2 + direction*( (totaltimer.time() - BEFORE_RAMP_UP)/5),-1,1),waiter);
                }

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

        int pot = 0;
        while(beaconToucher.getState() && !waiter.opModeIsActive() ) {
            tel.addData("Delay", pot);
//            Robot.tel.addData("beacon r", beacon.red() + "  g: " + beacon.green() + "  b: " + beacon.blue() + "  alpha: " + beacon.alpha());
//            Robot.tel.addData("ground r", ground.red() + "  g: " + ground.green() + "  b: " + ground.blue() + "  alpha: " + ground.alpha());
            Robot.tel.addData("gyro", Robot.state.getSensorReading("hero"));
//            Robot.tel.addData("beacon RedVsBlue", Robot.state.redVsBlue("beacon"));
            Robot.tel.addData("beacon limit",Robot.hmap.digitalChannel.get("beaconToucher").getState());
            Robot.tel.addData("left limit", Robot.hmap.digitalChannel.get("leftLimit").getState());
            Robot.tel.addData("right limit", Robot.hmap.digitalChannel.get("rearLimit").getState());


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
        if(atBeacon != SensorState.ColorType.NONE){
            return atBeacon;
        }
        else{
            return runUp;
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
        //At home with sensoqr print we saw peaks at about 1000-1100
        //we tuned it to about 600 in the code
        //At meadow room with sensor print 1400-1500
        //and in code tuned to 725

        int RED_THRESH = 750;
        int GREEN_THRESH = 750;
        int BLUE_THRESH = 750 ;
        int maxRed = 0;
        int maxGreen = 0;
        int maxBlue = 0;


        SensorState.ColorType dominant = null;

        waiter.waitOneFullHardwareCycle();

        Thread.sleep(10);
        drivetrain.move(power, waiter);
        do {
            while(!groundColorSensor.isColorUpdate());

            if (dominant == null && (state.redVsBlue(beaconName) == SensorState.ColorType.RED || state.redVsBlue(beaconName) == SensorState.ColorType.BLUE)) {
                dominant = state.redVsBlue(beaconName);
            }
            Thread.sleep(1);
        } while ((groundColorSensor.getRed() <= RED_THRESH ) ||
                (groundColorSensor.getGreen() <= GREEN_THRESH) ||
                (groundColorSensor.getBlue() <= BLUE_THRESH));

        drivetrain.stopMove();
        Thread.sleep(20);
        return dominant;

    }

    public static SensorState.ColorType tillWhiteJumpThresh(double power, String groundName, String beaconName, String colorToIgnore) throws InterruptedException {
        //At home with sensoqr print we saw peaks at about 1000-1100
        //we tuned it to about 600 in the code
        //At meadow room with sensor print 1400-1500
        //and in code tuned to 725


        SensorState.ColorType dominant = null;

        Robot.mux = new Wire(hmap, "mux", 2*0x70);
        groundColorSensor = new AdafruitColorSensor(hmap, "ground", "cdim", -1, 0, mux);
        beaconColorSensor = new AdafruitColorSensor(hmap, "beacon", "cdim", -1, 1, mux);

        int RED_THRESH = 100;
        int GREEN_THRESH = 100;
        int BLUE_THRESH = 100;
        while(!groundColorSensor.isColorUpdate());
        int prevRed = 0;
        int prevGreen = 0;
        int prevBlue = 0;
        int redDiff = 0;
        int greenDiff = 0;
        int blueDiff = 0;

        for (int i = 0; i < 5; i++){
            groundColorSensor.isColorUpdate();
            prevRed += groundColorSensor.getRed();
            prevGreen += groundColorSensor.getGreen();
            prevBlue += groundColorSensor.getBlue();
            Thread.sleep(10);
        }

        prevRed /= 5;
        prevGreen /= 5;
        prevBlue /= 5;

        SensorState.ColorType tempdominant;
        waiter.waitOneFullHardwareCycle();
        //
        Thread.sleep(10);
        drivetrain.move(power, waiter);
        drivetrain.mover.divisor = 1.5;
        do {
            if(groundColorSensor.isColorUpdate()) {
                if(dominant == null){
                    tempdominant = state.redVsBlueJumpThresh(beaconName);
                    if ((tempdominant == SensorState.ColorType.RED || tempdominant == SensorState.ColorType.BLUE)) {
                        dominant = tempdominant;
                    }
                }

                redDiff = groundColorSensor.getRed() - prevRed;
                greenDiff = groundColorSensor.getGreen() - prevGreen;
                blueDiff = groundColorSensor.getBlue() - prevBlue;
                tel.addData("red_jump", redDiff);
                tel.addData("green_jump", greenDiff);
                tel.addData("blue_jump", blueDiff);

                prevRed = groundColorSensor.getRed();
                prevGreen = groundColorSensor.getGreen();
                prevBlue = groundColorSensor.getBlue();
            }
            Thread.sleep(20);
        } while ((redDiff <= RED_THRESH ) ||
                (greenDiff <= GREEN_THRESH) ||
                (blueDiff <= BLUE_THRESH));

        drivetrain.stopMove();
        drivetrain.mover.divisor = 1;

//        while (waiter.opModeIsActive()) {
//            tel.addData("red_jump", redDiff);
//            tel.addData("green_jump", greenDiff);
//            tel.addData("blue_jump", blueDiff);
//        }

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

    public static void openSkirts() {
        Robot.setServoPosition("rightZipline", 0.43);
        Robot.setServoPosition("leftZipline", 0.5);
    }

    public static void closeSkirts() {
        Robot.setServoPosition("rightZipline", 0.88);
        Robot.setServoPosition("leftZipline", 0.11);
    }
}
