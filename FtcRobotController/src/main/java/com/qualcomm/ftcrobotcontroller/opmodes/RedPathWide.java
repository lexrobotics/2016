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
public class RedPathWide extends LinearOpMode {
    public void path() throws InterruptedException {
        BotInit.bot2(hardwareMap, telemetry, this);
        boolean armTimeOut;
        Menu menu = new Menu();
        menu.run();
        waitForStart();
        menu.delay();

        Robot.drivetrain.dumbGyroTurn(0, .7, 45);
        Robot.drivetrain.moveDistanceWithCorrections(1, 100, false);
        Robot.tillLimitSwitch("leftLimit", "leftLimitServo", 0.3, 0.75, 0, 4);
        Robot.drivetrain.moveDistanceWithCorrections(-0.4, 3);
        Thread.sleep(20);
        Robot.drivetrain.dumbGyroTurn(0, -0.7, 45);

        SensorState.ColorType dominant;
        Robot.tillWhiteJumpThresh(-0.19, "ground", "beacon", "red");

        armTimeOut = Robot.extendTillBeacon("beaconToucher");

        if(armTimeOut){
            Robot.retractButtonPusher();
            Thread.sleep(10);
            Robot.tillWhiteJumpThresh(-0.19, "ground", "beacon", "red");
            Robot.extendTillBeacon("beaconToucher");
        }

        Robot.servos.get("buttonPusher").setPosition(0.2); // press button pusher
        Thread.sleep(200);
        dominant = Robot.state.redVsBlueJumpThresh("beacon");
        Robot.servos.get("buttonPusher").setPosition(0.5);
        Robot.dumpClimbers();
        if(dominant == SensorState.ColorType.RED) {
            Robot.pushButton("beaconToucher", -1);
        }
        else if(dominant == SensorState.ColorType.BLUE) {
            Robot.pushButton("beaconToucher", 1);
        }
        else{

        }
        Robot.drivetrain.move(0);
        Robot.drivetrain.stopMove();

        new Thread(new Robot.RetractButtonPusherThread()).start();
        Thread.sleep(500);
        int scoot = menu.getScoot();
        if(scoot == Menu.SCOOT_FORWARD)
            Robot.drivetrain.moveDistanceWithCorrections(0.67, 48);
        else if(scoot == Menu.SCOOT_BACKWARDS)
            Robot.drivetrain.moveDistanceWithCorrections(-0.4, 24);
        else if(scoot == Menu.SCOOT_DEFENSE) {
            Robot.drivetrain.dumbGyroTurn(0.7, 0, 15);
            Robot.drivetrain.moveDistanceWithCorrections(1, 12);
            Robot.drivetrain.dumbGyroTurn(0.7, 0, 45);
            Robot.drivetrain.moveDistanceWithCorrections(1, 50);        }
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
