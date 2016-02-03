package simplelib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;
import java.util.HashMap;

import simplelib.helper.SensorState;

/**
 * SimpleRobot is a rewrite of the orignal Robot, with more simplicity.
 * It's essentially a data center with all stuffs.
 *
 * Written by Vivek Bhupatiraju & Luke West
 */

public class SimpleRobot
{
    public static Telemetry tel;
    public static LinearOpMode opm;
    public static HardwareMap hmap;
    public static SensorState state;
    
    public static DriveTrain drivetrain;
    public static HashMap<String, DcMotor> motors;
    public static HashMap<String, Servo> servos;
    public static String gyroName;

    private static double expectedHeading;

    public static void init(HardwareMap h, Telemetry t, LinearOpMode o) {
        hmap = h;
        tel = t;
        opm = o;
    }

    public static void registerServo(String servoName) {
        if (!servos.keySet().contains(servoName))
            servos.put(servoName, hmap.servo.get(servoName));
    }

    public static void registerMotor (String motorName) {
        if (!motors.containsKey(motorName))
            motors.put(motorName, hmap.dcMotor.get(motorName));
    }

    public static void registerGyro (String name) {
        gyroName = name;
    }

    public static void setPosition(String name, double pos) {
        servos.get(name).setPosition(pos / 180.0);
    }

    public static void setPower(String name, double power) {
        motors.get(name).setPower(power);
    }

    public static double getExpectedHeading() {
        return expectedHeading;
    }

    public static void setExpectedHeading(double eH) {
        expectedHeading = eH;
    }

    public static double getActualHeading() {
        return state.getSensorReading(gyroName);
    }
}
