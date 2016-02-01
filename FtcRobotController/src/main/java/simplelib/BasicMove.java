package simplelib;

public class BasicMove {

    public void move(int power)
    {
        SimpleRobot.drivetrain.setLeftPower(power);
        SimpleRobot.drivetrain.setRightPower(power);
    }

}