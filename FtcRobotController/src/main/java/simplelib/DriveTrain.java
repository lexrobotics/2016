package simplelib;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Vivek Bhupatiraju on 1/20/16.
 */

public class DriveTrain
{
  private final double TURN_SCALAR = 0.23;

  private DcMotor[] left, right;
  private int[] leftEncoder, rightEncoder;
  private double wheel_circumference, expectedHeading;

  public DriveTrain(DcMotor[] left, boolean[] leftRev,
                    DcMotor[] right, boolean[] rightRev,
                    double wheel_diameter)
  {
        this.wheel_circumference = Math.PI * wheel_diameter;

        this.left = left;
        this.right = right;

        for (int i = 0; i < left.length; i++)
            if (leftRev[i]) this.left[i].setDirection(DcMotor.Direction.REVERSE);

        for (int i = 0; i < right.length; i++)
            if (rightRev[i]) this.right[i].setDirection(DcMotor.Direction.REVERSE);

        this.leftEncoder = new int[left.length];
        this.rightEncoder = new int[right.length];
        this.resetEncoders();
    }

    public void setLeftMotors(double power) {
        for (DcMotor l : left) l.setPower(power);
    }

    public void setRightMotors(double power) {
        for (DcMotor r : right) r.setPower(power);
    }

    public void resetEncoders() {
        for (int i = 0; i < left.length; i++) leftEncoder[i] = left[i].getCurrentPosition();
        for (int i = 0; i < right.length; i++) rightEncoder[i] = right[i].getCurrentPosition();
    }
}
