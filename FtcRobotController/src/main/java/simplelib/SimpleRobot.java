package simplelib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;
import java.util.HashMap;

/**
 * Created by Vivek Bhupatiraju on 1/20/16.
 */

public class SimpleRobot
{
    // Objects for Functions
    public static HardwareMap hmap;
    public static Telemetry tel;
    public static LinearOpMode opm;
    public static DriveTrain drivetrain;

    // HashMap Stores Moving Parts
    public static HashMap<String, DcMotor> motors;
    public static HashMap<String, Servo> servos;

    // Cannot Make Instance of Mah Boi
    private SimpleRobot() {}

    public static void init (HardwareMap h, Telemetry t, LinearOpMode o) {
        hmap = h;
        tel = t;
        opm = o;
    }

    public static void registerServo(String servoName, double initial_position) {
        if (!servos.keySet().contains(servoName)){
            hmap.servo.get(servoName).setPosition(initial_position);
            servos.put(servoName, hmap.servo.get(servoName));
        }
    }

    public static void registerMotor (String motorName, double initial_power) {
        if (!motors.containsKey(motorName)){
            hmap.dcMotor.get(motorName).setPower(initial_power);
            motors.put(motorName, hmap.dcMotor.get(motorName));
        }
    }


}
