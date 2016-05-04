package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import lib.BotInit;
import lib.Menu;
import lib.Robot;

/**
 * Created by lhscompsci on 4/14/16.
 */
public class DefensiveBlueWidePath extends LinearOpMode {
    public void path() throws InterruptedException {
        BotInit.bot2(hardwareMap, telemetry, this);
        //menu
        Menu menu = new Menu();
        menu.run();
        waitForStart();

        ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);

        timer.reset();

        menu.delay();

        Robot.drivetrain.dumbGyroTurn(0.7, 0, 45);
        Thread.sleep(100);
        Robot.drivetrain.moveDistanceWithCorrections(1, 92);
        Thread.sleep(100);

        Robot.drivetrain.dumbGyroTurn(0, 0.7, 45);
        Robot.drivetrain.dumbGyroTurn(0, 0.7, 45);

        Thread.sleep(100);

        telemetry.addData("timer", timer.time());
        while(timer.time()<10.2) {
            telemetry.addData("timer",timer.time());

            Thread.sleep(1);
        }
        Robot.drivetrain.moveDistanceWithCorrections(.65, 61);

        while(timer.time() < 29 && opModeIsActive()) {
            Thread.sleep(1);
        }
        Robot.drivetrain.stopMove();
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
