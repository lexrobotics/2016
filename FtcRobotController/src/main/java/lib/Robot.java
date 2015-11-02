package lib;
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
    private DriveTrain drivetrain;
    private Telemetry tel;
    // Store the objects corresponding to the devices of the robot (motors, sensors, servos) in hashmaps.
    private HashMap<String, Object> motors;
    private HashMap<String, Servo> servos;
    private UltraServoHelper ultraservohelper;

    public Robot (HardwareMap hmap, Telemetry tel, LinearOpMode opm) {

        this.hmap = hmap;
        this.tel = tel;
        this.ultraservohelper = new UltraServoHelper();

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

    public void registerUltrasonicServo(String sensorName, String servoName) {
        ultraservohelper.registerServo(sensorName,servos.get(servoName));
    }

    public void tillSenseTowards(String sensorName,int servoPosition, double power, int distance, int filterlength) {
        ultraservohelper.setPosition(sensorName,servoPosition);
        drivetrain.move(power);
        while(state.getAvgSensorData(sensorName,filterlength) > distance){
            try{
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void tillSenseAway(String sensorName,int servoPosition, double power, int distance, int filterlength){
        ultraservohelper.setPosition(sensorName,servoPosition);
        drivetrain.move(power);
        while(state.getAvgSensorData(sensorName,filterlength) < distance){
            try{
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
    public void colorSweep(String color, double threshold, String lightname, String colorname) {

//        AnalogInput li = (AnalogInput) sensors.get("light_sensor");
        SensorState.ColorType stored_color = SensorState.ColorType.NONE;               // First detected color
        SensorState.ColorType dominant = state.getColorData(colorname);   // Current dominant color detected
        double[] lights = new double[20];       // Record of light values
        int index = 0;                          // Index of most recent light value
        int streak = 0;                         // Streak of high light values
        double average = 0;                     // Average of light values

        drivetrain.move(-0.10F);

        // Get the first detected red or blue surface
        while(!(dominant == SensorState.ColorType.RED || dominant == SensorState.ColorType.BLUE)) {
            dominant = state.getColorData(colorname);
            try {
                Thread.sleep(1, 1);
            } catch (InterruptedException ex){}
        }

        stored_color = dominant;
        tel.addData("Top color detected", stored_color);

        // Build up a record of some normal light values
        try{
            Thread.sleep(20);
        } catch (InterruptedException ex){}
        lights = state.getSensorDataObject(lightname).values;

        average /= lights.length;
        double reading = 0;
        tel.addData("Average", average);

        // Look for a streak of values all above the average of the collected values array.
        // Values that are above average are not added to the array, so that we don't get stuck when we drive over a white line.

        // Change to while loop later
        for (int i = 0; i < 10000; i++){
            tel.addData("Step", i);
            tel.addData("Average", average);

            reading = state.getSensorReading(lightname);
            tel.addData("Reading", reading);

            if(reading - average > threshold){
                streak++;
                if(streak > 5){
                    break;
                }
            }
            else {
                streak = 0;
                average = ((average * lights.length)-lights[index] + reading)/lights.length;
                lights[index] = reading;
                index++;
                index = index%lights.length;
            }
            try{
                Thread.sleep(1);
            } catch (InterruptedException ex){}
        }

        drivetrain.move(0F);

        // It seems like the conversion is necessary because drivetrain was declared as the abstract parent DriveTrain.
        // First color detected is team color, so get that button.
        if (stored_color.equals(color)){
            tel.addData("Color", "CORRECT");
//            drivetrain.moveD(-0.1,2);
            ((TwoWheelDrive)drivetrain).moveDistance(0.1, 1);
        }

        // First color detected is wrong color, so hit other button, which must be the right button.
        else {
            tel.addData("Color", "WRONG");
            ((TwoWheelDrive)drivetrain).moveDistance(-0.1, 2);

        }
    }
}
