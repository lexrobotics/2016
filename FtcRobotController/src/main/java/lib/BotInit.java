package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
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
public class BotInit {
    public static void bot2 (HardwareMap hardwareMap, Telemetry telemetry, LinearOpMode op) throws InterruptedException{
        DriveTrain dave_train = new FourWheelDrive(
                hardwareMap.dcMotor.get("leftFrontDrive"), true,
                hardwareMap.dcMotor.get("rightFrontDrive"), false,
                hardwareMap.dcMotor.get("leftRearDrive"), true,
                hardwareMap.dcMotor.get("rightRearDrive"), false,
                4);

        Robot.init(hardwareMap, telemetry, op, dave_train, "hero");

        Robot.registerMotor("noodler");
        Robot.registerMotor("armTilter");
        Robot.registerMotor("liftStageOne");
        Robot.registerMotor("liftStageTwo");

        Robot.registerServo("divider", 0.5);
        Robot.registerServo("rightZipline", 0.88);
        Robot.registerServo("leftZipline", 0.11);

        Robot.registerServo("buttonPusher", 0.5);
        Robot.registerServo("climberDropper", 1);

        Robot.registerServo("redDoor", 1);
        Robot.registerServo("blueDoor", 0);

        Robot.registerServo("leftLimitServo", 0);
        Robot.registerServo("rightLimitServo", 1);

        Robot.registerServo("armLock", 1);

        Robot.state = new SensorState(hardwareMap, 1, 0);

        Robot.state.registerSensor("hero", SensorState.SensorType.IMU, true, 12);

//        Robot.state.registerSensor("beacon", SensorState.SensorType.COLOR, false, 12);
//        hardwareMap.colorSensor.get("beacon").setI2cAddress(0x3C);

        Robot.state_thread = new Thread(Robot.state);
        Robot.state_thread.start();


    }
}
