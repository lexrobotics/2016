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
        Robot.drivetrain.dumbGyroTurn(0.7, 0, 45);

        Robot.drivetrain.moveDistanceWithCorrections(1, 60);
        Thread.sleep(100);
        Robot.drivetrain.dumbGyroTurn(-0.7, 0, 22);
        Thread.sleep(100);

        Robot.drivetrain.moveDistanceWithCorrections(1, 30);
        Thread.sleep(100);

        Robot.drivetrain.dumbGyroTurn(-0.7, 0, 65);
        Robot.drivetrain.moveDistanceWithCorrections(1, 18);

        Thread.sleep(100);

        while(timer.time() < 10 && opModeIsActive() ) {
            Thread.sleep(1);
        }
        Robot.drivetrain.move(.65,this);
        while(timer.time() < 29 && opModeIsActive()) {
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
