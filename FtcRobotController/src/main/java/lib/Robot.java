package lib;
import com.qualcomm.ftcrobotcontroller.opmodes.ColorSweep;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.robocol.Telemetry;
import java.util.HashMap;

/**
 * Created by luke on 10/7/15.
 */


public class Robot {
//    public static SensorState state;

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


    // REGISTRATION FUNCTIONS
    // It makes more sense to have the opmode construct a drivetrain and pass it to Robot than to repeat
    // constructors in Robot and the drivetrain classes.
    public void registerDriveTrain(DriveTrain d){
        this.drivetrain = d;
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
        sensors.put("light_sensor", hmap.analogInput.get(lightName));
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

    // tillSense for colors. If the first color we detect is the color argument (our teams color)
    // Then we will hit that button.
    // Otherwise, we go to the next light.
    public void colorSweep(String color, double threshold) {

        AnalogInput li = (AnalogInput) sensors.get("light_sensor");
        String stored_color = "";               // First detected color
        String dominant = getDominantColor();   // Current dominant color detected
        double[] lights = new double[20];       // Record of light values
        int index = 0;                          // Index of most recent light value
        int streak = 0;                         // Streak of high light values
        double average = 0;                     // Average of light values

        drivetrain.move(-0.10F);

        // Get the first detected red or blue surface
        while(!(dominant.equals("red") || dominant.equals("blue"))) {
            dominant = getDominantColor();
            try {
                Thread.sleep(1, 1);
            } catch (InterruptedException ex){}
        }

        stored_color = dominant;
        tel.addData("Top color detected",stored_color);

        // Build up a record of some normal light values
        for(int i =0; i<20; i++){
            lights[i]=li.getValue();
            average += lights[i];
            try {
                opm.waitOneFullHardwareCycle();
            }
            catch(InterruptedException ex){

            }
        }

        average /= 20.0;
        double reading = 0;
        tel.addData("Average", average);

        // Look for a streak of values all above the average of the collected values array.
        // Values that are above average are not added to the array, so that we don't get stuck when we drive over a white line.

        // Change to while loop later
        for (int i = 0; i < 10000; i++){
            tel.addData("Step", i);
            tel.addData("Average", average);

            reading = li.getValue();
            tel.addData("Reading", reading);

            if(reading - average > threshold){
                streak++;
                if(streak > 5){
                    break;
                }
            }
            else {
                streak = 0;
                average = ((average *20.0)-lights[index] + reading)/average;
                lights[index] = reading;
                index++;
                index = index%20;
            }
            try{
                Thread.sleep(1);
            } catch (InterruptedException ex){}
        }

        drivetrain.move(0F);

        // It seems like the conversion is necessary because drivetrain was declared as the abstract parent DriveTrain.
        // First color detected is team color, so get that button.
//        if (stored_color.equals(color)){
//            tel.addData("Color", "CORRECT");
//            drivetrain.move(0F);
////            ((TwoWheelDrive)drivetrain).moveDistance(0.25f, 20);
//        }
//
//        // First color detected is wrong color, so hit other button, which must be the right button.
//        else {
//            tel.addData("Color", "WRONG");
//            drivetrain.move(0F);
////            ((TwoWheelDrive) drivetrain).moveDistance(-0.25f, 20);
//        }
    }
}
