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
        Robot.init(hardwareMap, telemetry, this); // makes Robot "dave"

        Robot.state = new SensorState(hardwareMap, 1, 0);

        Thread state_thread = new Thread(Robot.state);
        state_thread.start();

        waitForStart();

        while (opModeIsActive()){
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex){
                break;
            }
        }
    }
}
