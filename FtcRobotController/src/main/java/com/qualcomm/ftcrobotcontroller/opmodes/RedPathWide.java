package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
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
        Servo redDoor;
        redDoor = hardwareMap.servo.get("redDoor");
        int delayTime = (int)Robot.delaySet("delayDial","beaconToucher");
        waitForStart();

        while (Robot.state.gyroIsCalibrating("hero") == true) {
            waitOneFullHardwareCycle();
        }

        Robot.drivetrain.dumbGyroTurn(0, 0.7, 46);
        DcMotor noodle = hardwareMap.dcMotor.get("noodler");
        noodle.setPower(-1);
        redDoor.setPosition(0);
        Robot.drivetrain.moveDistanceWithCorrections(0.6, 100);
        Robot.tillLimitSwitch("leftLimit", "leftLimitServo", 0.2, 0.8, 0, 4);
        redDoor.setPosition(1);
        Thread.sleep(10);
        Robot.drivetrain.moveDistance(-0.6, 1, this);
        Thread.sleep(10);
        Robot.drivetrain.dumbGyroTurn(0.4, 44);

        noodle.setPower(1);
        SensorState.ColorType dominant = Robot.tillWhite(-0.175, "ground", "beacon");
        noodle.setPower(0);

        Robot.extendTillBeacon("beaconToucher");
        dominant = Robot.sameDominantColorFusion(dominant, Robot.state.redVsBlue("beacon"));
        Robot.dumpClimbers();
        if(dominant != SensorState.ColorType.RED && dominant != SensorState.ColorType.BLUE){
            dominant = Robot.tillColor("beacon",-1);
        }
        Robot.tel.addData("color", dominant);
        if(dominant == SensorState.ColorType.RED) {
            Robot.pushButton("beaconToucher", 1);
        }
        else if(dominant == SensorState.ColorType.BLUE) {
            Robot.pushButton("beaconToucher", -1);
        }
        Robot.drivetrain.move(0);
        Robot.drivetrain.stopMove();

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
