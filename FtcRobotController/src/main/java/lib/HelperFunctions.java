package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;

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
            Robot.tel.addData("beacon RedVsBlue", Robot.state.redVsBlue("beacon"));
            Robot.tel.addData("beacon limit",Robot.hmap.digitalChannel.get("beaconToucher").getState());
            Robot.tel.addData("left limit", Robot.hmap.digitalChannel.get("leftLimit").getState());
            Robot.tel.addData("right limit",Robot.hmap.digitalChannel.get("rightLimit").getState());



            Robot.tel.addData("ground RedVsBlue", Robot.state.redVsBlue("ground"));

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
}
