package lib;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by noah on 3/9/16.
 */
public class AdafruitColorSensor {
    int led;
    int i2cMuxChannel;
    DeviceInterfaceModule cdim;
    Wire cs, mux;

    private int clear, red, green, blue;

    public AdafruitColorSensor(HardwareMap hmap, String colorName, String cdimName, int ledChannel) throws InterruptedException {
        this(hmap, colorName, cdimName, ledChannel, -1, null);
    }

    public AdafruitColorSensor(HardwareMap hmap, String colorName, String cdimName, int ledChannel, int i2cMuxChannel, Wire mux) throws InterruptedException {
        led = ledChannel;
        this.mux = mux;
        this.i2cMuxChannel = i2cMuxChannel;
        if(led != -1) {
            cdim = hmap.deviceInterfaceModule.get(cdimName);
            cdim.setDigitalChannelMode(led, DigitalChannelController.Mode.OUTPUT);
        }

        cs = new Wire(hmap, colorName, 2*0x29);

        selectSensor();
        Thread.sleep(10);
        cs.write(0x80, 0x03);                // R[00] = 3    to enable power
        cs.requestFrom(0x92, 1);            // R[12]        is the device ID
        cs.write(0x8F, 0x02);                // R[0F] = 2    to set gain 16
        cs.write(0x81, 0xEC);                // R[01] = EC   to set integration time to 20* 2.4 ms
        // 256 - 20 = 236 = 0xEC

        // start polling color sensor
        cs.requestFrom(0x93, 1);
    }

    public void setLed(boolean on) {
        if(led == -1) return;
        this.cdim.setDigitalChannelState(led, on);
    }

    public boolean isColorUpdate() throws InterruptedException{
        selectSensor();
        Thread.sleep(10);
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
                            Log.i("GST", String.format("ERROR reg 0x%02X Len = 0x%02X (!= 1)",
                                    regNumber, regCount));
                        }
                        break;
                    case 0x94:
                        cs.requestFrom(0x93,1);                     // Keep polling
                        if (regCount == 8) {                        // Check register count
                            clear       = cs.readLH();              // Clear color
                            red         = cs.readLH();              // Red color
                            green       = cs.readLH();              // Green color
                            blue        = cs.readLH();              // Blue color
                            isNew       = true;
                        } else {
                            Log.i("GST", String.format("ERROR reg 0x%02X Len = 0x%02X (!= 8)",
                                    regNumber, regCount));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return isNew;
    }

    private void selectSensor() {
        if(i2cMuxChannel >= 0 && i2cMuxChannel <= 7 && mux != null) {
            mux.write(0, 1<<i2cMuxChannel);
            while(mux.responseCount() < 1);
            mux.getResponse();
        }
    }

    public int getClear() {
        return clear;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

}
