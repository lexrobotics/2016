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
public class BluePathWide extends LinearOpMode {
    public void path() throws InterruptedException {
        BotInit.bot2(hardwareMap, telemetry, this);
        int delayTime = (int) Robot.delaySet("delayDial", "beaconToucher");
        waitForStart();
        Robot.delayWithCountdown(delayTime);
        while (Robot.state.gyroIsCalibrating("hero")) {
            waitOneFullHardwareCycle();
        }

        Robot.drivetrain.dumbGyroTurn(1, 0, 47);
        DcMotor noodle = hardwareMap.dcMotor.get("noodler");
        noodle.setPower(-1);
        Thread.sleep(200);
        Robot.drivetrain.moveDistanceWithCorrections(0.6, 100);
        Robot.tillLimitSwitch("rightLimit", "rightLimitServo", 0.2, 0.25, 1, 1000);
        Robot.drivetrain.dumbGyroTurn(0.8, 130);

        SensorState.ColorType dominant = Robot.tillWhite(0.2, "ground", "beacon");
        int direction = 0;
        if (dominant == SensorState.ColorType.BLUE) {
            direction = -1;
        } else if (dominant == SensorState.ColorType.RED) {
            direction = 1;
        }

        noodle.setPower(0);
        Robot.pushButton("beaconToucher", direction);
        noodle.setPower(-1);
        Thread.sleep(1000);
        noodle.setPower(0);

        Robot.servos.get("buttonPusher").setPosition(0.8);
        Thread.sleep(2500);
        Robot.servos.get("buttonPusher").setPosition(0.5); // stop button pusher
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
