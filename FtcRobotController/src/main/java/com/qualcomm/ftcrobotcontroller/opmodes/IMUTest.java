package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.hardware.ModernRoboticsColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cDevice;

import java.util.concurrent.locks.Lock;

/**
 * Created by noah on 9/30/15.
 */
public class IMUTest extends OpMode implements I2cController.I2cPortReadyCallback{
    I2cDevice imu;
    DeviceInterfaceModule dim;
    private byte[] readCache;
    private Lock readCacheLock;
    private byte[] writeCache;
    private Lock writeCacheLock;

    int i2cAddress = 0; // also could be 0x69

    @Override
    public void init() {
        imu = hardwareMap.i2cDevice.get("imu");
        imu.registerForI2cPortReadyCallback(this);

        readCache = imu.getI2cReadCache();
        readCacheLock = imu.getI2cReadCacheLock();
        writeCache = imu.getI2cWriteCache();
        writeCacheLock = imu.getI2cWriteCacheLock();
    }

    @Override
    public void loop() {
        try {
            i2cWrite(0, (byte)0x01);
            Thread.sleep(1000);
        } catch(InterruptedException e) {

        }
    }

    public void i2cWrite(int address, byte write) {
        while(!imu.isI2cPortReady()) { // TODO: implement timeout
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            writeCacheLock.lock();
            writeCache[I2cController.I2C_BUFFER_START_ADDRESS] = write; //TODO: wtf is this constant

        } finally {
            writeCacheLock.unlock();
        }

        imu.enableI2cWriteMode(i2cAddress, address, 1);
        imu.setI2cPortActionFlag(); // "set the 'go do it' flag"
        imu.writeI2cCacheToController();
    }

    public byte[] i2cRead(int address, int len) {
        imu.enableI2cReadMode(i2cAddress, address, len);
        while(!imu.isI2cPortInReadMode()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        byte[] ret = imu.getI2cReadCache();
        return ret;
    }

    @Override
    public void portIsReady(int i) {
        Log.i("PORTREADY", String.valueOf(imu.isI2cPortInReadMode()));
    }
}
