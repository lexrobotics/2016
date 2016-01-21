package simplelib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;
import java.util.HashMap;

/**
 * SimpleRobot is a rewrite of the orignal Robot, with more simplicity.
 * It is not static; chose to make it an object.
 * 
 */

public class SimpleRobot
{
    // Objects for Functions
    public static Telemetry tel;
    public static LinearOpMode opm;
    public HardwareMap hmap;
    public DriveTrain drivetrain;

    // HashMap Stores Moving Parts
    public HashMap<String, DcMotor> motors;
    public HashMap<String, Servo> servos;

    // Cannot Make Instance of Mah Boi
    public SimpleRobot(HardwareMap h, Telemetry t, LinearOpMode o) {
        this.hmap = h;
        this.tel = t;
        this.opm = o;
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


}
