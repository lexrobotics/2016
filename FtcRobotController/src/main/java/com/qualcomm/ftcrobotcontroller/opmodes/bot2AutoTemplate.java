package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.BotInits;
import lib.FourWheelDrive;
import lib.Robot;
import lib.HelperFunctions;
import lib.SensorState;

/**
 * Created by lhscompsci on 1/11/16.
 */
public class bot2AutoTemplate extends LinearOpMode {

    public void runOpMode() throws InterruptedException{
//        Garrett thinks that the dumbGyroTurn changes expectedHeading incorrectly
//        Changed dumbGyroTurn to stop when the angledistance starts increasing again.


        Robot dave = BotInits.bot2(hardwareMap, telemetry, this);
        waitForStart();
//        for(double i=0; i<1; i+=0.01) {
//            dave.drivetrain.turn(i);
//            telemetry.addData("power", i);
//            try {
//                Thread.sleep(100);
//            } catch(InterruptedException e) {
//
//            }
//        }
        while (Robot.state.gyroIsCalibrating("hero")) ;

//        dave.drivetrain.moveDistanceWithCorrections(0.6, "hero", 15, this);
//        Thread.sleep(200);
//        dave.drivetrain.dumbGyroTurn(-0.5, 45, "hero");
        Thread.sleep(200);
        dave.tillSense("ultra", 0.5, 0.5, 15, 10);
        dave.drivetrain.dumbGyroTurn(0.5, 45, "hero");
        Thread.sleep(200);

        dave.tillSense("ultra", 0.8, 0.5, 30, 10);

        dave.drivetrain.dumbGyroTurn(0.6, 90, "hero");

        while (Robot.state.gyroIsCalibrating("hero"));
        dave.colorSweep(SensorState.ColorType.RED, "light", "color", 0.2);
        while(opModeIsActive()){
            Thread.sleep(10);
        }
//        telemetry.addData("done", "yes");
    }
}
