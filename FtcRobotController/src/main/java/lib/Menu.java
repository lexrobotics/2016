package lib;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.robocol.Telemetry;

/**
 * Created by lhscompsci on 4/15/16.
 */
public class Menu {
    Gamepad gamepad;
    Telemetry tel;

    private int entry = 0;
    private static final int ENTRY_DELAY = 0;
    private static final int ENTRY_SCOOT_TYPE = 1;
    private static final int ENTRY_COMPLETED = 2;
    private int delay = 0;

    private String[] scootTypes = {"Stop", "Forwards", "Backwards", "Defense"};
    private int selectedScoot = 0;
    public static final int SCOOT_NONE = 0;
    public static final int SCOOT_FORWARD = 1;
    public static final int SCOOT_BACKWARDS = 2;
    public static final int SCOOT_DEFENSE = 3;

    public Menu() {
        this.gamepad = Robot.waiter.gamepad1;
        this.tel = Robot.tel;
    }

    public void run() throws InterruptedException {
        while(!Robot.waiter.opModeIsActive()) {
            update();
            Thread.sleep(20);
        }
    }

    public void delay() throws InterruptedException {
        for(int i=delay; i>=0; i--) {
            Robot.tel.addData("TIME UNTIL LAUNCH", i);
            Thread.sleep(1000);
        }
    }

    public void update() {
        if(entry == ENTRY_DELAY) {
            tel.addData("Delay", delay);
        }
        if(entry == ENTRY_SCOOT_TYPE) {
            tel.addData("Scoot", scootTypes[selectedScoot]);
        }
        if(entry == ENTRY_COMPLETED) {
            tel.addData("Locked", String.format("d: %d, s: %s", delay, scootTypes[selectedScoot]));
        }

        if(gamepad.dpad_up) {
            while(gamepad.dpad_up);
            if(entry == ENTRY_DELAY) {
                delay++;
                if(delay > 29)
                    delay = 0;
            }
            if(entry == ENTRY_SCOOT_TYPE) {
                selectedScoot++;
                if(selectedScoot >= scootTypes.length) {
                    selectedScoot = 0;
                }
            }
        }
        if(gamepad.dpad_down) {
            while(gamepad.dpad_down);
            if(entry == ENTRY_DELAY) {
                delay--;
                if(delay < 0)
                    delay = 29;
            }
            if(entry == ENTRY_SCOOT_TYPE) {
                selectedScoot--;
                if(selectedScoot < 0) {
                    selectedScoot = scootTypes.length - 1;
                }
            }
        }
        if(gamepad.dpad_right) {
            while(gamepad.dpad_right);
            entry++;
            if(entry > ENTRY_COMPLETED)
                entry = ENTRY_COMPLETED;
        }
        if(gamepad.dpad_left) {
            while(gamepad.dpad_left);
            entry--;
            if(entry < 0)
                entry = 0;
        }
    }

    public boolean configEntered() {
        return (entry == ENTRY_COMPLETED);
    }

    public int getDelay() {
        return delay;
    }

    public int getScoot() {
        return selectedScoot;
    }
}
