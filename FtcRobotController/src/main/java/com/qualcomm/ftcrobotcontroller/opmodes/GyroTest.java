package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;
//import com.qualcomm.robotcore.hardware.GyroSensor;

import lib.MovementThread;
import lib.Robot;
import lib.SensorState;
import lib.TwoWheelDrive;

/**
 * Created by luke on 11/9/15.
 */
public class GyroTest extends LinearOpMode {
//    SensorState state;

    @Override
    public void runOpMode() throws InterruptedException {
        Robot dave = new Robot(hardwareMap, telemetry, this); // makes Robot "dave"

        Robot.state = new SensorState(hardwareMap, 1, 0);
        Robot.state.registerSensor("hero", SensorState.SensorType.GYRO, true, 12);

        while(Robot.state.calibrating("hero") == true)
            Thread.sleep(20);

        Thread state_thread = new Thread(Robot.state); // starts sensor thread
        state_thread.start();

        waitForStart();

        while (opModeIsActive()){
            telemetry.addData("Gyro heading", Robot.state.getSensorReading("hero"));
            Thread.sleep(10);
        }

        state_thread.interrupt();

//        hardwareMap.dcMotor.get("noodler").setPower(-0.4); // turns on the harvester

//        TwoWheelDrive dave_train = new TwoWheelDrive(hardwareMap.dcMotor.get("leftdrive"), true,
//                hardwareMap.dcMotor.get("rightdrive"), false, 4);
//
//        MovementThread gyroTester = new MovementThread(dave_train, "hero", 0, this);
//        gyroTester.setPower(-0.2);
//        Thread mthread = new Thread(gyroTester);
//        mthread.start();
//
//        while (opModeIsActive()){
//            Thread.sleep(20);
//        }
//        state_thread.interrupt();
//        mthread.interrupt();
    }
}
