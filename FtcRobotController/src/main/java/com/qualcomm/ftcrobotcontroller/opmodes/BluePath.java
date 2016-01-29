package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Hardware;

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
            BotInit.bot2(hardwareMap, telemetry, this);
            waitForStart();
            while (Robot.state.gyroIsCalibrating("hero")) {
                waitOneFullHardwareCycle();
            }
            Robot.drivetrain.dumbGyroTurn(1, 0, 45);
            Robot.drivetrain.moveDistanceWithCorrections(0.25, 30);
            Robot.tillLimitSwitch("rightLimit", "rightLimitServo", 0.2, 0.25, 1, 1000);
//            Robot.drivetrain.move(0.3, this);
            Robot.drivetrain.dumbGyroTurn(1, -1, 135);


            Robot.tillWhite("ground", -0.3); //dont we want to be looking for white? this needs to be explained?

//            Robot.tillColor("ground", 0.8, SensorState.ColorType.WHITE);
//
////            //dave.pushButton("buttonPusher", 1500);
////            dave.servos.get("climberDropper").setPosition(0.3);
////            //dave.pushButton("buttonPusher");
////            Thread.sleep(2000);
////            dave.servos.get("climberDropper").setPosition(0.6);
//            while (opModeIsActive()) {
//                telemetry.addData("done", "yes");
//                Thread.sleep(10);
//            }
        } catch(InterruptedException ex) {
            Log.i("InterruptedException", "In LinearOpMode, ending autonomous hopefully");
        }
//        dave.colorSweep(SensorState.ColorType.BLUE, "light", "color", 0.2, 20);
//        while(opModeIsActive()){
//            Thread.sleep(10);
//        }
//        telemetry.addData("done", "yes");

        Robot.state_thread.interrupt();
    }
}


/*
Functions to test:

dumbGyroTurn(both kinds)
registerServo
registerMotor
setServoPosition
tillLimitSwitch
tillColor
colorSweep
filterFilled functions
 */