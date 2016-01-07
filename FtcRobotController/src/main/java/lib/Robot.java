package lib;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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
    private static HardwareMap hmap;
    public static DriveTrain drivetrain;

    // Telemetry lets any class anywhere print messages through Robot
    public static Telemetry tel;

    // Store the objects corresponding to the devices of the robot (motors, sensors, servos) in hashmaps.
    private static HashMap<String, Object> motors;
    private static HashMap<String, Servo> servos;
    public static UltraServoHelper ultraservohelper;
    public static LinearOpMode waiter;

    // Prevents instantiation
    private Robot(){}

    public static void init (HardwareMap hmap, Telemetry tel, LinearOpMode opm) {
        Robot.hmap = hmap;
        Robot.tel = tel;
        Robot.servos = new HashMap<String, Servo>();
        Robot.ultraservohelper = new UltraServoHelper();
        Robot.waiter = opm;
    }

    // REGISTRATION FUNCTIONS
    public static void registerDrivetrain(DriveTrain d){
        Robot.drivetrain = d;
    }

    public static void registerServo(String servoName) {
        servos.put(servoName, hmap.servo.get(servoName));
    }

    public static void registerUltrasonicServo(String sensorName, String servoName) {
        if(servos.containsKey(servoName)) {
            ultraservohelper.registerServo(sensorName, servos.get(servoName));
        }
        else{
            registerServo(servoName);
            ultraservohelper.registerServo(sensorName, servos.get(servoName));
        }
    }

    // MOVEMENT FUNCTIONS
    public static void setPosition(String servoName, int position) {
        servos.get(servoName).setPosition(position / 180);
    }

    public void tillSense(String sensorName, int servoPosition, double power, int distance, int filterlength, String gyro_name) {
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

        drivetrain.move(power, gyro_name, waiter);

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

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (count<15 && waiter.opModeIsActive());

        drivetrain.move(0);

    }

    // tillSense for colors. If the first color we detect is the color argument (our teams color)
    // Then we will hit that button. Otherwise, we go to the next light.
    public static void colorSweep( SensorState.ColorType color,
                            double low_threshold,
                            double high_threshold,
                            double power,
                            String light_name,
                            String color_name,
                            String gyro_name) {

        SensorState.ColorType dominant = state.getColorData(color_name);   // Current dominant color detected
        double average;   // Average of light values
        double reading;   // reading of light values

        drivetrain.move(power,gyro_name, waiter);

        average = state.getAvgSensorData(light_name);

        while (waiter.opModeIsActive()) {
            reading = state.getAvgSensorData(light_name);

            if (average + low_threshold <= reading && reading <= average + high_threshold){
                break;
            }

            try{
                Thread.sleep(1);
            } catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }

        drivetrain.stopMove();

        // Add code here to drop the climbers in the right bin
        if (dominant == color){
            tel.addData("Color", "CORRECT");
        }

        // First color detected is wrong color, so hit other button, which must be the right button.
        else {
            tel.addData("Color", "WRONG");
        }
    }
}
