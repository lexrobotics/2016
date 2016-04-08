package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
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
//
        boolean armTimeOut;
//
        Servo redDoor;
        redDoor = hardwareMap.servo.get("redDoor");
//        int delayTime = (int)Robot.delaySet("delayDial","beaconToucher");
        waitForStart();
//        Robot.delayWithCountdown(delayTime);

        //initial turn
        Robot.drivetrain.pidGyroTurn(false, true, 45);
//
        DcMotor noodle = hardwareMap.dcMotor.get("noodler");
//        noodle.setPower(-1);
        redDoor.setPosition(1);
        Thread.sleep(20);
//
//        //Initial Move
        Robot.closeSkirts();
        Thread.sleep(20);

        Robot.drivetrain.moveDistanceWithCorrections(-0.8, 70);
        Robot.tillLimitSwitch("rearLimit", "rightLimitServo", -0.3, 0.25, 1, 4, true);
//        noodle.setPower(0);

        // Turn
        Robot.drivetrain.pidGyroTurn(false, true, -10);
        telemetry.addData("done with", "first turn");
        Robot.drivetrain.pidGyroTurn(true, false, -35);
//        noodle.setPower(1);

        //TillWhite
        SensorState.ColorType dominant = Robot.tillWhiteJumpThresh(-0.175, "ground", "beacon", "blue");
        noodle.setPower(0);
        armTimeOut = Robot.extendTillBeacon("beaconToucher");

        if(armTimeOut){
            Robot.retractButtonPusher();
            Thread.sleep(10);
            Robot.tillWhiteJumpThresh(-0.175, "ground", "beacon", "blue");
            Robot.extendTillBeacon("beaconToucher");
        }



        dominant = Robot.sameDominantColorFusion(dominant, Robot.state.redVsBlueJumpThresh("beacon"));
        Robot.dumpClimbers();

        for (int i = 0; i < 20; i++){
            Robot.tel.addData(dominant + "", "");
            Thread.sleep(100);
        }

        //PushButton
        if(dominant == SensorState.ColorType.BLUE) {
            Robot.pushButton("beaconToucher", 1);
        }
        else if(dominant == SensorState.ColorType.RED) {
            Robot.pushButton("beaconToucher", -1);
        }
        Robot.drivetrain.move(0);
        Robot.drivetrain.stopMove();
        Robot.retractButtonPusher();

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


