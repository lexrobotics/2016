package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;
//import com.qualcomm.robotcore.hardware.GyroSensor;

import lib.MovementThread;
import lib.Robot;
import lib.SensorState;
import lib.SensorTest;
import lib.TwoWheelDrive;

/**
 * Created by luke on 11/9/15.
 */
public class GyroTest extends LinearOpMode {
//    SensorState state;

    @Override
    public void runOpMode() throws InterruptedException {
        String x = "NOT CALIBRATED";
        Robot dave = new Robot(hardwareMap, telemetry, this); // makes Robot "dave"


        Robot.state = new SensorState(hardwareMap, 1, 0);
        Robot.state.registerSensor("hero", SensorState.SensorType.GYRO, true, 12);
        Thread state_thread = new Thread(Robot.state);
        state_thread.start();

        waitForStart();
        while (Robot.state.calibrating("hero")){
            x = "calibrated";
            Robot.tel.addData("Reading", "");
        }

        TwoWheelDrive dave_train = new TwoWheelDrive(hardwareMap.dcMotor.get("leftdrive"), true,
                hardwareMap.dcMotor.get("rightdrive"), false, 4);
        dave_train.turnWithGyro(-0.3,90,"hero");

        state_thread.interrupt();

    }
}
