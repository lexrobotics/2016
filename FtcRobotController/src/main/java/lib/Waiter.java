package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by luke on 1/25/16.
 */
public class Waiter implements Runnable{
    private LinearOpMode opm;

    public Waiter(LinearOpMode opm) {
        this.opm = opm;
    }

    public void run() {
        while (opm.opModeIsActive()){
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex){
                throw new RuntimeException("Waiter Thread.sleep() interrupted.");
            }
        }

        System.exit(0);
    }
}
