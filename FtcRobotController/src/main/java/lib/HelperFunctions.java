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

//        Robot.mux = new Wire(Robot.hmap, "mux", 2*0x70);
//        Robot.groundColorSensor = new AdafruitColorSensor(Robot.hmap, "ground", "cdim", -1, 0, Robot.mux);
//        Robot.beaconColorSensor = new AdafruitColorSensor(Robot.hmap, "beacon", "cdim", -1, 1, Robot.mux);

//        ground.setLed(true);
        while(op.opModeIsActive()){
            Robot.tel.addData("gyro", Robot.state.getSensorReading("hero"));

//            if(Robot.beaconColorSensor.isColorUpdate())
//                Robot.tel.addData("beacon r", Robot.beaconColorSensor.getRed() + "  g: " + Robot.beaconColorSensor.getGreen() + "  b: " + Robot.beaconColorSensor.getBlue() + "  alpha: " + Robot.beaconColorSensor.getClear());
//            if(Robot.groundColorSensor.isColorUpdate())
//                Robot.tel.addData("ground r", Robot.groundColorSensor.getRed() + "  g: " + Robot.groundColorSensor.getGreen() + "  b: " + Robot.groundColorSensor.getBlue() + "  alpha: " + Robot.groundColorSensor.getClear());
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

    public static void calibrateServo(String name, double safeStart, HardwareMap hmap, Gamepad gamepad, LinearOpMode waiter, Telemetry tel) throws InterruptedException{
        Servo s = hmap.servo.get(name);
        s.setPosition(safeStart);
        double position = safeStart;

        while (waiter.opModeIsActive()) {
            if (gamepad.dpad_left&&position>.01){
                position -= 0.01;
            }

            if (gamepad.dpad_right && position<0.99){
                position += 0.01;
            }

            s.setPosition(position);
            tel.addData("Position", position);

            Thread.sleep(200);
        }
    }

    public static void calibrateServos(String name1, String name2, double safeStart1, double safeStart2, HardwareMap hmap, Gamepad gamepad, LinearOpMode waiter, Telemetry tel) throws InterruptedException{
        Servo s1 = hmap.servo.get(name1);
        Servo s2 = hmap.servo.get(name2);
        s1.setPosition(safeStart1);
        s2.setPosition(safeStart2);
        double position1 = safeStart1;
        double position2 = safeStart2;

        while (waiter.opModeIsActive()) {
            if (gamepad.dpad_left && position1>.01){
                position1 -= 0.01;
            }

            if (gamepad.dpad_right && position1<0.99){
                position1 += 0.01;
            }

            if (gamepad.dpad_down && position2>.01){
                position2 -= 0.01;
            }

            if (gamepad.dpad_up && position2<0.99){
                position2 += 0.01;
            }


            s1.setPosition(position1);
            tel.addData("Position 1", position1);
            s2.setPosition(position2);
            tel.addData("Position 2", position2);

            Thread.sleep(200);
        }
    }
}
