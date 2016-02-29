package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;

import lib.BotInit;
import lib.DriveTrain;
import lib.FourWheelDrive;
import lib.Robot;
import lib.HelperFunctions;
import lib.SensorState;

/**
 * Created by lhscompsci on 1/11/16.
 */
public class RedPathWide extends LinearOpMode {
    public void path() throws InterruptedException {
        BotInit.bot2(hardwareMap, telemetry, this);
        int delayTime = (int)Robot.delaySet("delayDial","beaconToucher");
        waitForStart();
        Robot.delayWithCountdown(delayTime);
        Robot.state.registerSensor("hero", SensorState.SensorType.GYRO, true, 12);
        Thread.sleep(3500);
        while (Robot.state.gyroIsCalibrating("hero") == true) {
            waitOneFullHardwareCycle();
        }


        Robot.drivetrain.dumbGyroTurn(0, 1, 47);
        DcMotor noodle = hardwareMap.dcMotor.get("noodler");
        noodle.setPower(-1);
        Thread.sleep(200);
        Robot.drivetrain.moveDistanceWithCorrections(0.6, 100);
        Robot.tillLimitSwitch("leftLimit", "leftLimitServo", 0.2, 0.8, 0, 1000);
        Robot.drivetrain.dumbGyroTurn(0.7, 47);

        noodle.setPower(1);
        SensorState.ColorType dominant = Robot.tillWhite(-0.2, "ground", "beacon");
        noodle.setPower(0);

        if(dominant == SensorState.ColorType.BLUE) {
            Robot.dumpClimbers(2);
            Robot.pushButton("beaconToucher", -1);
        }
        else if(dominant == SensorState.ColorType.RED) {
            Robot.pushButton("beaconToucher", 1);
            Robot.dumpClimbers();
        }

        Robot.retractButtonPusher();
    }

    @Override
    public void runOpMode()  {
        try {
            path();
        } catch(InterruptedException ex) {
            Log.i("InterruptedException", "In LinearOpMode, ending autonomous hopefully");
            Robot.drivetrain.setLeftMotors(0);
            Robot.drivetrain.setRightMotors(0);
            Robot.motors.get("noodler").setPower(0);
            Robot.state_thread.interrupt();
            return;
        }
    }
}
