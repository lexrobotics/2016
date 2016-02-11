package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
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

            DcMotor noodle = hardwareMap.dcMotor.get("noodler");
            noodle.setPower(-1);

            Robot.drivetrain.moveDistanceWithCorrections(0.25, 30);
            Robot.tillLimitSwitch("rightLimit", "rightLimitServo", 0.2, 0.25, 1, 1000);

            Thread.sleep(500);

            Robot.drivetrain.dumbGyroTurn(0.6, 135);

            Thread.sleep(200);
            noodle.setPower(1);
            Thread.sleep(1500);
            noodle.setPower(0);
            Thread.sleep(200);

            Robot.pushButton("beaconToucher", SensorState.ColorType.BLUE);

//            noodle.setPower()
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