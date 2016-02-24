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
    public void path() throws InterruptedException {
        BotInit.bot2(hardwareMap, telemetry, this);
        int delayTime = (int)Robot.delaySet("delayDial","beaconToucher");
        waitForStart();
        Robot.delayWithCountdown(delayTime);
        while (Robot.state.gyroIsCalibrating("hero")) {
            waitOneFullHardwareCycle();
        }

        Robot.drivetrain.dumbGyroTurn(1, 0, 45);

        DcMotor noodle = hardwareMap.dcMotor.get("noodler");
        noodle.setPower(-1);

        Robot.drivetrain.moveDistanceWithCorrections(0.6, 55);
        Robot.tillLimitSwitch("rightLimit", "rightLimitServo", 0.2, 0.25, 1, 1000);
        noodle.setPower(0);
        Thread.sleep(500);

        Robot.drivetrain.dumbGyroTurn(0.75, 135);

        Thread.sleep(200);
        noodle.setPower(1);
        SensorState.ColorType dominant = Robot.tillWhite(-0.2, "ground", "beacon");
        int direction = 0;
        if(dominant == SensorState.ColorType.BLUE) {
            direction =  1;
        }
        else if(dominant == SensorState.ColorType.RED) {
            direction = -1;
        }

        noodle.setPower(0);
        Robot.pushButton("beaconToucher", direction);
    }

    public void runOpMode() throws InterruptedException{
        try {
            path();
        } catch(InterruptedException ex) {
            Log.i("InterruptedException", "In LinearOpMode, ending autonomous hopefully");
        }
        Robot.state_thread.interrupt();
    }
}


