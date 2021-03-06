package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by luke on 10/7/15.
 */
public class DriveTrain {
    // DriveTrain is the base class for TwoWheelDrive and FourWheelDrive, which only differ on the most
    // basic levels of wheel control.

    //OVERRIDDEN:
    public void setLeftMotors(double power) {
        throw new RuntimeException("Bad function called from Drivetrain");
    }
    public void setRightMotors(double power) {
        throw new RuntimeException("Bad function called from Drivetrain");
    }
    public void setLeftMotors(double power, boolean thread) {
        throw new RuntimeException("Bad function called from Drivetrain");
    }
    public void setRightMotors(double power,boolean thread) {
        throw new RuntimeException("Bad function called from Drivetrain");
    }
    public void resetEncoders() {
        throw new RuntimeException("Bad function called from Drivetrain");
    }
    public boolean isAMotorZero() {
        throw new RuntimeException("Bad function called from Drivetrain");
    }
    public int getEncoders() {
        throw new RuntimeException("Bad function called from Drivetrain");
    }
    // END OF OVERRIDDEN

    protected double wheel_circumference;

    protected Thread move_thread;
    protected MovementThread mover;
    protected volatile boolean thread_running;

    protected double expectedHeading;
    protected final double TURN_SCALAR = 0.23;


    public double getExpectedHeading() { return expectedHeading; }

    // Returns the difference between the first angle and the second.
    // The returned value is between -180 and 180, so it does account for sign.
    // Accounts for modulus around 360
    public double angleDist(double deg1, double deg2) {
        double absDist = (360 + deg2 - deg1) % 360;
        if (absDist > 180)
            absDist -= 360;
        return absDist;
    }

    public void moveDistance(double power, double distance, LinearOpMode waiter) throws InterruptedException{
        this.resetEncoders();
        distance = (distance / wheel_circumference) * 1120;

        while (Math.abs(getEncoders()) < distance && waiter.opModeIsActive()) {
            setLeftMotors(power);
            setRightMotors(power);
            Robot.waiter.waitOneFullHardwareCycle();
        }

        setLeftMotors(0);
        setRightMotors(0);
    }

    public void move(double power){
        setLeftMotors(power);
        setRightMotors(power);
    }

    // Uses MovementThread to move a distance while correcting for nudges.
    public void moveDistanceWithCorrections(double power, double distance) throws InterruptedException {
        // 1120 ticks in the encoder
        resetEncoders();
        distance = (distance / wheel_circumference) * 1120;

        move(power, Robot.waiter);
        while (Math.abs(getEncoders()) < distance && Robot.waiter.opModeIsActive()) {
            Robot.waiter.waitOneFullHardwareCycle();
        }
        this.stopMove();
    }

    public void moveDistanceWithCorrections(double power, double distance, boolean stop) throws InterruptedException {
        // 1120 ticks in the encoder
        resetEncoders();
        distance = (distance / wheel_circumference) * 1120;

        move(power,Robot.waiter);

        while (Math.abs(getEncoders()) < distance && Robot.waiter.opModeIsActive()) {
            Robot.waiter.waitOneFullHardwareCycle();
        }
        if(stop) {
            this.stopMove();
        }
    }

    // Starts a new MovementThread, with protections to avoid creating a new one while one is running.
    public void move(double power, LinearOpMode waiter){
        if(!thread_running) {

//            mover = new MovementThread(power, 1, 10, .175, 0, 0);
            mover = new MovementThread(power, 1, 5, .125, 0.01, 0);

            move_thread = new Thread(mover);
            move_thread.start();
            thread_running = true;
        }

        else {
            mover.setPower(power);
        }
    }

    public void stopMove() throws InterruptedException {
        setLeftMotors(0);
        setRightMotors(0);

        if (thread_running){
            thread_running = false;
            move_thread.interrupt();
        }
        if(mover != null) {
            mover.setPower(0);
        }

        setLeftMotors(0);
        setRightMotors(0);

    }

    public void turnWithEncoders(double power, double angle) {
        resetEncoders();
        double target = TURN_SCALAR * (angle/wheel_circumference) * 1120;

        while(Math.abs(getEncoders()) < target) {
            setLeftMotors(-power);
            setRightMotors(power);
        }

        setLeftMotors(0);
        setRightMotors(0);
    }

    public void turnWithGyro(int degrees) throws InterruptedException {
        int i = 0;
        PID turnPID = new PID(0.011, 0.01, 0, true, 0, -1); // 0.003

        turnPID.setTarget(degrees);
        turnPID.setMaxOutput(1);
        turnPID.setMinOutput(-1);

        double update;

        while (Robot.waiter.opModeIsActive()&& !turnPID.isAtTarget()) {
            update = turnPID.update(Robot.state.getSensorReading(Robot.gyroName));

            Robot.tel.addData("PID update", scaleInput(update));
            Robot.tel.addData("PID error", turnPID.getError());
            Robot.tel.addData("loops", i);

            this.setRightMotors(scaleInput(update));
            this.setLeftMotors(scaleInput(-update));

            Thread.sleep(10);
            i++;
        }


        this.setLeftMotors(0);
        this.setRightMotors(0);
    }

    public void turn(double speed) {
        setRightMotors(speed);
        setLeftMotors(-speed);
    }

    /*This method scales the joystick input so for low joystick values, the
    * scaled value is less than linear.  This is to make it easier to drive
    * the robot more precisely at slower speeds. */
    private double scaleInput(double dVal)  {
        double direction = Math.signum(dVal);
        dVal = Math.abs(dVal);

        double[] scaleArray = { 0.0, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5,
                0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 1, 1.00 };

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);

        // index should be positive.
        if (index < 0) {
            index = -index;
        }

        // index cannot exceed size of array minus 1.
        if (index > 16) {
            index = 16;
        }

        // get value from the array.
        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        // return scaled value.
        return direction * dScale;
    }

    public void pidGyroTurn(boolean leftPower, boolean rightPower, double angle) throws InterruptedException {
        expectedHeading = (expectedHeading + angle + 360) % 360;

//        PID turnPID = new PID(0.03,0.01,0,false,0.75);
        PID turnPID = new PID(0.1,0.01,0,false,0);

        turnPID.setMaxOutput(1);
        turnPID.setMinOutput(-1);
        ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
        timer.reset();
        setLeftMotors(0);
        setRightMotors(0);
            do {
                Robot.tel.addData("turn",angleDist(Robot.state.getSensorReading("hero"), this.getExpectedHeading()));
                double correction = turnPID.updateWithError(angleDist(Robot.state.getSensorReading("hero"), this.getExpectedHeading()));
                if (leftPower) {
                    setLeftMotors(scaleInput(correction));
                }
                if (rightPower) {
                    setRightMotors(scaleInput(-correction));
                }
                Thread.sleep(10);
            } while (!turnPID.isAtTarget() && timer.time() < 1.5);


        setLeftMotors(0);
        setRightMotors(0);

    }
    public void pidGyroTurn(double angle) throws InterruptedException {
        pidGyroTurn(true,true,angle);

    }

    // NEVER MAKE THE ANGLE NEGATIVE. To turn in the negative direction, make the power negative.
    public void dumbGyroTurn(double power, double angle) throws InterruptedException {
        if(angle > 0)
            dumbGyroTurn(power, -power, angle);
        else
            dumbGyroTurn(-power, power, angle);
    }

    public void dumbGyroTurn(double powerLeft, double powerRight, double angle) throws InterruptedException {
        int sign = 1;

        if (powerLeft > powerRight){
            sign = -1;
        }

        expectedHeading = (expectedHeading + sign * angle + 360) % 360;

        ElapsedTime turntimer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
        turntimer.reset();

        setLeftMotors(powerLeft);
        setRightMotors(powerRight);

        while (Math.abs(angleDist(expectedHeading, Robot.state.getSensorReading(Robot.gyroName))) > 4 && Robot.waiter.opModeIsActive() && turntimer.time()<2.5) {
            Thread.sleep(10);
        }

        setLeftMotors(0);
        setRightMotors(0);
    }
}