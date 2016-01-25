package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.util.Range;

import lib.FourWheelDrive;
import lib.Robot;
import lib.SensorState;
import lib.TwoWheelDrive;

/**
 * Created by lhscompsci on 12/9/15.
 */
public class DropPath extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        TwoWheelDrive dave_train = new TwoWheelDrive(hardwareMap.dcMotor.get("leftdrive"), true,
                hardwareMap.dcMotor.get("rightdrive"), false, 10);

        Robot.init(hardwareMap, telemetry, this, dave_train, "hero");

        Robot.state = new SensorState(hardwareMap, 1, 0);
        Robot.state.registerSensor("mr", SensorState.SensorType.COLOR, false, 12);
        Robot.state.registerSensor("mrs", SensorState.SensorType.LIGHT, true, 12);
        Robot.state.registerSensor("hero", SensorState.SensorType.GYRO, true, 12);
        Robot.state.registerSensor("rearUltra", SensorState.SensorType.ULTRASONIC, true, 50);
        Robot.state.registerSensor("frontUltra", SensorState.SensorType.ULTRASONIC, true, 50);
        Robot.state.setUltrasonicPin("ultraToggle");

        Thread state_thread = new Thread(Robot.state);
        state_thread.start();

        waitForStart();

        while (Robot.state.gyroIsCalibrating("hero"));

//        dave_train.move(0.2, "hero", this);


//        Robot.colorSweep(SensorState.ColorType.BLUE, 4, 10, "mrs", "mr", -0.3);

        while (opModeIsActive()){
//            telemetry.addData("running", Robot.state.getSensorReading("hero"));
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex){
                break;
            }
        }

        state_thread.interrupt();

////        dave_train.moveDistance(0.5, 24);
////        Thread.sleep(100);
////        dave_train.turnWithGyro(45, "hero");
////        Thread.sleep(100);
////        dave.tillSense("rearUltra", 0, 0.5, 99, 10);
////        Thread.sleep(100);
////        dave_train.turnWithGyro(-45, "hero");
////        Thread.sleep(100);
////         thresholds need to be calibrated
//        dave.colorSweep(SensorState.ColorType.BLUE, 0, 0.5, "mrs", "mr", 0.3);
////        Thread.sleep(100);
//        //climber dropper
////        Thread.sleep(100);
    }
}