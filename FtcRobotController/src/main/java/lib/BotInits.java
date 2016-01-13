package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;

import lib.DriveTrain;
import lib.FourWheelDrive;
import lib.Robot;
import lib.SensorState;

/**
 * Created by lhscompsci on 1/11/16.
 */
public class BotInits {
    public static Robot bot2 (HardwareMap hardwareMap, Telemetry telemetry, LinearOpMode op) {
        Robot dave = new Robot(hardwareMap, telemetry, op);

        DriveTrain dave_train = new FourWheelDrive(
                hardwareMap.dcMotor.get("leftFrontDrive"), false,
                hardwareMap.dcMotor.get("rightFrontDrive"), false,
                hardwareMap.dcMotor.get("leftRearDrive"), true,
                hardwareMap.dcMotor.get("rightRearDrive"), true,
                4);
        dave.registerDriveTrain(dave_train);

        dave.registerMotor("noodler");
        dave.registerMotor("armTilter");
        dave.registerMotor("liftStageOne");
        dave.registerMotor("liftStageTwo");

        dave.registerServo("divider", 0.5);
        dave.registerServo("rightZipline", 0.5);
        dave.registerServo("leftZipline", 0.5);

        dave.registerServo("buttonPusher", 0.5);
        dave.registerServo("climberDropper", 0.85);

        dave.registerServo("redDoor", 0);
        dave.registerServo("blueDoor", 1);
        dave.registerUltrasonicServo("ultra", "ultraServo", 0.2);
        Robot.state = new SensorState(hardwareMap, 1, 0);

        Robot.state.registerSensor("color", SensorState.SensorType.COLOR, false, 12);
        Robot.state.registerSensor("light", SensorState.SensorType.LIGHT, true, 12);
        Robot.state.registerSensor("hero", SensorState.SensorType.GYRO, true, 12);
        Robot.state.registerSensor("ultra", SensorState.SensorType.ULTRASONIC, true, 50);

        for (int i = 0; i < 30; i++){
            try {
                Thread.sleep(50);
                Robot.tel.addData("opmode", Robot.waiter.opModeIsActive());
            } catch (InterruptedException ex){
                break;
            }
        }

        Thread state_thread = new Thread(Robot.state);
        state_thread.start();

        return dave;

        // SENSORSTATE EXPECTS AN ULTRATOGGLE, FIX THAT

    }
}
