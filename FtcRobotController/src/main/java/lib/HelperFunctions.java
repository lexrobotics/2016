package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by lhscompsci on 1/11/16.
 */
public class HelperFunctions {
    public static void bot2SensorPrint(LinearOpMode op) throws InterruptedException{

        try {
            op.waitOneFullHardwareCycle();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        ground.setLed(true);
        while(op.opModeIsActive()){
            Robot.tel.addData("gyro", Robot.state.getSensorReading("hero"));

//            if(Robot.beaconColorSensor.isColorUpdate())
//                Robot.tel.addData("beacon r", Robot.beaconColorSensor.getRed() + "  g: " + Robot.beaconColorSensor.getGreen() + "  b: " + Robot.beaconColorSensor.getBlue() + "  alpha: " + Robot.beaconColorSensor.getClear());
            if(Robot.groundColorSensor.isColorUpdate())
                Robot.tel.addData("ground r", Robot.groundColorSensor.getRed() + "  g: " + Robot.groundColorSensor.getGreen() + "  b: " + Robot.groundColorSensor.getBlue() + "  alpha: " + Robot.groundColorSensor.getClear());
//            Robot.tel.addData("beacon RedVsBlue", Robot.state.redVsBlue("beacon"));
            Robot.tel.addData("beacon limit",Robot.hmap.digitalChannel.get("beaconToucher").getState());
            Robot.tel.addData("hall",Robot.hmap.digitalChannel.get("hall1").getState());
            Robot.tel.addData("armBackStop",Robot.hmap.digitalChannel.get("compressLimit").getState());
            Robot.tel.addData("left limit", Robot.hmap.digitalChannel.get("leftLimit").getState());
            Robot.tel.addData("rear limit",Robot.hmap.digitalChannel.get("rearLimit").getState());
            ((FourWheelDrive) Robot.drivetrain).outputEncoders();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {

            }
        }
    }
    public static void moveEncoderTest(LinearOpMode op) throws InterruptedException {
        Robot.drivetrain.moveDistanceWithCorrections(0.5, 12);
    }
    public static void turnWithGyroTest() throws InterruptedException {
        Robot.drivetrain.turnWithGyro(90);
    }
    public static void encodersPrint(LinearOpMode op) {
        while(op.opModeIsActive()) {
            ((FourWheelDrive) Robot.drivetrain).outputEncoders();
        }
    }
    public static void movementThreadCalibration(LinearOpMode op) throws InterruptedException {
        while(op.opModeIsActive()) {
            Robot.drivetrain.moveDistanceWithCorrections(-0.6,36);
            Robot.drivetrain.moveDistanceWithCorrections(0.6, 36);

        }
    }

    public static void testGyroCalibration(String name, HardwareMap hmap, Telemetry tel, LinearOpMode op) {
        GyroSensor g = hmap.gyroSensor.get(name);
        ElapsedTime timer = new ElapsedTime();
        boolean calibrated = false;

        g.calibrate();

        while (g.isCalibrating() && op.opModeIsActive()) {
            tel.addData("Calibrating time", timer.time());
            calibrated = true;
        }

        while (op.opModeIsActive()) {
            tel.addData("Ever calibrated", calibrated);
            tel.addData("Hero are you with us?", g.getHeading());
        }

    }

    public static void calibrateServo(String name, double safeStart, HardwareMap hmap, Gamepad gamepad) throws InterruptedException{
        Servo s = hmap.servo.get(name);
        s.setPosition(safeStart);
        double position = safeStart;

        while (true) {
            if (gamepad.dpad_left){
                position -= 0.01;
            }

            if (gamepad.dpad_right){
                position += 0.01;
            }

            s.setPosition(position);
            Robot.tel.addData("Position", position);

            Thread.sleep(1);
        }
    }
}
