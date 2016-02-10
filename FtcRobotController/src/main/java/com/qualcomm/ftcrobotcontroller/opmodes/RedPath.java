package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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
public class RedPath extends LinearOpMode {

    @Override
    public void runOpMode()  {
//        Garrett thinks that the dumbGyroTurn changes expectedHeading incorrectly
//        Changed dumbGyroTurn to stop when the angledistance starts increasing again.



        try {


            BotInit.bot2(hardwareMap, telemetry, this);
            waitForStart();
            while (Robot.state.gyroIsCalibrating("hero")) {
                waitOneFullHardwareCycle();
            }

//             Path
            Robot.drivetrain.dumbGyroTurn(0, 1, 45);
//            Robot.motors.get("noodler").setPower(.75);
            Thread.sleep(200);
            Robot.drivetrain.moveDistanceWithCorrections(0.25, 30);
            Robot.tillLimitSwitch("leftLimit", "leftLimitServo", 0.2, 0.8, 0, 1000);
            Robot.drivetrain.dumbGyroTurn(0.6, -0.6, 45);
            Thread.sleep(200);
            Robot.pushButton("beaconToucher", SensorState.ColorType.RED);
            Robot.servos.get("buttonPusher").setPosition(0.8);
            Thread.sleep(2500);
            Robot.servos.get("buttonPusher").setPosition(0.5); // stop button pusher



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
