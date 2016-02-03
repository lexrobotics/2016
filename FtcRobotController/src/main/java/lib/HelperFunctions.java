package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;

/**
 * Created by lhscompsci on 1/11/16.
 */
public class HelperFunctions {
    public static void bot2SensorPrint(LinearOpMode op){
        while(op.opModeIsActive()){
//            Robot.tel.addData("gyro", Robot.state.getSensorReading("hero"));
//            Robot.tel.addData("ultra", Robot.state.getSensorReading("ultra"));
//            Robot.tel.addData("color", Robot.state.getSensorReading("color"));
//            Robot.tel.addData("light", Robot.state.getSensorReading("light"));
            Robot.tel.addData("gyro", Robot.state.getAvgSensorData("hero"));
            Robot.tel.addData("beacon", Robot.state.getColorData("beacon"));
            Robot.tel.addData("ground", Robot.state.getColorData("ground"));

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
