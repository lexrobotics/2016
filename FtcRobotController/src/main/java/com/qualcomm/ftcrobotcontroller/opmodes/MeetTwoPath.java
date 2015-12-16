package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.util.Range;

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

        TwoWheelDrive dave_train = new TwoWheelDrive(hardwareMap.dcMotor.get("leftdrive"), true,
                hardwareMap.dcMotor.get("rightdrive"), false, 4);

        dave.registerDriveTrain(dave_train);

        waitForStart();

        //movement//

        dave_train.moveDistance(0.5, 60);
    }
}
