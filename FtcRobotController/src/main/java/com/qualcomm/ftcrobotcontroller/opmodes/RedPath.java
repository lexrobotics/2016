package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

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

            // Path
            Robot.drivetrain.dumbGyroTurn(0, 1, 45);
//            Robot.motors.get("noodler").setPower(.75);
            Thread.sleep(200);
            Robot.drivetrain.moveDistanceWithCorrections(0.25, 30);
            Robot.tillLimitSwitch("leftLimit", "leftLimitServo", 0.2, 0.8, 0, 1000);
            Robot.drivetrain.dumbGyroTurn(0.6, -0.4, 45);

            boolean dominant = (SensorState.ColorType.RED == Robot.tillWhite(0.2, "ground", "beacon"));
            if(dominant){

            }
            else{
                Robot.drivetrain.moveDistanceWithCorrections(0.25, 4);

            }

//            Robot.motors.get("noodler").setPower(0);

            while (opModeIsActive()) {
                telemetry.addData("done", "yes");
                Thread.sleep(10);
            }
        } catch(InterruptedException ex) {
            Log.i("InterruptedException", "In LinearOpMode, ending autonomous hopefully");
            Robot.drivetrain.setLeftMotors(0);
            Robot.drivetrain.setRightMotors(0);
            Robot.motors.get("noodler").setPower(0);
            return;
        }
    }
}
