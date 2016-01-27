package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

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

    @Override
    public void runOpMode()  {
//        Garrett thinks that the dumbGyroTurn changes expectedHeading incorrectly
//        Changed dumbGyroTurn to stop when the angledistance starts increasing again.

//        Robot dave = BotInit.bot2(hardwareMap, telemetry, this);
        DriveTrain dave_train = new FourWheelDrive(
                hardwareMap.dcMotor.get("leftFrontDrive"), false,
                hardwareMap.dcMotor.get("rightFrontDrive"), false,
                hardwareMap.dcMotor.get("leftRearDrive"), true,
                hardwareMap.dcMotor.get("rightRearDrive"), true,
                4);
        Robot.init(hardwareMap, telemetry, this, dave_train,"hero");


        try {


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
            hardwareMap.servo.get("ultraServo").setPosition(0.3);
//            dave.registerUltrasonicServo("ultra", "ultraServo", 0.2);
            Robot.state = new SensorState(hardwareMap, 1, 0);

            Robot.state.registerSensor("color", SensorState.SensorType.COLOR, false, 12);
            Robot.state.registerSensor("light", SensorState.SensorType.LIGHT, true, 12);
            Robot.state.registerSensor("hero", SensorState.SensorType.GYRO, true, 12);
            Robot.state.registerSensor("ultra", SensorState.SensorType.ULTRASONIC, true, 50);

            Thread state_thread = new Thread(Robot.state);

            waitForStart();

            state_thread.start();

            // Gyro Calibration
            while (Robot.state.gyroIsCalibrating("hero")) {
                Thread.sleep(10);

            }

            // Path
            Robot.drivetrain.dumbGyroTurn(0, 1, 45);
            Robot.motors.get("noodler").setPower(.75);
            Thread.sleep(200);


            dave_train.moveDistanceWithCorrections(0.25, 30);
            Robot.tillLimitSwitch("limit","ultraServo",  0.2, 0.8, 0.3,100000);
//            dave_train.dumbLukeMakesMeSadBlueGyroTurn(0.5, 0.2, 0, "hero");


//            dave.tillSense("ultra", 0.5, 0.5, 12, 10, false);

//            Thread.sleep(200);
//            dave.drivetrain.moveDistance(0.6, 3, this);
//
//            Thread.sleep(200);
//            Robot.drivetrain.dumbGyroTurn(-0.5, 0, 0);
//////////
////            Thread.sleep(200);
////            dave.motors.get("noodler").setPower(0);
////
////            Robot.colorSweep(SensorState.ColorType.RED, "light", "color", 0.4, 20);
            Robot.motors.get("noodler").setPower(0);

//            ////        dave.drivetrain.dumbGyroTurn(0.5, , 90, "hero");
//            //        dave.drivetrain.moveDistance(-0.6, 10);
            while (opModeIsActive()) {
                telemetry.addData("done", "yes");
                Thread.sleep(10);
            }
        } catch(InterruptedException ex) {
            Log.i("InterruptedException", "In LinearOpMode, ending autonomous hopefully");
            Robot.drivetrain.setLeftMotors(0);
            Robot.drivetrain.setRightMotors(0);
            Robot.motors.get("noodler").setPower(0);
            return;
        }
    }
}
