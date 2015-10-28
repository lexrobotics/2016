package lib;

/**
 * Created by luke on 10/7/15.
 */
public interface DriveTrain {
    // DriveTrain describes the drive setup of the robot.
    // This can be four wheel drive, two wheel drive, treads, etc, so we need to implement
    // this interface in other classes.
    public void move(float power);
    public void turnWithEncoders(float power, int degrees);
}
