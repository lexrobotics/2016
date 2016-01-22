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
public class BluePath extends LinearOpMode {

    public void runOpMode() throws InterruptedException{
//        Garrett thinks that the dumbGyroTurn changes expectedHeading incorrectly
//        Changed dumbGyroTurn to stop when the angledistance starts increasing again.
        try {


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

            //Robot dave = BotInit.bot2(hardwareMap, telemetry, this);
            waitForStart();
//        for(double i=0; i<1; i+=0.01) {
//            dave.drivetrain.turn(i);
//            telemetry.addData("power", i);
//            try {
//                Thread.sleep(100);
//            } catch(InterruptedException e) {
//
//            }
//        }
            while (Robot.state.gyroIsCalibrating("hero")) ;

            dave.drivetrain.dumbBlueGyroTurn(0.75, true, 45, "hero");
            Thread.sleep(200);
            dave.motors.get("noodler").setPower(0.75);
            dave.drivetrain.moveDistanceWithCorrections(0.3, "hero", 33, this);
            Thread.sleep(200);
            dave.motors.get("noodler").setPower(0);
            dave.drivetrain.dumbGyroTurn(-0.75, 0, "hero");
            Thread.sleep(100);
            dave.drivetrain.moveDistanceWithCorrections(0.3, "hero", 33, this);
            Thread.sleep(100);
            dave.drivetrain.dumbGyroTurn(0.75, 260, "hero");
            Thread.sleep(100);
            dave.tillLimitSwitch("limit", 0.2, "ultraServo", 1, 0.3);
            Thread.sleep(200);
            dave.drivetrain.dumbLukeMakesMeSadBlueGyroTurn(0.75, -0.5, 175, "hero");
            Thread.sleep(200);
            double baseline = Robot.state.getAvgSensorData("light");
            double reading = baseline;
            dave.drivetrain.move(-0.5);

            while (opModeIsActive()) {
                reading = Robot.state.getSensorReading("light");
                Robot.tel.addData("reading", reading);

                if (Math.abs(reading - baseline) > 20) {
                    Robot.tel.addData("bump detected", "");
                    break;
                }
                    Thread.sleep(10);

            }

            dave.drivetrain.stopMove();

            dave.drivetrain.moveDistance(0.5, 7.5, dave.waiter);
            dave.drivetrain.stopMove();
            Thread.sleep(200);
            //dave.pushButton("buttonPusher", 1500);
            dave.servos.get("climberDropper").setPosition(0.3);
            //dave.pushButton("buttonPusher");
            Thread.sleep(2000);
            dave.servos.get("climberDropper").setPosition(0.6);

        } catch(InterruptedException ex) {
            Log.i("InterruptedException", "In LinearOpMode, ending autonomous hopefully");
            return;
        }
//        dave.colorSweep(SensorState.ColorType.BLUE, "light", "color", 0.2, 20);
//        while(opModeIsActive()){
//            Thread.sleep(10);
//        }
//        telemetry.addData("done", "yes");
    }
}
