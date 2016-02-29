package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import lib.Robot;

/**
 * Created by lhscompsci on 2/24/16.
 */
public class RedPathScoot extends RedPath {
    @Override
    public void runOpMode()  {
        try {
            path();
            Robot.drivetrain.moveDistanceWithCorrections(0.6, 35);
            Robot.retractButtonPusher();
        }
        catch(InterruptedException ie) {
            Log.i("InterruptedException", "In LinearOpMode, ending autonomous hopefully");
            Robot.drivetrain.setLeftMotors(0);
            Robot.drivetrain.setRightMotors(0);
            Robot.motors.get("noodler").setPower(0);
            Robot.state_thread.interrupt();
            return;
        }
    }
}
