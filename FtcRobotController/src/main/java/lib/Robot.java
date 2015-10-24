package lib;
import com.qualcomm.ftcrobotcontroller.opmodes.ColorSweep;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.Hardware;
import java.util.HashMap;

//import  com.qualcomm.robotcore.eventloop.opmode.LinearOpMode.*;

/**
 * Created by luke on 10/7/15.
 */

public class Robot {

    // Hardware map pulls device Objects from the robot.
    // Drivetrain handles functions specific to our drive type (four-wheeld, two-wheel, treads, etc).
    private HardwareMap hmap;
    private DriveTrain drivetrain;
    private Telemetry tel;
    private LinearOpMode opm;

    // Store the objects corresponding to the devices of the robot (motors, sensors, servos) in hashmaps.
    private HashMap<String, Object> motors;

    public HashMap<String, Object> getSensors() { return sensors; }

    private HashMap<String, Object> sensors;
    private HashMap<String, Object> servos;

    public Robot (HardwareMap hmap, Telemetry tel, LinearOpMode opm) {

        this.hmap = hmap;
        this.sensors = new HashMap<String, Object>();
        this.motors = new HashMap<String, Object>();
        this.servos = new HashMap<String, Object>();
        this.opm = opm;
        this.tel = tel;
    }

    // If someone tries to get a device not registered in a hashmap.
    public class HardwareNotRegisteredException extends Exception {
        public HardwareNotRegisteredException() { super(); }
        public HardwareNotRegisteredException(String cause) { super(cause); }
    }

    // The register functions take a String that corresponds to a device in the hardware map, and
    // add an Object corresponding to that device to the right hashmap.
    public void registerColorSensor(String colorName) {
        sensors.put("color_sensor", hmap.colorSensor.get(colorName));
    }

    public void registerGyroSensor(String gyroName) {
        sensors.put("gyro_sensor", hmap.gyroSensor.get(gyroName));
    }

    public void registerLightSensor(String lightName){
        sensors.put("light_sensor", hmap.opticalDistanceSensor.get(lightName));
    }

    public void registerUltraSonicSensor(String usName) {
        sensors.put(usName, new UltraSonic(hmap.analogInput.get(usName)));
    }

    public void registerUltraSonicSensor(String usName, String servoName) {
        sensors.put(usName, new UltraSonic(hmap.analogInput.get(usName), hmap.servo.get(servoName)));
    }

    public void registerServo(String servoName) {
        sensors.put(servoName, hmap.servo.get(servoName));
    }

    /*=============================================================================
      _____       _           _     ______                _   _
     |  __ \     | |         | |   |  ____|              | | (_)
     | |__) |___ | |__   ___ | |_  | |__ _   _ _ __   ___| |_ _  ___  _ __  ___
     |  _  // _ \| '_ \ / _ \| __| |  __| | | | '_ \ / __| __| |/ _ \| '_ \/ __|
     | | \ \ (_) | |_) | (_) | |_  | |  | |_| | | | | (__| |_| | (_) | | | \__ \
     |_|  \_\___/|_.__/ \___/ \__| |_|   \__,_|_| |_|\___|\__|_|\___/|_| |_|___/

     *===========================================================================*/

    // This just gets the color reading from the color sensor. We can really only use it in one way,
    // so it doesn't really need its own class.
    public String getDominantColor() {
        ColorSensor sen = (ColorSensor) sensors.get("color_sensor");
        int r = sen.red(), b = sen.blue(), g = sen.green();

        if ((r > 0) && (b + g == 0))
            return "red";
        if ((b > 0) && (r + g == 0))
            return "blue";
        if ((r == 1) && (b == 1) && (g == 1))
            return "white";
        if (r + g + b == 0)
            return "clear";
        return "none";
    }

    // tillSense for colors.
    public void colorSweep(String color, double threshold) {
        // Color sensor max accurate range without blinder: approx 5 inches
//        ColorSensor c = (ColorSensor) sensors.get("color_sensor");
//        c.enableLed(true);
//        drivetrain.move(-0.25F);
//        while(!(getDominantColor().equals(color))) {
//            tel.addData("Dominant", getDominantColor());
//            tel.addData("Blue", c.blue());
//            tel.addData("Red", c.red());
//            tel.addData("Green", c.green());
//            tel.addData("Alpha", c.alpha());
//            try {
//                opm.waitOneFullHardwareCycle();
//            }
//            catch(InterruptedException ex){
//
//            }
//        }
//        drivetrain.move(0.0F);

        LightSensor li = (OpticalDistanceSensor) sensors.get("light_sensor");
        String stored_color = "";
        String dominant = getDominantColor();
        double[] lights = new double[20];
        int index = 0;
        int streak = 0;
        double average = 0;

        drivetrain.move(-0.25F);

        while(!(dominant.equals("red") || dominant.equals("blue"))) {
            dominant = getDominantColor();
        }
        stored_color = dominant;

        for(int i =0; i<20; i++){
            lights[i]=li.getLightDetected();
            average += lights[i];
            try {
                opm.waitOneFullHardwareCycle();
            }
            catch(InterruptedException ex){

            }
        }
        average /= 20.0;
        double reading=0;

        while (true){
            reading = li.getLightDetected();
            if(reading - average > threshold){
                streak++;
                if(streak > 10){
                    break;
                }
            }
            else {
                streak = 0;
                average = (average *20)-lights[index] + reading;
                lights[index] = reading;
                index++;
                index = index%20;
            }
        }

        // It seems like the conversion is necessary because drivetrain was declared as the abstract parent DriveTrain.
        if (stored_color.equals(color)){
            tel.addData("Color", "CORRECT");
//            ((TwoWheelDrive)drivetrain).moveDistance(0.25f, 20);
        }

        else {
            tel.addData("Color", "WRONG");
//            ((TwoWheelDrive) drivetrain).moveDistance(-0.25f, 20);
        }


    }

    // register the drive motors on the robot with the drivetrain instance.
    public void registerDriveMotors(String left, boolean leftRev,
                                    String right, boolean rightRev) {
        drivetrain = new TwoWheelDrive(this,
                                        hmap.dcMotor.get(left), leftRev,
                                        hmap.dcMotor.get(right), rightRev);
    }

    public void registerDriveMotors(String frontLeft, boolean frontLeftRev,
                                    String frontRight, boolean frontRightRev,
                                    String backLeft, boolean backLeftRev,
                                    String backRight, boolean backRightRev) {

        drivetrain = new FourWheelDrive(this,
                                        hmap.dcMotor.get(frontLeft), frontLeftRev,
                                        hmap.dcMotor.get(frontRight), frontRightRev,
                                        hmap.dcMotor.get(backLeft), backLeftRev,
                                        hmap.dcMotor.get(backRight), backRightRev);
    }
}
