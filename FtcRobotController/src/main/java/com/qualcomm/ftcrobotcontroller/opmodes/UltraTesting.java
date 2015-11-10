package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.util.Range;

import lib.Robot;
import lib.TwoWheelDrive;
import lib.SensorState;


public class UltraTesting extends LinearOpMode {
    // Demo class for the new Robot classes.

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        Robot dave = new Robot(hardwareMap, telemetry, this);
        Robot.state = new SensorState(hardwareMap, 1, 0);
        Robot.state.registerSensor("mr", SensorState.SensorType.COLOR, false, 12);
        Robot.state.registerSensor("mrs", SensorState.SensorType.LIGHT, true, 12);
        Robot.state.registerSensor("rearUltra", SensorState.SensorType.ULTRASONIC, true, 50);
        Robot.state.registerSensor("frontUltra", SensorState.SensorType.ULTRASONIC, true, 50);


        Thread state_thread = new Thread(Robot.state);
        state_thread.start();


        TwoWheelDrive dave_train = new TwoWheelDrive(   hardwareMap.dcMotor.get("leftdrive"), true,
                hardwareMap.dcMotor.get("rightdrive"), false, 4);

        dave.registerDriveTrain(dave_train);
//        dave.registerUltrasonicServo("frontUltra", "frontSwivel");
//        dave.registerUltrasonicServo("rearUltra","rearSwivel");

//        dave.colorSweep(SensorState.ColorType.BLUE, 2, "mrs", "mr");

        while (opModeIsActive() && !(Thread.currentThread().isInterrupted())){
            telemetry.addData("frontAvg", Robot.state.getAvgSensorData("frontUltra", 60));
            telemetry.addData("rearAvg", Robot.state.getAvgSensorData("rearUltra", 60));
            telemetry.addData("frontReading", Robot.state.getSensorReading("frontUltra"));
            telemetry.addData("rearReading", Robot.state.getSensorReading("rearUltra"));

            try {
                Thread.sleep(1);
            } catch (InterruptedException ex){
                Thread.currentThread().interrupt();
                break;
            }
        }
        state_thread.interrupt();
    }
}
