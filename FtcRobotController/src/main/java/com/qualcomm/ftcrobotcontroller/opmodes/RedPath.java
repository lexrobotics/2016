package com.qualcomm.ftcrobotcontroller.opmodes;

import android.hardware.Sensor;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.GyroSensor;
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
public class RedPath extends LinearOpMode {
    public void path() throws InterruptedException {
        boolean armTimeOut;
        BotInit.bot2(hardwareMap, telemetry, this);
        Servo redDoor;
        redDoor = hardwareMap.servo.get("redDoor");
//        int delayTime = (int)Robot.delaySet("delayDial","beaconToucher");
        waitForStart();
//        Robot.delayWithCountdown(delayTime);


        //initial turn
        Robot.drivetrain.pidGyroTurn(false, true, -45);
        DcMotor noodle = hardwareMap.dcMotor.get("noodler");


        //first movement
        Robot.drivetrain.moveDistanceWithCorrections(0.8, 60, false);
        Robot.tillLimitSwitch("leftLimit", "leftLimitServo", 0.3, 0.75, 0, 4);
        redDoor.setPosition(1);

        //turn to parallel
        Robot.drivetrain.pidGyroTurn(false, true, 45);
//        noodle.setPower(1);

        //turn to tillWhite
        SensorState.ColorType dominant;
        Robot.tillWhiteJumpThresh(0.175, "ground", "beacon", "red");
        noodle.setPower(0);

        //PushButton STuff
        armTimeOut = Robot.extendTillBeacon("beaconToucher");

        if(armTimeOut){
            Robot.retractButtonPusher();
            Thread.sleep(10);
            Robot.tillWhiteJumpThresh(0.175, "ground", "beacon", "red");
            Robot.extendTillBeacon("beaconToucher");
        }

        dominant = Robot.state.redVsBlueJumpThresh("beacon");
        Robot.dumpClimbers();
        telemetry.addData("Color detected", dominant);
        if(dominant == SensorState.ColorType.RED) {
            Robot.pushButton("beaconToucher", -1);
        }
        else if(dominant == SensorState.ColorType.BLUE) {
            Robot.pushButton("beaconToucher", 1);
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
