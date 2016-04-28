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
import lib.Menu;
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

        Menu menu = new Menu();
        menu.run();
        waitForStart();
        menu.delay();

        //initial turn
        Robot.drivetrain.dumbGyroTurn(0, -0.7, 45);
        Robot.closeSkirts();
        Thread.sleep(20);

        //Initial Move
        Robot.drivetrain.moveDistanceWithCorrections(-1, 90, false);
        Robot.tillLimitSwitch("rearLimit", "rightLimitServo", -0.3, 0.25, 1, 3, 0.2, true);

        // Pre-extend button pusher
        new Thread(new Robot.ExtendButtonPusherThread()).start();

        //Big Turn
        Robot.drivetrain.dumbGyroTurn(-0.4, 0.4, 43);

        //TillWhite
        Robot.tillWhiteJumpThresh(0.19, "ground", "beacon", "blue");
        armTimeOut = Robot.extendTillBeacon("beaconToucher");

        if(armTimeOut){
            Robot.retractButtonPusher();
            Thread.sleep(10);
            Robot.tillWhiteJumpThresh(0.19, "ground", "beacon", "blue");
            Robot.extendTillBeacon("beaconToucher");
        }

        SensorState.ColorType dominant;

        Robot.servos.get("buttonPusher").setPosition(0.2); // press button pusher
        Thread.sleep(200);
        dominant = Robot.state.redVsBlueJumpThresh("beacon");
        Robot.servos.get("buttonPusher").setPosition(0.5);
        Robot.dumpClimbers();

        //PushButton
        if (dominant == SensorState.ColorType.BLUE) {
            Robot.pushButton("beaconToucher", -1);
        } else if (dominant == SensorState.ColorType.RED) {
            Robot.pushButton("beaconToucher", 1);
        }

        Robot.drivetrain.move(0);
        Robot.drivetrain.stopMove();
        new Thread(new Robot.RetractButtonPusherThread()).start();
        Thread.sleep(500);
        int scoot = menu.getScoot();
        if(scoot == Menu.SCOOT_FORWARD)
            Robot.drivetrain.moveDistanceWithCorrections(-0.67, 48);
        else if(scoot == Menu.SCOOT_BACKWARDS)
            Robot.drivetrain.moveDistanceWithCorrections(0.8, 24);
        else if(scoot == Menu.SCOOT_DEFENSE) {
            Robot.drivetrain.dumbGyroTurn(0.7 , 135);
            Robot.drivetrain.moveDistanceWithCorrections(1, 75);
        }
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
