package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.BotInit;
import lib.DriveTrain;
import lib.FourWheelDrive;
import lib.Robot;
import lib.HelperFunctions;
import lib.SensorState;

/**
 * Created by lhscompsci on 1/11/16.
 */
public class RedPath extends LinearOpMode {

    public void runOpMode() throws InterruptedException {
//        Garrett thinks that the dumbGyroTurn changes expectedHeading incorrectly
//        Changed dumbGyroTurn to stop when the angledistance starts increasing again.

//        Robot dave = BotInit.bot2(hardwareMap, telemetry, this);




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

        // Gyro Calibration
        while (Robot.state.gyroIsCalibrating("hero")) ;

        // Path
//        dave.drivetrain.moveDistanceWithCorrections(0.6, "hero", 10, this);
//        Thread.sleep(200);
        dave.drivetrain.dumbGyroTurn(0.75, false, 45, "hero");
        Thread.sleep(200);
        dave.tillSense("ultra", 0.5, 0.5, 12, 10);
        Thread.sleep(200);
        dave.drivetrain.moveDistanceWithCorrections(0.6, "hero", 5, this);

        Thread.sleep(200);
        dave.drivetrain.dumbGyroTurn(0.5, -0, "hero");
        Thread.sleep(200);
//
        dave.colorSweep(SensorState.ColorType.RED, "light", "color", 0.4, 20);
////        dave.drivetrain.dumbGyroTurn(0.5, , 90, "hero");
        while (opModeIsActive()) {
            telemetry.addData("done", "yes");

            Thread.sleep(10);
        }
    }
}
