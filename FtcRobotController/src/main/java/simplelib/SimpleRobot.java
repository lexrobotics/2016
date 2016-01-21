package simplelib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;
import java.util.HashMap;

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
    
    public static DriveTrain drivetrain;
    public static HashMap<String, DcMotor> motors;
    public static HashMap<String, Servo> servos;

    public static void init(HardwareMap h, Telemetry t, LinearOpMode o) {
        hmap = h;
        tel = t;
        opm = o;
    }

    public void registerServo(String servoName) {
        if (!servos.keySet().contains(servoName))
            servos.put(servoName, hmap.servo.get(servoName));
    }

    public void registerMotor (String motorName) {
        if (!motors.containsKey(motorName))
            motors.put(motorName, hmap.dcMotor.get(motorName));
    }

    public void setPosition(String name, double pos) {
        servos.get(name).setPosition(pos / 180.0);
    }

    public void setPower(String name, double power) {
        motors.get(name).setPower(power);
    }
}
