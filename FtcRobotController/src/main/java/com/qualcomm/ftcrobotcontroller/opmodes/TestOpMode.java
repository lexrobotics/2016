package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.DriveTrain;
import lib.FourWheelDrive;
import lib.HelperFunctions;
import lib.Robot;
import lib.SensorState;
import lib.TwoWheelDrive;

/**
 * Created by lhscompsci on 1/19/16.
 */
public class TestOpMode extends LinearOpMode{

    @Override
    public void runOpMode() throws InterruptedException{
        DriveTrain dave_train = new FourWheelDrive(
                hardwareMap.dcMotor.get("leftFrontDrive"), false,
                hardwareMap.dcMotor.get("rightFrontDrive"), false,
                hardwareMap.dcMotor.get("leftRearDrive"), true,
                hardwareMap.dcMotor.get("rightRearDrive"), true,
                4);

        Robot.init(hardwareMap, telemetry, this, dave_train, "hero");

        Robot.registerMotor("noodler");
        Robot.registerMotor("armTilter");
        Robot.registerMotor("liftStageOne");
        Robot.registerMotor("liftStageTwo");

        Robot.registerServo("divider", 0.5);
        Robot.registerServo("rightZipline", 0.5);
        Robot.registerServo("leftZipline", 0.5);

        Robot.registerServo("buttonPusher", 0.5);
        Robot.registerServo("climberDropper", 0.85);

        Robot.registerServo("redDoor", 1);
        Robot.registerServo("blueDoor", 0);

        Robot.registerUltrasonic("ultra");
        Robot.registerServo("ultraServo", 0.0);

        Robot.registerUltraServo("ultra", "ultraServo");

        Robot.state = new SensorState(hardwareMap, 1, 0);

        Robot.state.registerSensor("color", SensorState.SensorType.COLOR, false, 12);
        Robot.state.registerSensor("light", SensorState.SensorType.LIGHT, true, 12);
        Robot.state.registerSensor("hero", SensorState.SensorType.GYRO, true, 12);
        Robot.state.registerSensor("ultra", SensorState.SensorType.ULTRASONIC, true, 50);

        Thread state_thread = new Thread(Robot.state);

        waitForStart();

        state_thread.start();

        while (Robot.state.gyroIsCalibrating("hero")) {
            Thread.sleep(10);
        }

        HelperFunctions.moveEncoderTest(this);
        while (opModeIsActive()){
            Thread.sleep(10);
        }

        state_thread.interrupt();
    }
}
