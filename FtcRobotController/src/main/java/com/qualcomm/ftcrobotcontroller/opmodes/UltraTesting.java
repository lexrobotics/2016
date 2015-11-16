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
    private final int FRONT_CENTER = 30;
    private final int REAR_CENTER = 80;

    @Override
    public void runOpMode() throws InterruptedException {

        Robot dave = new Robot(hardwareMap, telemetry, this); // makes Robot "dave"

        Robot.state = new SensorState(hardwareMap, 1, 0); // this block sets up sensors
        Robot.state.registerSensor("mr", SensorState.SensorType.COLOR, false, 12);
        Robot.state.registerSensor("mrs", SensorState.SensorType.LIGHT, true, 12);
        Robot.state.registerSensor("rearUltra", SensorState.SensorType.ULTRASONIC, true, 50);
        Robot.state.registerSensor("frontUltra", SensorState.SensorType.ULTRASONIC, true, 50);

        Thread state_thread = new Thread(Robot.state); // starts sensor thread
        state_thread.start();

        waitForStart(); // waits for start

        TwoWheelDrive dave_train = new TwoWheelDrive(   hardwareMap.dcMotor.get("leftdrive"), true,
                hardwareMap.dcMotor.get("rightdrive"), false, 4); // sets up two wheel drive

        dave.registerDriveTrain(dave_train);
        dave.registerUltrasonicServo("frontUltra", "frontSwivel");
        dave.registerUltrasonicServo("rearUltra", "rearSwivel");
        dave.registerServo("climber");

        dave.ultraservohelper.setPosition("frontUltra", FRONT_CENTER);
        dave.ultraservohelper.setPosition("rearUltra", REAR_CENTER);

        Thread.sleep(200);
        dave.setPosition("climber", 180);
        dave.drivetrain.moveDistance(-0.4, 48);
        Thread.sleep(100);
        dave.drivetrain.turnWithEncoders(0.4, 40);
        dave.drivetrain.moveDistance(-0.2, 15);
        dave.tillSenseTowards("frontUltra", 130, -0.2, 30, 10);
        Thread.sleep(10000);
        dave.ultraservohelper.setPosition("frontUltra", FRONT_CENTER);
        dave.ultraservohelper.setPosition("rearUltra", REAR_CENTER);
            dave.drivetrain.turnWithEncoders(0.4, 150);
        Thread.sleep(300);
        dave.parallel("frontUltra", "rearUltra", 0.30, 0.5, 30);

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
