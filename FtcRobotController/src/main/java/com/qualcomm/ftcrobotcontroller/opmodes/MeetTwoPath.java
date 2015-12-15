package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Timer;

import lib.Robot;
import lib.SensorState;
import lib.TwoWheelDrive;

/**
 * Created by lhscompsci on 12/9/15.
 */
public class MeetTwoPath extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        /************
         * INITIALIZE
         ***********/

        Robot dave = new Robot(hardwareMap, telemetry, this); // makes Robot "dave"

        TwoWheelDrive dave_train = new TwoWheelDrive(hardwareMap.dcMotor.get("leftdrive"), true,
                hardwareMap.dcMotor.get("rightdrive"), false, 4);

        dave.registerDriveTrain(dave_train);
        dave.registerUltrasonicServo("frontUltra", "frontSwivel");
        DcMotor noodler = hardwareMap.dcMotor.get("noodler");

        Robot.state = new SensorState(hardwareMap, 1, 0);
        Robot.state.registerSensor("hero", SensorState.SensorType.GYRO, true, 12);
        Robot.state.registerSensor("mr", SensorState.SensorType.COLOR, false, 12);
        Robot.state.registerSensor("mrs", SensorState.SensorType.LIGHT, true, 12);
        Robot.state.registerSensor("rearUltra", SensorState.SensorType.ULTRASONIC, true, 50);
        Robot.state.registerSensor("frontUltra", SensorState.SensorType.ULTRASONIC, true, 50);

        ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
        timer.reset();

        Thread state_thread = new Thread(Robot.state);
        state_thread.start();

        waitForStart();

        /******
         * LOOP
         *****/

        while (Robot.state.calibrating("hero")) {
            Robot.tel.addData("Reading", "CALIBRATING");
        }

        noodler.setPower(1.0);
        dave_train.moveDistanceWithCorrections(.5, 135);
        noodler.setPower(-1.0);

        while (timer.time() < 25){
            Thread.sleep(10);
        }
        noodler.setPower(0);

        state_thread.interrupt();
    }
}