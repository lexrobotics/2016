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
public class BluePathWide extends LinearOpMode {
    public void path() throws InterruptedException {
        BotInit.bot2(hardwareMap, telemetry, this);
        Servo blueDoor;
        blueDoor = hardwareMap.servo.get("blueDoor");

        int delayTime = (int)Robot.delaySet("delayDial","beaconToucher");
        waitForStart();
        while (Robot.state.gyroIsCalibrating("hero") == true) {
            waitOneFullHardwareCycle();
        }

        //initial turn
        Robot.drivetrain.dumbGyroTurn(0.7, 0, 46);
        DcMotor noodle = hardwareMap.dcMotor.get("noodler");
        noodle.setPower(-1);
        blueDoor.setPosition(1);
        Thread.sleep(20);

        //Initial Move
        Robot.drivetrain.moveDistanceWithCorrections(0.6, 100, false);
        Robot.tillLimitSwitch("rightLimit", "rightLimitServo", 0.2, 0.25, 1, 4);
        blueDoor.setPosition(0);

        //Big Turn
        Robot.drivetrain.dumbGyroTurn(0.4, 134);
        Thread.sleep(200);
        noodle.setPower(1);

        //TillWhite
        SensorState.ColorType dominant = Robot.tillWhite(0.175, "ground", "beacon", "blue");
        noodle.setPower(0);
        Robot.extendTillBeacon("beaconToucher");
        dominant = Robot.dominantColorFusion(dominant, Robot.state.redVsBlue("beacon"));
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
