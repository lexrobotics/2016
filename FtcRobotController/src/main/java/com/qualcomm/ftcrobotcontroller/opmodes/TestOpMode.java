package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.DriveTrain;
import lib.FourWheelDrive;
import lib.Robot;
import lib.SensorState;
import lib.TwoWheelDrive;

/**
 * Created by lhscompsci on 1/19/16.
 */
public class TestOpMode extends LinearOpMode{

    @Override
    public void runOpMode() throws InterruptedException{
        Robot dave = new Robot(hardwareMap, telemetry, this);

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

        dave.registerServo("redDoor", 1);
        dave.registerServo("blueDoor", 0);
        dave.registerUltrasonicServo("ultra", "ultraServo", 0.2);

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

        dave.tillSense("ultra", 0.5, 0.7, 20, 1);

        while (opModeIsActive()){
            Thread.sleep(10);
        }

        state_thread.interrupt();
    }
}