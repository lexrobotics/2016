package simplelib.move;

import simplelib.SimpleRobot;

public class BasicMove {

    public void move(int power)
    {
        SimpleRobot.drivetrain.setLeftMotors(power);
        SimpleRobot.drivetrain.setRightMotors(power);
    }



}