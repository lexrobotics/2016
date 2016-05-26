package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
/**
 * Created by Skywu on 4/9/2016.
 * Simple TeleOp for lifting
 */
public class Lifting extends OpMode {
    DcMotor motor1;
    DcMotor motor2;
    @Override
    public void init() {
        motor1 = hardwareMap.dcMotor.get("fistStage");
        motor2 = hardwareMap.dcMotor.get("secondStage");
    }
    @Override
    public void loop() {
        if (gamepad1.a)
            motor1.setPower(1);
        if (gamepad1.b)
            motor1.setPower(-1);
        if (gamepad1.x)
            motor2.setPower(1);
        if (gamepad1.y)
            motor2.setPower(-1);
    }
}