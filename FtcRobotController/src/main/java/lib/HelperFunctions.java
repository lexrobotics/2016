package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by lhscompsci on 1/11/16.
 */
public class HelperFunctions {
    public static void bot2SensorPrint(LinearOpMode op){
        ColorSensor beacon = Robot.hmap.colorSensor.get("beacon");
        ColorSensor ground = Robot.hmap.colorSensor.get("ground");

        while(op.opModeIsActive()){
            Robot.tel.addData("gyro", Robot.state.getAvgSensorData("hero"));
            Robot.tel.addData("beacon r", beacon.red() + "  g: " + beacon.green() + "  b: " + beacon.blue() + "  alpha: " + beacon.alpha());
            Robot.tel.addData("ground r", ground.red() + "  g: " + ground.green() + "  b: " + ground.blue() + "  alpha: " + ground.alpha());
//            Robot.tel.addData("beacon RedVsBlue", Robot.state.redVsBlue("beacon"));
            Robot.tel.addData("beacon limit",Robot.hmap.digitalChannel.get("beaconToucher").getState());
            Robot.tel.addData("hall",Robot.hmap.digitalChannel.get("hall1").getState());

            Robot.tel.addData("left limit", Robot.hmap.digitalChannel.get("leftLimit").getState());
            Robot.tel.addData("right limit",Robot.hmap.digitalChannel.get("rightLimit").getState());
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
            Robot.drivetrain.moveDistanceWithCorrections(0.6,36);
            Robot.drivetrain.moveDistanceWithCorrections(-0.6,36);

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
}
