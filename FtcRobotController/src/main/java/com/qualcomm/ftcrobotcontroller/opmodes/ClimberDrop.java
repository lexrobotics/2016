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


public class ClimberDrop extends LinearOpMode {
    // Demo class for the new Robot classes.
    private final int FRONT_CENTER = 30;
    private final int REAR_CENTER = 140;



    @Override
    public void runOpMode() throws InterruptedException {

        Robot dave = new Robot(hardwareMap, telemetry, this); // makes Robot "dave"

        Robot.state = new SensorState(hardwareMap, 1, 0);
        Robot.state.registerSensor("mr", SensorState.SensorType.COLOR, false, 12);
        Robot.state.registerSensor("mrs", SensorState.SensorType.LIGHT, true, 12);
        Robot.state.registerSensor("rearUltra", SensorState.SensorType.ULTRASONIC, true, 50);
        Robot.state.registerSensor("frontUltra", SensorState.SensorType.ULTRASONIC, true, 50);

        Thread state_thread = new Thread(Robot.state); // starts sensor thread
        state_thread.start();

        waitForStart();

        hardwareMap.dcMotor.get("noodler").setPower(-0.4); // turns on the harvester

        TwoWheelDrive dave_train = new TwoWheelDrive(   hardwareMap.dcMotor.get("leftdrive"), true,
                hardwareMap.dcMotor.get("rightdrive"), false, 4);

        dave.registerDriveTrain(dave_train);
        dave.registerUltrasonicServo("frontUltra", "frontSwivel");
        dave.registerUltrasonicServo("rearUltra", "rearSwivel");
        dave.registerServo("climber");

        Thread.sleep(500);
        dave.setPosition("climber", 180); // sets position
        dave.drivetrain.moveDistance(-0.4, 48); // moves forward
        Thread.sleep(500);
        dave.drivetrain.turnWithEncoders(0.4, 47); // 1st turn
        Thread.sleep(500);
        dave.drivetrain.moveDistance(-0.3, 35); // moves forward along diagonal
        Thread.sleep(500);
        dave.tillSenseTowards("frontUltra", 160, -0.2, 13, 10); // tillSense
        Thread.sleep(500);
        dave.ultraservohelper.setPosition("frontUltra", FRONT_CENTER);
        dave.ultraservohelper.setPosition("rearUltra", REAR_CENTER);
        Thread.sleep(500);
        dave.drivetrain.turnWithEncoders(0.5, 133); // turns w/ encoders
        Thread.sleep(500);

//         dave.parallel("frontUltra", "rearUltra", 0.30, 0.5, 30); //

        dave.colorSweep(SensorState.ColorType.BLUE, 4, 8, "mrs", "mr", -0.4);
        Thread.sleep(500);
//        dave.drivetrain.moveDistance(0.3, 5);
//        Thread.sleep(100);
        dave.setPosition("climber", 0);

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
