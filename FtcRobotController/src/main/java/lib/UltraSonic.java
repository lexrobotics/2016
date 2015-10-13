package lib;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.AnalogInput;

/**
 * Created by luke on 10/13/15.
 */
public class UltraSonic {
    AnalogInput us;
    Servo serv;
    double offset;

    public UltraSonic(AnalogInput us) {
        this.us = us;
        offset = 0;
    }

    public UltraSonic(AnalogInput us, Servo serve) {
        this.us = us;
        this.serv = serve;
        offset = 0;
    }

    public void setOffset(double offset){
        this.offset = offset;
    }

    public void setAngle(double theta){
        // theta is measured in degrees
        serv.setPosition((offset + theta) / 180.0);
    }

}
