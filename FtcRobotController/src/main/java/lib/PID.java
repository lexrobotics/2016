package lib;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by noah on 11/30/15.
 */
public class PID {
    private final double Kp;
    private final double Ki;
    private final double Kd;
    private final boolean reversed;
    private final double iCap;
    private final double targetThresh;

    private double target;
    private double minOutput;
    private double maxOutput;

    private double iTerm;
    private double prevError;
    private double prevOutput;

    private ElapsedTime timer;

    public PID(double Kp, double Ki, double Kd) {
        this(Kp, Ki, Kd, false, 0, -1);
    }

    public PID(double Kp, double Ki, double Kd, boolean reversed, double targetThresh) {
        this(Kp, Ki, Kd, reversed, targetThresh, -1);
    }

    public PID(double Kp, double Ki, double Kd, boolean reversed, double targetThresh, double iCap) {
        this.Kp = Kp;
        this.Ki = Ki;
        this.Kd = Kd;
        this.reversed = reversed;
        this.targetThresh = targetThresh;
        this.iCap = iCap;
        this.target = 0;
        timer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
        reset();
    }

    public double update(double current) {
        double dt = timer.time(); // get time since last update
        double error = target - current;
        if(prevError == -1)
            prevError = error;

        if(prevOutput < maxOutput/2 && prevOutput > minOutput/2);
            iTerm += error * dt;

        if(iCap != -1)
            iTerm = Range.clip(iTerm, -1*iCap, iCap);

        double dTerm = (error - prevError)/dt;

        double output = Kp * error + Kd * (dt/(error - prevError));
//        double output = Kp * error + Ki * iTerm + Kd * dTerm;
        output = Range.clip(output, minOutput, maxOutput);

        prevOutput = output;
        if(reversed)
            output *= -1;

        prevError = error;
        timer.reset(); // reset timer and start counting time till next update
        return output;
    }

    public void reset() {
        iTerm = 0;
        prevError = -1;
        timer.reset();
    }

    public boolean setMinOutput(double minOutput) {
        if(minOutput > this.maxOutput)
            return false;
        this.minOutput = minOutput;
        return true;
    }

    public boolean setMaxOutput(double maxOutput) {
        if(maxOutput < this.minOutput)
            return false;

        this.maxOutput = maxOutput;
        return true;
    }

    public double getITerm() {
        return iTerm;
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public boolean isAtTarget() {
        if(prevError == -1)
            return false;
        return (targetThresh >= Math.abs(prevError));
    }
}
