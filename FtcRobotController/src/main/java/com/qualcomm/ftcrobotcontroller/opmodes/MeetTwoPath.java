package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.util.Range;

import lib.FourWheelDrive;
import lib.Robot;
import lib.SensorState;
import lib.TwoWheelDrive;

/**
 * Created by lhscompsci on 12/9/15.
 */
public class MeetTwoPath extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Robot dave = new Robot(hardwareMap, telemetry, this); // makes Robot "dave"

        Robot.state = new SensorState(hardwareMap, 1, 0);
        Robot.state.registerSensor("hero", SensorState.SensorType.GYRO, true, 12);
        Thread state_thread = new Thread(Robot.state);
        state_thread.start();

        waitForStart();

        while (Robot.state.gyroIsCalibrating("hero"));
        FourWheelDrive dave_train = new FourWheelDrive(hardwareMap.dcMotor.get("leftFront"), true,
                hardwareMap.dcMotor.get("rightFront"), false,
                hardwareMap.dcMotor.get("leftRear"), true,
                hardwareMap.dcMotor.get("rightRear"), false,
                4);

        dave.registerDriveTrain(dave_train);

        waitForStart();

        //movement//

        dave_train.moveDistance(0.5, 65);
//        dave_train.turnWithGyro(90,"hero");
    }
}