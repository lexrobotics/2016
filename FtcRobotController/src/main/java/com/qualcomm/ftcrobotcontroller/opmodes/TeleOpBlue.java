package com.qualcomm.ftcrobotcontroller.opmodes;

/**
 * Created by noah on 12/17/15.
 */
public class TeleOpBlue extends TeleOp {
    @Override
    public void loop() {
        super.loop();

        if(gamepad2.x) {
            door.setPosition(1);
        } else {
            door.setPosition(0);
        }
    }
}

