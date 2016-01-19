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
    public HashMap<String, Servo> servos;
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
        servos.get(name).setPosition(position / 180);
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

    public void tillSense(String sensorName, double servoPosition, double power, int distance, int filterlength) throws InterruptedException{
        PID ultraPID = new PID(0.05, 0.01, 0, true, 0.1);
        ultraPID.setTarget(distance);
        ultraPID.setMinOutput(-1);
        ultraPID.setMaxOutput(1);
        ultraservohelper.setPosition(sensorName, servoPosition);
            Thread.sleep(400);
        drivetrain.move(power, "hero", waiter);
        while(!ultraPID.isAtTarget() && waiter.opModeIsActive()){
            power = ultraPID.update(state.getAvgSensorData(sensorName));
            drivetrain.mover.setPower(power);

            tel.addData("AvgUSDistance", state.getAvgSensorData(sensorName));
            Thread.sleep(10);
        }
        drivetrain.stopMove();
    }

    public void parallel(String sensorNameA, String sensorNameB, double power, double thresh, int filterlength) throws InterruptedException{
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
                Thread.sleep(5);

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
    public void pushButton(String servoName) throws InterruptedException{
        servos.get(servoName).setPosition(0);
            Thread.sleep(1200);

        servos.get(servoName).setPosition(1);

            Thread.sleep(1200);
        servos.get("buttonPusher").setPosition(0.5);

    }

    public void pushButton(String servoName, int duration) throws InterruptedException{
        servos.get(servoName).setPosition(0);
        Thread.sleep(duration);

        servos.get(servoName).setPosition(0.5);


    }

    public void colorSweep(SensorState.ColorType color, String lightname, String colorname, double power, int bumpthresh) throws InterruptedException{


        SensorState.ColorType dominant = state.getColorData(colorname);   // Current dominant color detected
//        this.pushButton("buttonPusher", 1000);

        double reading = 0.0;

        Thread.sleep(20);

        double baseline = state.getAvgSensorData("light");

//        reading = state.getAvgSensorData(lightname);
        if (color == SensorState.ColorType.RED) {
            drivetrain.move(power, "hero", this.waiter);
        }
        else
            drivetrain.move(-power, "hero", this.waiter);

        Thread.sleep(300);

        do {
            dominant = state.getColorData("color");
//            tel.addData("")

            Thread.sleep(1);

            if (!waiter.opModeIsActive()){
                return;
            }
        } while (!(dominant == SensorState.ColorType.BLUE || dominant == SensorState.ColorType.RED) && waiter.opModeIsActive());
        drivetrain.stopMove();

        Thread.sleep(300);


        if(color == SensorState.ColorType.RED)
            drivetrain.moveDistance(-power, 5, this.waiter);

        Thread.sleep(300);

        drivetrain.move(.75 * power, "hero", this.waiter);
        while(waiter.opModeIsActive()){
            reading = state.getSensorReading(lightname);
            Robot.tel.addData("reading", reading);

            if(Math.abs(reading - baseline) > bumpthresh ) {
                Robot.tel.addData("bump detected", "");
                break;
            }
            Thread.sleep(10);
        }

        drivetrain.stopMove();

        Thread.sleep(300);

//        drivetrain.moveDistance(power, 4);
        Thread.sleep(300);

//
//        double correctScoot = 0 ;
//        double wrongScoot = 2 ;
//


        if (dominant == color) {
//            drivetrain.moveDistance(power, 3);
            tel.addData("Color", "First");
        } else {
            tel.addData("Color", "Second");
            Thread.sleep(300);
        }
        this.pushButton("buttonPusher", 1500);
        servos.get("climberDropper").setPosition(0.3);
        this.pushButton("buttonPusher");
        Thread.sleep(300);
    }
}
