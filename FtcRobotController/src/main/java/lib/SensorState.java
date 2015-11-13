package lib;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.HashMap;

/**
 * Public fields
 */

/**
 * TODO:
 *  - Have private functions accept SensorContainers, not strings. More efficient
 *  - Rolling averages
 *  - Find out whether the getter functions need delays to not block run()
 *  - Exponential averages
 *  - Take away some of the unnecessary SensorData and SensorContainer constructors, use setters instead.
 *  - Change the names of the types_inv functions
 *  - Maybe store SensorState instance in this class as static?
 *  - Investigate problem of volatility:
 *
        // Potential problem that should be investigated: If two functions operate on a shared variable without synchronization,
        //    They might be reading or writing to variables only local to that thread.
        //    With synchronization, the problem disappears, but not all of the functions below are synchronized like that.
        //    Can also be declared volatile to fix.
        // Or, make more synchronized functions.
 *
 */

public class SensorState implements Runnable{


    public static class SensorData{
        // values is all the sensor data in chronological order, starting at index and wrapping around.
        public int index;
        public double[] values;
        public SensorState.ColorType[] colors;

        public SensorData(int index, double[] values) {
            this.index = index;
            this.values = values;
        }

        public SensorData(int index, SensorState.ColorType[] colors) {
            this.index = index;
            this.colors = colors;
        }
    }

    private static class SensorContainer {
        // We set these three manually
        public int index;
        public double[] values;                 // For all other sensors.
        public SensorState.ColorType[] colors;        // For ColorSensors

        public Object sensor;
        public boolean update;
        public String name;
        public SensorState.SensorType type;

        public SensorContainer(Object sensor, boolean update, SensorState.SensorType type, String name) {
            this.index = 0;
            this.sensor = sensor;
            this.update = update;
            this.type = type;
            this.name = name;
        }
    }

    // As long as the private methods are ONLY CALLED FROM run(), no synchronization errors should occur.

    // To make run() more readable
    public enum SensorType { GYRO, ULTRASONIC, COLOR, LIGHT, ENCODER }
    public enum ColorType{ RED, BLUE, WHITE, CLEAR, NONE }

    private HashMap<SensorType, HardwareMap.DeviceMapping> maps;    // HashMap of DeviceMappings from HardwareMap to grab sensor objects in registration.
    private HashMap<String, SensorContainer> sensors;                        // Stores SensorContainer objects (definition at bottom)
    private HashMap<SensorType, SensorContainer[]> types_inv;                // Allows recovery of all sensors of a certain type.
    private HardwareMap hmap;

    private DigitalChannel usPin;

    // interval determines how long run() waits between updates.
    private int milli_interval;
    private int nano_interval;

    public SensorState(HardwareMap hmap, int milli_interval, int nano_interval) {
        this.hmap = hmap;
        maps = new HashMap<SensorType, HardwareMap.DeviceMapping>();
        sensors = new HashMap<String, SensorContainer>();
        types_inv = new HashMap<SensorType, SensorContainer[]>();

        this.milli_interval = milli_interval;
        this.nano_interval = nano_interval;

        // Grab the DeviceMappings from the hardware map
        maps.put(SensorType.ULTRASONIC, hmap.analogInput);
        maps.put(SensorType.LIGHT, hmap.analogInput);
        maps.put(SensorType.GYRO, hmap.gyroSensor);
        maps.put(SensorType.COLOR, hmap.colorSensor);
        maps.put(SensorType.ENCODER, hmap.dcMotor);

        types_inv.put(SensorType.ULTRASONIC, new SensorContainer[0]);
        types_inv.put(SensorType.LIGHT, new SensorContainer[0]);
        types_inv.put(SensorType.GYRO, new SensorContainer[0]);
        types_inv.put(SensorType.COLOR, new SensorContainer[0]);
        types_inv.put(SensorType.ENCODER, new SensorContainer[0]);
    }

    /*
    ************************
    INITIALIZATION FUNCTIONS
    ************************
     */

    /**
     * Registers a sensor by creating a SensorContainer around it and adding the SensorContainer to
     * the internal HashMaps.
     * Special cases:
     *  - Calibrates a gyro
     *  - Initializes the Color SensorContainer differently and disables its LED
     *  - Adds a special ultrasonic pin to ultrasonic SensorContainers
     *
     * @param name          The name of the sensor to register, as recorded in the config
     * @param type          The type of the sensor to register, either GYRO, ULTRASONIC, COLOR, LIGHT, or ENCODER
     * @param update        Whether or not to update the sensor on each pass
     * @param data_length   The number of sensor readings to store for the sensor
     */

    public synchronized void registerSensor(String name, SensorType type, boolean update, int data_length){
        // Add a SensorContainer object to the sensors HashMap
        Object sensor_obj = maps.get(type).get(name);
        SensorContainer sen = new SensorContainer(sensor_obj, update, type, name);

        if (type == SensorType.GYRO)
            ((GyroSensor) sensor_obj).calibrate();

        // SensorContainers for ColorSensors have a different structure.
        if (type == SensorType.COLOR) {
            sen.colors = new ColorType[data_length];
            ((ColorSensor) sen.sensor).enableLed(false);
        }

        else {
            // Initialize with zeroes so that averaging doesn't have any issues.
            double[] values = new double[data_length];
            for (int i = 0; i < data_length; i++)
                values[i] = 0.0;
            sen.values = values;
        }

        sensors.put(name, sen);
        updateTypes_Inv(sen);
    }

    public void setUltrasonicPin(String pin_name){
        this.usPin = hmap.digitalChannel.get(pin_name);
    }

    /**
     * Updates the types_inv HashMap that maps from sensor types to lists of sensor names.
     *
     * @param sen   SensorContainer to add to the HashMap
     */
    private void updateTypes_Inv(SensorContainer sen){
        SensorContainer[] old_sensors = types_inv.get(sen.type);        // Pull out the old array of sensors
        int old_length = old_sensors.length;

        SensorContainer[] new_sensors = new SensorContainer[old_length + 1];

        System.arraycopy(old_sensors, 0, new_sensors, 0, old_length);               // Copy them into a new array
        new_sensors[old_length] = sen;                                              // Add the new sensor

        types_inv.put(sen.type, new_sensors);
    }

    /*
    *********************************
    SENSORCONTAINER WRITING FUNCTIONS
    *********************************
     */

    // Add a new value onto the array
    private void updateArray(SensorContainer sen, double value){
        int index = sen.index;
        index = (index + 1) % (sen.values.length);
        sen.values[index] = value;
        sen.index = index;
    }

    private void updateColorSensor(SensorContainer sen, ColorType color){
        int index = sen.index;
        index = (index + 1) % (sen.colors.length);
        sen.colors[index] = color;
        sen.index = index;
    }

    /*
    **************
    USER FUNCTIONS
    **************
     */

    /**
     * Start or stop updating the sensor values. Might want to use to prevent interference, or save
     * cpu cycles.
     *
     * @param name      Name of sensor to change the update status of
     * @param update    Whether or not to update the sensor in run()
     */
    public synchronized void changeUpdateStatus(String name, boolean update){
        sensors.get(name).update = update;
    }

    /**
     * Get the most important color visible to the sensor
     * Also used by run() to update the ColorSensor.
     * There shouldn't be any sync issues.
     *
     * @param name      Name of ColorSensor to get color from
     * @return          The most dominant color visible
     */
    public synchronized ColorType getDominantColor(String name) {
        ColorSensor sen_obj = (ColorSensor) sensors.get(name).sensor;
        int r = sen_obj.red(), b = sen_obj.blue(), g = sen_obj.green();

        if ((r > 0) && (b + g == 0))
            return ColorType.RED;
        if ((b > 0) && (r + g == 0))
            return ColorType.BLUE;
        if ((r == 1) && (b == 1) && (g == 1))
            return ColorType.WHITE;
        if (r + g + b == 0)
            return ColorType.CLEAR;
        return ColorType.NONE;
    }

    /**
     * Get an array of all sensor names belonging to sensors of a certain type.
     *
     * @param type      The type of sensor that you're looking for
     * @return          An array of strings containing the sensor names belonging to sensors of the specified type.
     */
    public synchronized String[] getSensorsFromType(SensorType type){
        SensorContainer[] sens = types_inv.get(type);
        String[] ret = new String[sens.length];

        for (int i = 0; i < sens.length; i++)       // Transfer names to new array
            ret[i] = sens[i].name;

        return ret;
    }

    public boolean gyroIsCalibrating(String gyro_name){
        return ((GyroSensor)sensors.get(gyro_name).sensor).isCalibrating();
    }

    /**
     * Extremely important user function. Used to get all currently stored chronological sensor data
     * for a sensor, along with the most recent index.
     *
     * @param name  The name of the sensor whose data you want to collect
     * @return      A SensorData instance containing a list of said data and an index pointing to the
     *              most recent reading.
     */
    public synchronized SensorData getSensorDataObject(String name){
        SensorContainer sen = sensors.get(name);

        if (sen.type == SensorType.COLOR)
            return new SensorData(sen.index, sen.colors);
        else
            return new SensorData(sen.index, sen.values);
    }

    /**
     * Returns a single sensor reading immediately, for when you can't wait a millisecond to get the reading.
     *
     * @param name      The name of the sensor you want to get a reading from
     * @return          The current reading of that sensor as a double
     */
    public double getSensorReading(String name){
        double value;

        synchronized (this) {
            SensorContainer sen = sensors.get(name);
            switch (sen.type) {
                case GYRO:
                    return ((GyroSensor) sen.sensor).getHeading();

                case ENCODER:
                    return ((DcMotor) sen.sensor).getCurrentPosition();

                case ULTRASONIC:
                    try {
                        usPin.setState(true);
                        Thread.sleep(0, 20);
                        usPin.setState(false);
                        value = ((5.0 / 1023.0) / 0.00977) * ((AnalogInput) sen.sensor).getValue();
                        Thread.sleep(0, 20);
                        return value;

                    } catch (InterruptedException ex){
                        ex.printStackTrace();
                    }

                case LIGHT:
                    return ((AnalogInput) sen.sensor).getValue();

                default:
                    return 0.0;
            }
        }
    }

    /**
     * Using the most recent chronological sensor data, average the last several readings
     *
     * @param name              The name of the sensor you want to get an average reading from
     * @param filter_length     The number of readings you want to average over
     * @return                  The average of the last (filter_length) readings
     */
    public synchronized double getAvgSensorData(String name, int filter_length) {
        SensorContainer senC = sensors.get(name);
        double[] data = senC.values;
        int len = data.length;
        int index = senC.index;
        double sum = 0;

        index = (index - (filter_length - 1)) % len;    // We subtract 1 because we want to include the most recent reading

        if (index < 0)              // Loop back around
            index = len + index;

        for (int i = 0; i < filter_length; i++){
            sum += data[index];
            index++;
            if (index >= len)
                index = 0;
        }

        sum /= (double) filter_length;
        return sum;
    }

    /*
    *************
    ACTUAL THREAD
    *************
     */

    /**
     * The body of the thread. This function continually updates all sensorContainers for which an
     * update is requested.
     * It loops through every sensor in the sensors HashMap, checks its type, and then updates it accordingly.
     * After doing so, it waits for milli_intervals + micro_intervals before the next round of updates.
     *
     * The pin fiddling at the beginning is there to synchronize the readings of the ultrasonics
     * so they don't interfere with each other. The pulse on the channel is the ultrasonic's signal to start a reading.
     */
    public void run() {

        while (true){
            try {
                if (usPin != null){                     // Prevent ultrasonic interference
                    usPin.setState(true);
                    Thread.sleep(0, 20);
                    usPin.setState(false);
                }

                synchronized (this) {                   // Can't let any reading happen while updating values
                    for (String key : sensors.keySet()) {
                        SensorContainer sen = sensors.get(key);

                        if (sen.update) {               // Only update if an update is requested
                            switch (sen.type) {
                                case GYRO:
                                    updateArray(sen, ((GyroSensor) sen.sensor).getHeading());
                                    break;

                                case ENCODER:
                                    updateArray(sen, ((DcMotor) sen.sensor).getCurrentPosition());
                                    break;

                                case ULTRASONIC:        // Converts from voltage to inches
                                    updateArray(sen, ((5.0/1023.0)/0.00977) * ((AnalogInput) sen.sensor).getValue());
                                    break;

                                case COLOR:
                                    updateColorSensor(sen, getDominantColor(key));
                                    break;

                                case LIGHT:
                                    updateArray(sen, ((AnalogInput) sen.sensor).getValue());
                                    break;

                                default: break;
                            }
                        }
                    }
                }

                Thread.sleep(milli_interval, nano_interval);        // The getter functions need time to grab the lock
            } catch (InterruptedException ex){
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}