package lib;

/**
 * Created by luke on 10/7/15.
 */
public interface DriveTrain {
    // DriveTrain describes the drive setup of the robot.
    // This can be four wheel drive, two wheel drive, treads, etc, so we need to implement
    // this interface in other classes.
    public void turnWithEncoders(float power, int degrees);
    public void move(double power);
    public void setLeftMotors(double power);
    public void setRightMotors(double power);
    public void resetEncoders();
    public int getEncoders();
//    public void moveWithCorrection(double power);
}
