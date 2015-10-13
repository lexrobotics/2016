package lib;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Hardware;
import java.util.HashMap;

/**
 * Created by luke on 10/7/15.
 */
public class Robot {

    // Hardware map pulls device Objects from the robot.
    // Drivetrain handles functions specific to our drive type (four-wheel, two-wheel, treads, etc).
    private HardwareMap hmap;
    private DriveTrain drivetrain;

    // Store the objects corresponding to the devices of the robot (motors, sensors, servos) in hashmaps.
    private HashMap<String, Object> motors;
    private HashMap<String, Object> sensors;
    private HashMap<String, Object> servos;

    public Robot (HardwareMap hmap) {
        this.hmap = hmap;
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
        int r = sen.red(), b = sen.blue(), g = sen.green(), a = sen.alpha();

        if ((r > 0) && (b + g == 0))
            return "red";
        if ((b > 0) && (r + g == 0))
            return "blue";
        if (r == 1 && b == 1 && g == 1)
            return "white";
        if (r + g + b == 0)
            return "clear";
        return "none";
    }

    // tillSense for colors.
    public void colorSweep(String color) {
        while(this.getDominantColor().equals(color)) {
            drivetrain.move(0.5F);
        }
    }

    // register the drive motors on the robot with the drivetrain instance.
    public void registerDriveMotors(String left, boolean leftRev,
                                    String right, boolean rightRev) {
        drivetrain = new TwoWheelDrive( hmap.dcMotor.get(left), leftRev,
                                        hmap.dcMotor.get(right), rightRev);
    }

    public void registerDriveMotors(String frontLeft, boolean frontLeftRev,
                                    String frontRight, boolean frontRightRev,
                                    String backLeft, boolean backLeftRev,
                                    String backRight, boolean backRightRev) {

        drivetrain = new FourWheelDrive(hmap.dcMotor.get(frontLeft), frontLeftRev,
                                        hmap.dcMotor.get(frontRight), frontRightRev,
                                        hmap.dcMotor.get(backLeft), backLeftRev,
                                        hmap.dcMotor.get(backRight), backRightRev);
    }
}
