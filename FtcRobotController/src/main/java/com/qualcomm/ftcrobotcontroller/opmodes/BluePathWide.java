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
public class BluePathWide extends LinearOpMode {
    public void path() throws InterruptedException {
        BotInit.bot2(hardwareMap, telemetry, this);
        boolean armTimeOut;
        Servo blueDoor;
        blueDoor = hardwareMap.servo.get("blueDoor");

        waitForStart();

        //initial turn
        Robot.drivetrain.pidGyroTurn(false, true, 45);
        Robot.closeSkirts();
        Thread.sleep(20);

        //Initial Move
        Robot.drivetrain.moveDistanceWithCorrections(-0.8, 90, false);
        Robot.tillLimitSwitch("rearLimit", "rightLimitServo", -0.3, 0.25, 1, 5, 0.2, true);
        blueDoor.setPosition(0);

        //Big Turn
        Robot.drivetrain.pidGyroTurn(false, true, -10);
        Robot.drivetrain.pidGyroTurn(true, false, -35);
        //        Thread.sleep(100);

        //TillWhite
        Robot.tillWhiteJumpThresh(0.175, "ground", "beacon", "blue");
        armTimeOut = Robot.extendTillBeacon("beaconToucher");

        if(armTimeOut){
            Robot.retractButtonPusher();
            Thread.sleep(10);
            Robot.tillWhiteJumpThresh(0.175, "ground", "beacon", "blue");
            Robot.extendTillBeacon("beaconToucher");
        }

        SensorState.ColorType  dominant = Robot.state.redVsBlueJumpThresh("beacon");
        Robot.dumpClimbers();

        //PushButton
        if (dominant == SensorState.ColorType.BLUE) {
            Robot.pushButton("beaconToucher", -1);
        } else if (dominant == SensorState.ColorType.RED) {
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
