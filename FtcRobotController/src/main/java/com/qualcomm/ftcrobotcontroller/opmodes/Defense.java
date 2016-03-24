package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import lib.BotInit;
import lib.Robot;

/**
 * Created by noah on 3/19/16.
 */
public class Defense extends LinearOpMode {
    public void path() throws InterruptedException {
        BotInit.bot2(hardwareMap, telemetry, this);

        int delayTime = (int)Robot.delaySet("delayDial","beaconToucher");
        waitForStart();
        Robot.delayWithCountdown(delayTime);


        while (Robot.state.gyroIsCalibrating("hero") == true) {
            waitOneFullHardwareCycle();
        }
        //initial turn
        Robot.drivetrain.dumbGyroTurn(0, .7, 45);

        DcMotor noodle = hardwareMap.dcMotor.get("noodler");
        noodle.setPower(1);
        Robot.drivetrain.moveDistanceWithCorrections(1, 119);
        Robot.drivetrain.dumbGyroTurn(0.4, 45);
        Robot.drivetrain.moveDistance(1,75,this);

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
