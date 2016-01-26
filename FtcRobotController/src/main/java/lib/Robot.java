package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.HashMap;

/**
 * Created by luke on 10/7/15.
 */

public class Robot {
    // SensorState to store sensor values. Universal access point.
    public static SensorState state;

    // We really only ever need one gyro, so we can keep it here.
    public static String gyroName;

    // Hardware map pulls device Objects from the robot.
    private static HardwareMap hmap;

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
                throw new RuntimeException("Grabbing servo from HardwareMap failed somehow.");
            }

            servos.put(servoName, s);
        } else {
            throw new RuntimeException("Servo already registered.");
        }
    }

    public static void registerMotor (String motorName) {
        DcMotor m;

        if (!motors.keySet().contains(motorName)){
            try {
                m = hmap.dcMotor.get(motorName);
                m.setPower(0.0);
            } catch (Exception ex){
                throw new RuntimeException("Grabbing motor from HardwareMap failed somehow.");
            }

            motors.put(motorName, m);
        } else {
            throw new RuntimeException("Motor already registered.");
        }
    }

    public static void registerUltrasonic (String ultraName){
        AnalogInput u;

        if (!ultras.keySet().contains(ultraName)){
            try {
                u = hmap.analogInput.get(ultraName);
            } catch (Exception ex){
                throw new RuntimeException("Grabbing AnalogInput ultrasonic from HardwareMap failed somehow.");
            }

            ultras.put(ultraName, u);
        } else {
            throw new RuntimeException("Ultrasonic already registered.");
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
            throw new RuntimeException("In setServoPosition, the servo has not been registered.");
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

    public static void pushButton(String servoName) throws InterruptedException{
        servos.get(servoName).setPosition(0);
            Thread.sleep(1200);

        servos.get(servoName).setPosition(1);

            Thread.sleep(1200);
        servos.get("buttonPusher").setPosition(0.5);

    }

    public static void pushButton(String servoName, int duration) throws InterruptedException{
        servos.get(servoName).setPosition(0);
        Thread.sleep(duration);

        servos.get(servoName).setPosition(0.5);
    }

    public static void tillColor(SensorState.ColorType color, String colorName, double power) throws InterruptedException{
        drivetrain.move(power, waiter);

        while (!(state.getColorData(colorName) == color) && waiter.opModeIsActive()){
            waiter.waitOneFullHardwareCycle();
        }

        drivetrain.stopMove();
    }

    public static void colorSweep   (SensorState.ColorType color,
                                     String bottomName,
                                     String topName,
                                     double power) throws InterruptedException {

        tillColor(SensorState.ColorType.WHITE, bottomName, power);
//        tillColor(SensorState.)

    }


    /*
    // tillSense for colors. If the first color we detect is the color argument (our teams color)
    // Then we will hit that button.
    // REWRITE TO USE COLOR
    // Otherwise, we go to the next light.
    public static void colorSweep(SensorState.ColorType color,
                                  String lightname,
                                  String colorname,
                                  double power,
                                  int bumpthresh) throws InterruptedException{

        SensorState.ColorType dominant = state.getColorData(colorname);   // Current dominant color detected

        double reading = 0.0;

        Thread.sleep(20);

        double baseline = state.getAvgSensorData("light");

//        Thread.sleep(300);

//        do {
//            dominant = state.getColorData("color");
////            tel.addData("")
//
//            Thread.sleep(10);
//
//            if (!waiter.opModeIsActive()){
//                return;
//            }
//        } while (!(dominant == SensorState.ColorType.BLUE || dominant == SensorState.ColorType.RED) && waiter.opModeIsActive());
//        drivetrain.stopMove();
//
//        Thread.sleep(300);
//
//
//        if(color == SensorState.ColorType.RED)
//            drivetrain.moveDistance(-power, 5, this.waiter);

//        Thread.sleep(300);

        drivetrain.move(.75 * power, waiter);
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
        drivetrain.moveDistanceWithCorrections(.3, 2, waiter);
        Thread.sleep(300);


//        drivetrain.moveDistance(power, 4);
//        Thread.sleep(300);

//
//        double correctScoot = 0 ;
//        double wrongScoot = 2 ;
//
//
//
//        if (dominant == color) {
////            drivetrain.moveDistance(power, 3);
//            tel.addData("Color", "First");
//        } else {
//            tel.addData("Color", "Second");
//            Thread.sleep(300);
//        }


//        this.pushButton("buttonPusher", 1500);
        servos.get("climberDropper").setPosition(0.3);
        Thread.sleep(2000);
        servos.get("climberDropper").setPosition(0.85);
//        this.pushButton("buttonPusher");
        Thread.sleep(300);
    }
    */






    /*
     ***********************
     UNUSED ULTRASONIC CODE
     ************************
     */

    /*
    public static void tillSense(String sensorName, double servoPosition, double power, int distance, int filterlength, boolean overshootExit) throws InterruptedException{
//        PID ultraPID = new PID(0.05, 0.02, 0, true, 0.2);
//        ultraPID.setTarget(distance);
//        ultraPID.setMinOutput(-power);
////        ultraPID.setMaxOutput(power);
//        ultraservohelper.setPosition(sensorName, servoPosition);
            Thread.sleep(400);
        drivetrain.move(power, "hero", waiter);
        while((Math.abs(distance-state.getAvgSensorData(sensorName)) > 0.5) && waiter.opModeIsActive() ){
//            power = ultraPID.update(state.getAvgSensorData(sensorName));
//            drivetrain.mover.setPower(power);

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
    */
}
