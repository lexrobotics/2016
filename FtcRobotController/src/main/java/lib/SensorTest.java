package lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by lhscompsci on 11/24/15.
 */
public class SensorTest {
    LinearOpMode waiter;
    static LinearOpMode waiter_static;

    public SensorTest(LinearOpMode waiter){
        this.waiter = waiter;
    }

    public static void setWaiter(LinearOpMode waiter_s){
        waiter_static = waiter_s;
    }

    public static void displayStatic(String sensor_name){
        while (waiter_static.opModeIsActive()){
            Robot.tel.addData("Sensor Readings", Robot.state.getSensorReading(sensor_name));
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex){
                break;
            }
        }
    }

    public void display(String sensor_name){
        while (waiter.opModeIsActive()){
            Robot.tel.addData("Sensor Readings", Robot.state.getSensorReading(sensor_name));
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex){
                break;
            }
        }
    }
}
