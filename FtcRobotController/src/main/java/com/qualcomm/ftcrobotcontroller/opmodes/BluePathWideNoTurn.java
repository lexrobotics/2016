package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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
public class BluePathWideNoTurn extends LinearOpMode {
    public void path() throws InterruptedException {
        BotInit.bot2(hardwareMap, telemetry, this);
        boolean armTimeOut;
        Servo blueDoor;
        blueDoor = hardwareMap.servo.get("blueDoor");

        int delayTime = (int)Robot.delaySet("delayDial","beaconToucher");
        waitForStart();
        Robot.delayWithCountdown(delayTime);

        GyroSensor hero = hardwareMap.gyroSensor.get("hero");
        ElapsedTime timer = new ElapsedTime();
        hero.calibrate();
        timer.reset();
        while (!hero.isCalibrating() && opModeIsActive() && timer.time() < 0.5){
            Thread.sleep(1);
        }
        while(hero.isCalibrating() && opModeIsActive()){
            Thread.sleep(1);
        }

//        while (Robot.state.gyroIsCalibrating("hero") == true) {
//            waitOneFullHardwareCycle();
//        }

        //initial turn
//        Robot.drivetrain.dumbGyroTurn(0.7, 0, 47);
        DcMotor noodle = hardwareMap.dcMotor.get("noodler");
        noodle.setPower(1);

        Thread.sleep(20);

        //Initial Move
        Robot.drivetrain.moveDistanceWithCorrections(1, 100);
        Robot.tillLimitSwitch("rightLimit", "rightLimitServo", 0.2, 0.25, 1, 4);
        blueDoor.setPosition(0);

        //Big Turn
        Robot.drivetrain.dumbGyroTurn(0.4, 130);
//        Thread.sleep(100);

        //TillWhite
        SensorState.ColorType dominant = Robot.tillWhiteJumpThresh(0.175, "ground", "beacon", "blue");
        noodle.setPower(0);
        armTimeOut = Robot.extendTillBeacon("beaconToucher");

        if(armTimeOut){
            Robot.retractButtonPusher();
            Thread.sleep(10);
            Robot.tillWhiteJumpThresh(0.175, "ground", "beacon", "blue");
            Robot.extendTillBeacon("beaconToucher");
        }

        dominant = Robot.oppositeDominantColorFusion(dominant, Robot.state.redVsBlue("beacon"));
        Robot.dumpClimbers();

        //PushButton
        if (dominant == SensorState.ColorType.BLUE) {
            Robot.pushButton("beaconToucher", 1);
        } else if (dominant == SensorState.ColorType.RED) {
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
