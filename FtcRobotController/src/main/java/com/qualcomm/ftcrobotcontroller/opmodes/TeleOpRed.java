package com.qualcomm.ftcrobotcontroller.opmodes;

/**
 * Created by noah on 12/17/15.
 */
public class TeleOpRed extends TeleOp {
    @Override
    public void loop() {
        super.loop();

        if (gamepad2.x) {
            door.setPosition(0);
        } else {
            door.setPosition(1);
        }
    }
}
