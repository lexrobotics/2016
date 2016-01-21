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
  private double wheel_circumference, expectedHeading;

  private HashMap<String, DcMotor> left, right;
  private HashMap<String, int> leftEncoder, rightEncoder;

  public DriveTrain(String[] leftnames, String[] rightnames, double wheel_diameter)
  {
        this.wheel_circumference = Math.PI * wheel_diameter;

        for (String name : leftnames) left.putIfAbsent(name, SimpleRobot.hmap.dcMotor.get(name));
        for (String name : rightnames) right.putIfAbsent(name, SimpleRobot.hmap.dcMotor.get(name));

        this.leftEncoder = new int[left.length];
        this.rightEncoder = new int[right.length];
        this.resetEncoders();
    }
    
    public void reverseMotor(String name) {
        if (left.containsKey(name)) left[name].setDirection(DcMotor.Direction.REVERSE);
        else if (right.containsKey(name)) right[name].setDirection(DcMotor.Direction.REVERSE);
        else throw new RuntimeException("Motor not registered");
    }

    public void setLeftMotors(double power) {
        for (DcMotor l : left.values()) l.setPower(power);
    }

    public void setRightMotors(double power) {
        for (DcMotor r : right.values()) r.setPower(power);
    }

    public void resetEncoders() {
        for (String i : left.keys()) leftEncoder[i] = left[i].getCurrentPosition();
        for (String i : right.keys()) rightEncoder[i] = right[i].getCurrentPosition();
    }
    
    public int getEncoders() {
      double ans = 0.0;
      for (int i = 0; i < left.length; i++) ans += left[i].getCurrentPosition() - leftEncoder[i];
      for (int i = 0; i < right.length; i++) ans += right[i].getCurrentPosition() - rightEncoder[i];
      return ans / (double)(left.length + right.length);
    }
}
