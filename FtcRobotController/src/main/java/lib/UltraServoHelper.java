package lib;

import android.util.Log;

import com.qualcomm.robotcore.hardware.Servo;

import java.util.HashMap;

/**
 * Created by lhscompsci on 11/2/15.
 */
public class UltraServoHelper {
    HashMap<String, Servo> nameToServo;
    HashMap<String, Double> nameToCenter;
    public UltraServoHelper() {
        nameToServo = new HashMap<String, Servo>();
        nameToCenter = new HashMap<String, Double>();
    }
    void registerServo(String name, Servo serv, double center){
        Log.i("Nullcheck", "gonna check for null");
        if(nameToServo == null) {
            Log.i("Nullcheck", "nametoservo is null");
        }
        if(serv == null) {
            Log.i("Nullcheck", "servo is null");
        }
        if(name == null) {
            Log.i("Nullcheck", "name is null");
        }
        nameToServo.put(name, serv);
        nameToCenter.put(name, center);
        serv.setPosition(center);
    }
    public void setPosition(String name, int angle){
        nameToServo.get(name).setPosition((angle/180.0) - nameToCenter.get(name));
    }
}
