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
        Robot.state = new SensorState(hardwareMap, 1, 0);
        Robot.state.registerSensor("mr", SensorState.SensorType.COLOR, true, 1);
        Robot.state.registerSensor("mrs", SensorState.SensorType.LIGHT, true, 1);
        Thread state_thread = new Thread(Robot.state);
        state_thread.start();


        TwoWheelDrive dave_train = new TwoWheelDrive(   hardwareMap.dcMotor.get("leftdrive"), true,
                                                        hardwareMap.dcMotor.get("rightdrive"), false, 4);

        dave.registerDriveTrain(dave_train);
//        dave.colorSweep(SensorState.ColorType.BLUE, 2, "mrs", "mr");

        while (opModeIsActive() && !(Thread.currentThread().isInterrupted())){
            telemetry.addData("OpModeIsActive", "");
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex){
                Thread.currentThread().interrupt();
                break;
            }
        }
        state_thread.interrupt();
    }
}

/**
 * NOTE:
 * if a loop is running when the program is requested to stop, and the loop doesn't stop, the opmode
 * takes too long to end and the robot controller crashes.
 *
 * To counter this, we can either make a condition in every loop that ends it if the opmode ends
 * OR
 * we put everything in another thread and end it when the opmode ends, killing all running operations.
 * OR
 * maybe, if we can put everything in a master loop that breaks when the opmode ends, it might work.
 * as in while(opModeIsActive()) { do stuff } in the opmode
 * Not sure that would work, stuff inside would have to not block.
 **/