package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.util.Range;

import lib.Robot;
import lib.TwoWheelDrive;
import lib.SensorState;


public class ColorSweep extends LinearOpMode {
    // Demo class for the new Robot classes.

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        Robot dave = new Robot(hardwareMap, telemetry, this);
        Robot.state = new SensorState(hardwareMap, 0, 500000);
        Robot.state.registerSensor("mr", SensorState.SensorType.COLOR, true, 1);
        Robot.state.registerSensor("mrs", SensorState.SensorType.LIGHT, true, 1);
        new Thread(Robot.state).start();

        TwoWheelDrive dave_train = new TwoWheelDrive(   hardwareMap.dcMotor.get("left_motors"), true,
                                                        hardwareMap.dcMotor.get("right_motors"), false, 4);

        dave.registerDriveTrain(dave_train);
        dave.colorSweep("blue", 10, "mr", "mrs");
    }
}
