package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.Robot;
import lib.SensorState;
import lib.TwoWheelDrive;

/**
 * Created by luke on 11/3/15.
 */
public class MoveStraight extends LinearOpMode {

        @Override
        public void runOpMode() throws InterruptedException{
            Robot dave = new Robot(hardwareMap, telemetry, this);

            Robot.state = new SensorState(hardwareMap, 1, 0);
            Robot.state.registerSensor("hero", SensorState.SensorType.GYRO, true, 30);

            Thread state_thread = new Thread(Robot.state);
            state_thread.start();

            waitForStart();

            while (Robot.state.gyroIsCalibrating("hero")){
            }

            TwoWheelDrive drivetrain = new TwoWheelDrive(   hardwareMap.dcMotor.get("leftdrive"), true,
                    hardwareMap.dcMotor.get("rightdrive"), false, 4);
            drivetrain.turnWithGyro(90, "hero", hardwareMap.gyroSensor.get("hero"));
            while (opModeIsActive()){
                telemetry.addData("Gyro reading", Robot.state.getSensorReading("hero"));
            }
            state_thread.interrupt();
        }
}
