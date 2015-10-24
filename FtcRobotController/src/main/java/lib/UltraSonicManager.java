package lib;
import java.util.HashMap;

/**
 * Created by luke on 10/13/15.
 */
public class UltraSonicManager {
    // should define movement functions using ultrasonics, eg tillsense, using ultrasonic control provided by UltraSonic instances
    // movement supplied by drivetrain

    private HashMap<String, UltraSonic> ultrasonics;

    public UltraSonicManager() {
        this.ultrasonics = new HashMap<String, UltraSonic>();
    }

    public UltraSonicManager(HashMap<String, UltraSonic> ultrasonics){
        this.ultrasonics = ultrasonics;
    }

    public void addUltraSonic(UltraSonic us, String key){
        ultrasonics.put(key, us);
    }

    public void tillSense(String key, int speed, int threshold){}
}
