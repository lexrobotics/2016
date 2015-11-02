package lib;

import com.qualcomm.robotcore.hardware.Servo;

import java.util.HashMap;

/**
 * Created by lhscompsci on 11/2/15.
 */
public class UltraServoHelper {
    HashMap<String, Servo> nameToServo;
    void UltraServoHelper() {
        nameToServo = new HashMap<String, Servo>;
    }
    void registerServo(String name, Servo serv){
        nameToServo.put(name, serv);
    }
    void setPosition(String name, int angle){
        nameToServo.get(name).setPosition(angle/180);
    }
}
