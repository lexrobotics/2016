package simplelib;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import java.util.HashMap;

/**
 * Created by Vivek Bhupatiraju (LW helped)
 */

public class DriveTrain
{
    private final double TURN_SCALAR = 0.23;
    private double wheel_circumference;

    private HashMap<String, DcMotor> left, right;
    private HashMap<String, Integer> leftEncoder, rightEncoder;

    public DriveTrain(String[] leftnames, String[] rightnames, double wheel_diameter)
    {
        this.wheel_circumference = Math.PI * wheel_diameter;
        for (String name : leftnames) left.put(name, SimpleRobot.hmap.dcMotor.get(name));
        for (String name : rightnames) right.put(name, SimpleRobot.hmap.dcMotor.get(name));
        resetEncoders();
    }
    
    public void reverseMotor(String name) {
        if (left.containsKey(name)) left.get(name).setDirection(DcMotor.Direction.REVERSE);
        else if (right.containsKey(name)) right.get(name).setDirection(DcMotor.Direction.REVERSE);
        else throw new RuntimeException("Motor not registered");
    }

    public void setLeftMotors(double power) {
        for (DcMotor l : left.values())
            l.setPower(power);
    }

    public void setRightMotors(double power) {
        for (DcMotor r : right.values())
            r.setPower(power);
    }

    public void resetEncoders() {
        for (String i : left.keySet())
            leftEncoder.put(i, left.get(i).getCurrentPosition());

        for (String i : right.keySet())
            rightEncoder.put(i, right.get(i).getCurrentPosition());
    }
    
    public int getEncoders() {
      int ans = 0;
      for (String name : left.keySet())
          ans += left.get(name).getCurrentPosition() - leftEncoder.get(name);
      for (String name : right.keySet())
          ans += right.get(name).getCurrentPosition() - rightEncoder.get(name);
      return ans / (left.keySet().size() + right.keySet().size());
    }
}
