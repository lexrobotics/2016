package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.DriveTrain;
import lib.Robot;
import lib.SensorState;
import lib.TwoWheelDrive;

/**
 * Created by lhscompsci on 1/7/16.
 */
public class NewDrivetrainTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException{
        DriveTrain dave_train = new TwoWheelDrive(
                hardwareMap.dcMotor.get("leftdrive"), true,
                hardwareMap.dcMotor.get("rightdrive"), false, 10);

        Robot.init(hardwareMap, telemetry, this, dave_train, "hero");

        Robot.state = new SensorState(hardwareMap, 1, 0);
        Robot.state.registerSensor("mr", SensorState.SensorType.COLOR, false, 12);
        Robot.state.registerSensor("mrs", SensorState.SensorType.LIGHT, true, 12);
        Robot.state.registerSensor("hero", SensorState.SensorType.GYRO, true, 12);
        Robot.state.registerSensor("rearUltra", SensorState.SensorType.ULTRASONIC, true, 50);
        Robot.state.registerSensor("frontUltra", SensorState.SensorType.ULTRASONIC, true, 50);
        Robot.state.setUltrasonicPin("ultraToggle");

        Thread state_thread = new Thread(Robot.state);
        state_thread.start();

        waitForStart();

        while (Robot.state.gyroIsCalibrating("hero"));

        dave_train.turnWithGyro(45);

        while (opModeIsActive()){
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex){
                break;
            }
        }
    }
}
