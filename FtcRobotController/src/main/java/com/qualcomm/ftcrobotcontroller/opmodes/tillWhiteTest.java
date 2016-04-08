package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.AdafruitColorSensor;
import lib.BotInit;
import lib.HelperFunctions;
import lib.Robot;
import lib.SensorState;
import lib.Wire;

/**
 * Created by noah on 3/18/16.
 */
public class tillWhiteTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException{
        BotInit.bot2(hardwareMap, telemetry, this);
        waitForStart();
//        Robot.tillWhite(0.2, "ground", "beacon");
//        SensorState.ColorType dominant;
//        dominant = Robot.tillWhiteJumpThresh (-0.175, "ground", "beacon", "red");
//        Robot.tel.addData(dominant + "", "");

        Robot.mux = new Wire(hardwareMap, "mux", 2*0x70);
        Robot.groundColorSensor = new AdafruitColorSensor(hardwareMap, "ground", "cdim", -1, 0, Robot.mux);
        Robot.beaconColorSensor = new AdafruitColorSensor(hardwareMap, "beacon", "cdim", -1, 1, Robot.mux);

        while (opModeIsActive()){
            Robot.tel.addData(Robot.state.redVsBlueJumpThresh("beacon") + "", "");
            Thread.sleep(1);
        }

    }
}

