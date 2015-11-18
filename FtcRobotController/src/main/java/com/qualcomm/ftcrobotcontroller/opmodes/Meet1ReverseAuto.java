package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import lib.Robot;
import lib.SensorState;
import lib.TwoWheelDrive;

/**
 * Created by lhscompsci on 11/18/15.
 */
public class Meet1ReverseAuto extends LinearOpMode {
    // The reverse of ultraTesting, starting from red instead of blue

    private final int FRONT_CENTER = 30;
    private final int REAR_CENTER = 140;

    @Override
    public void runOpMode() throws InterruptedException{
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

        Thread.sleep(200);
        dave.setPosition("climber", 180); // sets position
        dave.tillSenseAway("rearUltra", REAR_CENTER - 90, -0.4, 63, 10);
        Thread.sleep(100);
        dave.drivetrain.turnWithEncoders(0.4, 90);
        dave.tillSenseTowards("frontUltra", REAR_CENTER + 90, -0.4, 15, 10);
        dave.drivetrain.turnWithEncoders(0.4, -90);
        dave.colorSweep(SensorState.ColorType.RED, 5, "mrs", "mr", -0.4);

        dave.setPosition("climber", 5);

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
