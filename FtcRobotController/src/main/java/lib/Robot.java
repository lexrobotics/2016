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

    private HardwareMap hmap;
    private DriveTrain drivetrain;

    private HashMap<String, Object> motors;
    private HashMap<String, Object> sensors;

    public Robot (HardwareMap hmap) {
        this.hmap = hmap;
    }

    public class HardwareNotRegisteredException extends Exception
    {
        public HardwareNotRegisteredException() { super(); }
        public HardwareNotRegisteredException(String cause) { super(cause); }
    }

    public void registerColorSensor(String colorName)
    {
        sensors.put("color_sensor", hmap.colorSensor.get(colorName));
    }
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
    public void colorSweep(String color){
        while(this.getDominantColor().equals(color)){
            drivetrain.move(0.5F);
        }
    }

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
