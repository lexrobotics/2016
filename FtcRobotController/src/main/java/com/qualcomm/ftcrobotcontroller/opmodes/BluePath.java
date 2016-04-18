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
import lib.Menu;
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

//        int delayTime = (int)Robot.delaySet("delayDial","beaconToucher");
        Menu menu = new Menu();
        menu.run();
        waitForStart();
        menu.delay();
        //        Robot.delayWithCountdown(delayTime);

        //initial turn
        Robot.drivetrain.dumbGyroTurn(0, -0.7, 45);
//
//
//        //Initial Move
        Robot.closeSkirts();
        Thread.sleep(20);

        Robot.drivetrain.moveDistanceWithCorrections(-1, 60, false);
        Robot.tillLimitSwitch("rearLimit", "rightLimitServo", -0.3, 0.25, 1, 5, 0.2, true);

        // Turn
        Robot.drivetrain.dumbGyroTurn(-0.4, 0.4, 43);

        //TillWhite
        Robot.tillWhiteJumpThresh(-0.175, "ground", "beacon", "blue");
        armTimeOut = Robot.extendTillBeacon("beaconToucher");

        if(armTimeOut){
            Robot.retractButtonPusher();
            Thread.sleep(10);
            Robot.tillWhiteJumpThresh(-0.175, "ground", "beacon", "blue");
            Robot.extendTillBeacon("beaconToucher");
        }



        SensorState.ColorType dominant = Robot.state.redVsBlueJumpThresh("beacon");
        Robot.dumpClimbers();

            Robot.tel.addData(dominant + "", "");

        //PushButton
        if(dominant == SensorState.ColorType.BLUE) {
            Robot.pushButton("beaconToucher", -1);
        }
        else if(dominant == SensorState.ColorType.RED) {
            Robot.pushButton("beaconToucher", 1);
        }
        Robot.drivetrain.move(0);
        Robot.drivetrain.stopMove();
        Robot.retractButtonPusher();
        int scoot = menu.getScoot();
        if(scoot == Menu.SCOOT_FORWARD)
            Robot.drivetrain.moveDistanceWithCorrections(-0.8, 24);
        else if(scoot == Menu.SCOOT_BACKWARDS)
            Robot.drivetrain.moveDistanceWithCorrections(0.8, 24);

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


