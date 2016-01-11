package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;

/**
 * Created by lhscompsci on 1/11/16.
 */
public class HelperFunctions {
    public static void bot2SensorPrint(Robot dave, LinearOpMode op){
        while(op.opModeIsActive()){
            Robot.tel.addData("gyro", Robot.state.getSensorReading("hero"));
            Robot.tel.addData("ultra", Robot.state.getSensorReading("ultra"));
            Robot.tel.addData("color", Robot.state.getSensorReading("color"));
            Robot.tel.addData("light", Robot.state.getSensorReading("light"));
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {

            }
        }
    }
    public static void moveEncoderTest(Robot dave, LinearOpMode op){
        dave.drivetrain.moveDistanceWithCorrections(0.5,"hero",12,op);
    }
    public static void encodersPrint(Robot dave, LinearOpMode op) {
        while(op.opModeIsActive()) {
            ((FourWheelDrive) dave.drivetrain).outputEncoders();
        }
    }
}
