package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import lib.BotInit;
import lib.Robot;

/**
 * Created by lhscompsci on 4/14/16.
 */
public class DefensiveBluePath extends LinearOpMode {

    public void path() throws InterruptedException {
        BotInit.bot2(hardwareMap, telemetry, this);
        ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
        waitForStart();
        timer.reset();

        Robot.drivetrain.dumbGyroTurn(0, 0.7, 45);
        Robot.closeSkirts();
        Thread.sleep(20);

        Robot.drivetrain.moveDistanceWithCorrections(-0.8, 50, false);
        Robot.drivetrain.dumbGyroTurn(0, 0.7, 30);
        Robot.drivetrain.dumbGyroTurn(0.7, 0, 15);

        Robot.drivetrain.moveDistanceWithCorrections(-0.8, 30, false);
        Robot.drivetrain.dumbGyroTurn(0, 0.7, 125);
        Robot.drivetrain.dumbGyroTurn(0.7, 0, 55);

        while (timer.time() < 10) {
            Thread.sleep(1);
        }
        Robot.drivetrain.move(.65, this);
        while (timer.time() < 29) {
            Thread.sleep(1);
        }
        Robot.drivetrain.stopMove();
    }

    @Override
    public void runOpMode() {
        try {
            path();
        } catch (InterruptedException ex) {
            Log.i("InterruptedException", "In LinearOpMode, ending autonomous hopefully");
            Robot.drivetrain.setLeftMotors(0);
            Robot.drivetrain.setRightMotors(0);
            Robot.motors.get("noodler").setPower(0);
            Robot.state_thread.interrupt();
            return;
        }
    }

}
