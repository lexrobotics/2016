package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannelController;

import lib.Wire;

/**
 * Created by lhscompsci on 3/8/16.
 */
public class PurestColorSensor extends LinearOpMode {
    ColorSensor ground;
    DeviceInterfaceModule cdim;
    DcMotor leftFrontDrive, leftRearDrive;
    DcMotor rightFrontDrive, rightRearDrive;
    private Wire cs;

    private int                     readCount = 0;
    private long                    timeStamp;              // In microseconds
    private int                     clear, red, green, blue;

    @Override
    public void runOpMode() throws InterruptedException {
        ground = hardwareMap.colorSensor.get("ground");
        cdim = hardwareMap.deviceInterfaceModule.get("cdim");
        leftFrontDrive = hardwareMap.dcMotor.get("leftFrontDrive");
        leftRearDrive = hardwareMap.dcMotor.get("leftRearDrive");
        rightFrontDrive = hardwareMap.dcMotor.get("rightFrontDrive");
        rightRearDrive = hardwareMap.dcMotor.get("rightRearDrive");

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftRearDrive.setDirection(DcMotor.Direction.REVERSE);

        cdim.setDigitalChannelMode(5, DigitalChannelController.Mode.OUTPUT);
        cdim.setDigitalChannelState(5, true);
        Thread.sleep(1000);

        initColorSensor();

        waitForStart();
        while(opModeIsActive()) {
            if (isColorUpdate()) {
                readCount++;
                telemetry.addData("Count", readCount);
                telemetry.addData("Time", timeStamp / 1e6);
                telemetry.addData("Colors", "C:" + clear +
                        " R:" + red +
                        " G:" + green +
                        " B:" + blue);
            }
        }
        cs.close();

//        setAllMotors(0.25);
//        while(ground.green() < 450 && opModeIsActive()) {
//            try {
//                Thread.sleep(750);
//            }
//            catch(InterruptedException ie) {
//            }
//        }
//        setAllMotors(0);
    }

    void setAllMotors(double power) {
        leftFrontDrive.setPower(power);
        leftRearDrive.setPower(power);
        rightFrontDrive.setPower(power);
        rightRearDrive.setPower(power);
    }

    private void initColorSensor() {
        cs = new Wire(hardwareMap,"ground",2*0x29);

        cs.write(0x80, 0x03);                // R[00] = 3    to enable power
        cs.requestFrom(0x92, 1);            // R[12]        is the device ID
        cs.write(0x8F, 0x02);                // R[0F] = 2    to set gain 16
        cs.write(0x81, 0xEC);                // R[01] = EC   to set integration time to 20* 2.4 ms
        // 256 - 20 = 236 = 0xEC
    }
    private void startColorPolling() {
        cs.requestFrom(0x93, 1);            // Get sensor status
    }

    private boolean isColorUpdate() {
        boolean isNew = false;
        if (cs.responseCount() > 0) {
            cs.getResponse();
            int regNumber = cs.registerNumber();
            if (cs.isRead()) {
                int regCount = cs.available();
                switch (regNumber) {
                    case 0x93:
                        if (regCount == 1) {
                            int status = cs.read();
                            if ((status & 1) != 0) {
                                cs.requestFrom(0x94,8);             // Get colors
                            } else {
                                cs.requestFrom(0x93,1);             // Keep polling
                            }
                        } else {
                            telemetry.addData("Error", regNumber + " length 1 != " + regCount);
                            Log.i("GST", String.format("ERROR reg 0x%02X Len = 0x%02X (!= 1)",
                                    regNumber, regCount));
                        }
                        break;
                    case 0x94:
                        cs.requestFrom(0x93,1);                     // Keep polling
                        if (regCount == 8) {                        // Check register count
                            timeStamp   = cs.micros();              // Reading time
                            clear       = cs.readLH();              // Clear color
                            red         = cs.readLH();              // Red color
                            green       = cs.readLH();              // Green color
                            blue        = cs.readLH();              // Blue color
                            isNew       = true;
                        } else {
                            telemetry.addData("Error", regNumber + " length 8 != " + regCount);
                            Log.i("GST", String.format("ERROR reg 0x%02X Len = 0x%02X (!= 8)",
                                    regNumber, regCount));
                        }
                        break;
                    default:
                        telemetry.addData("Error", "Unexpected register " + regNumber);
                        break;
                }
            }
        }
        return isNew;
    }
}




