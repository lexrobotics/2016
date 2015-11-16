package lib;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.robocol.Telemetry;

import java.util.HashMap;

/**
 * Public fields:
 *
 * class SensorData
 *
 * enum SensorType
 * enum ColorType
 *
 * // Add a sensor to be tracked and updated in the thread.
 * void registerSensor(...)
 *
 * // Get the names of all sensors registered under a certain sensor type.
 * String getSensorsFromType(...)
 *
 * // Change whether a sensor gets updated on each loop or not.
 * void changeUpdateStatus(...)
 *
 * // Get an object containing all the recorded data of a sensor.
 * SensorData getSensorDataArray(...)
 *
 * // Get data directly from a sensor. (Without waiting)
 * double getSensorData(...)
 *
 * // Get data directly from a ColorSensor. (Without waiting)
 * SensorState.Color getColorData(...)
 *
 * // Return the average value over a specified number of recent points.
 * getAvgSensorData(...)
 *
 * The get...Data functions might want to have a Thread.sleep. Needs more testing.
 *
 * In any opmode using the gyro, you must wait for the gyro to finish calibrating before running the thread.
 */

/**
 * TODO:
 *  - Take away some of the unnecessary SensorData and SensorContainer constructors, use setters instead.
 *  - Change the names of the types_inv functions
 *  - Maybe store SensorState instance in this class as static?
 *
 */


// Potential problem that should be investigated: If two functions operate on a shared variable without synchronization,
//    They might be reading or writing to variables only local to that thread.
//    With synchronization, the problem disappears, but not all of the functions below are synchronized like that.
//    Can also be declared volatile to fix.
    // Or, make more synchronized functions.

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
        // Should this handle its own updating?
        public int index;
        public double[] values;                 // For all other sensors.
        public SensorState.ColorType[] colors;        // For ColorSensors
        public Object sensor;
        public boolean update;
        public String name;
        public SensorState.SensorType type;

        // Lets us synchronize the ultrasonics readings nicely.
        public DigitalChannel usPin;

        // These constructors are kinda dumb. Use setters instead
        private SensorContainer(Object sensor, boolean update, SensorState.SensorType type, String name) {
            this.index = 0;
            this.sensor = sensor;
            this.update = update;
            this.type = type;
            this.name = name;
        }

        public SensorContainer(double[] values, Object sensor, boolean update, SensorState.SensorType type, String name) {
            this(sensor, update, type, name);
            this.values = values;
//            avg = 0;
        }

        public SensorContainer(SensorState.ColorType[] colors, Object sensor, boolean update, SensorState.SensorType type, String name) {
            this(sensor, update, type, name);
            this.colors = colors;
        }

        public void addUsPin(DigitalChannel usPin){
            this.usPin = usPin;
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
        SensorContainer sen;

        if (type == SensorType.GYRO){
            ((GyroSensor) sensor_obj).calibrate();
        }

        // SensorContainers for ColorSensors have a different structure.
        if (type == SensorType.COLOR) {
            sen = new SensorContainer(new ColorType[data_length], sensor_obj, update, type, name);
            ((ColorSensor) sen.sensor).enableLed(false);
        }

        else {
            // Initialize with zeroes so that averaging doesn't have any issues.
            double[] values = new double[data_length];
            for (int i = 0; i < data_length; i++){
                values[i] = 0.0;
            }
            sen = new SensorContainer(values, sensor_obj, update, type, name);
        }

        // We need the pin to synchronize the ultrasonic readings
        if (type == SensorType.ULTRASONIC){
            sen.addUsPin(hmap.digitalChannel.get("ultraToggle"));
        }

        sensors.put(name, sen);
        addToRevTypes(sen);
    }

    /**
     * Updates the types_inv HashMap that maps from sensor types to lists of sensor names.
     * May want to change this to map types to SensorContainers, but still return String[] from the
     * getter.
     *
     * @param sen   SensorContainer to add to the HashMap
     */
    private void addToRevTypes(SensorContainer sen){
        // Pull out the old list of sensors of this type. Transfer them to a new list, and also add the new sensor.
        SensorType type = sen.type;
        SensorContainer[] old_sensors = types_inv.get(type);
        int old_length = old_sensors.length;

        // Make a new list that is one longer than the old one.
        SensorContainer[] new_sensors = new SensorContainer[old_length + 1];

        // Transfer all
        for (int i = 0; i < old_length; i++){
            new_sensors[i] = old_sensors[i];
        }

        // Add new one
        new_sensors[old_length] = sen;
        types_inv.put(type, new_sensors);
    }



    /*
    *********************************
    SENSORCONTAINER WRITING FUNCTIONS
    *********************************
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

    // Add a new value onto the array
    private void updateArray(String name, double value){
        SensorContainer sen = sensors.get(name);
        int index = sen.index;
        index = (index + 1) % (sen.values.length);
        sen.values[index] = value;
        sen.index = index;
    }

    private void updateColorSensor(String key, ColorType color){
        // I don't know a good way to do this that avoids the unnecessary data pull without memory leaks or a lot of deallocation.
        SensorContainer sen = sensors.get(key);
        int index = sen.index;
        index = (index + 1) % (sen.colors.length);
        sen.colors[index] = color;
        sen.index = index;
    }

    // Get the most important color visible to the sensor
    private ColorType getDominantColor(String name) {
        ColorSensor sen_obj = (ColorSensor) sensors.get(name).sensor;
        int r = sen_obj.red(), b = sen_obj.blue(), g = sen_obj.green();
//        Robot.tel.addData("Red: " ,r);
//        Robot.tel.addData("Blue: " ,b);
//        Robot.tel.addData("Green: " ,g);


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

    /*
    **************
    USER FUNCTIONS
    **************
     */


    public synchronized String[] getSensorsFromType(SensorType type){
        // I'm pretty sure this needs to be synchronized because an operation on one of the sensors might be underway.
        // Returns an array of all the names of all the sensors of the given type.
        SensorContainer[] sens = types_inv.get(type);
        String[] ret = new String[sens.length];

        for (int i = 0; i < sens.length; i++){
            ret[i] = sens[i].name;
        }

        return ret;
    }

    // Allow an opmode to wait for the gyro to calibrate
    public boolean calibrating(String gyro_name){
        if (((GyroSensor)sensors.get(gyro_name).sensor).isCalibrating())
            return true;
        return false;
    }

    public SensorData getSensorDataObject(String name){
        // Returns a SensorData object containing an array of values and an index for all sensors.
        try {
            // Make sure that even if this function is called several times consecutively, it will not block run()
            Thread.sleep(0, 10);
        } catch (InterruptedException ex){}

        synchronized (this) {
            SensorContainer sen = sensors.get(name);
            // ColorSensors are annoying and different.
            if (sen.type == SensorType.COLOR)
                return new SensorData(sen.index, sen.colors);
            else
                return new SensorData(sen.index, sen.values);
        }
    }

    public double getSensorReading(String name){
        // Get a sensor value immediately, if you need a single value without waiting for the interval
        double value = 0;

        try {
            // Make sure that even if this function is called several times consecutively, it will not block run()
            Thread.sleep(0, 10);
        } catch (InterruptedException ex){}

        synchronized (this) {
            SensorContainer sen = sensors.get(name);
            switch (sen.type) {
                case GYRO:
//                    if (((GyroSensor) sen.sensor).isCalibrating())
                        return ((GyroSensor) sen.sensor).getHeading();
//                    else
//                        return -1;

                case ENCODER:
                    return ((DcMotor) sen.sensor).getCurrentPosition();

                case ULTRASONIC:
                    try {
                        sen.usPin.setState(true);
                        Thread.sleep(0, 20);
                        sen.usPin.setState(true);
                        value = ((5.0 / 1023.0) / 0.00977) * ((AnalogInput) sen.sensor).getValue();
                        Thread.sleep(0, 20);

                        return value;
                    } catch (InterruptedException ex){
                        ex.printStackTrace();
                    }

                case LIGHT:
                    return ((AnalogInput) sen.sensor).getValue();
            }
            return 0.0;
        }
    }

    public synchronized double getAvgSensorData(String name, int points)
    {
        // As usual, won't work on ColorSensors
        // Averages over (points) data points of recent sensor values.

        SensorContainer senC = sensors.get(name);
        double[] data = senC.values;
        int len = data.length;
        int index = senC.index;
        double sum = 0;
        index = (index - (points - 1)) % len;

        if (index < 0){
            index = len + index;
        }

        for (int i = 0; i < points; i++){
            sum += data[index];
            index++;
            if (index >= len){
                index = 0;
            }
        }

        sum /= (double)points;
        return sum;
    }

//    public synchronized double getRollingAvgSensorData(String name, int filter_length)
//    {
//
//    }

//    public synchronized double getAvgSensorData(String name, int filter_length){
//        SensorContainer sen = sensors.get(name);
//        double[] values = sen.values;
//        int length = values.length;
//        int old_avg_index = sen.old_avg_index;
//        int index = sen.index;
//        assert filter_length < values.length;
//        int fl = sen.filter_length;
//        double avg = sen.avg;
//
//        int start_index = (index - filter_length + 1) % values.length;
//        if (start_index < 0) {start_index += length;}
//
//        sen.filter_length = filter_length;
//        sen.old_avg_index = start_index;
//
//        if (old_avg_index == -1){
//            for (int i = 0; i < filter_length; i++){
//                start_index++;
//                if (start_index == length){
//                    start_index = 0;
//                }
//                avg += values[start_index];
//            }
//            sen.avg = avg;
//            return avg / filter_length;
//            // In this case, filter_length must also be -1
//            // Do full averaging over filter_length
//        }
//
//        else if(filter_length != fl){
//            int difference = Math.abs(filter_length - fl);
//            avg *= filter_length;
//            if (filter_length < fl){
//                for (int i = old_avg_index + 1; i < start_index; i++){
//                    if (i >= length){i = 0;}
//                    avg -= values[i];
//                }
////                delete everything from old_avg_index + 1 to start_index, including old_avg_index + 1 but not including start_index
//            }
//            if (filter_length > fl){
//                for (int i = start_index + 1; i < old_avg_index; i++){
//                    if (i >= length){i = 0;}
//                    avg += values[i];
//                }
////                Add everything from start_index + 1 to old_avg_index, including start_index + 1 but not including old_avg_index;
//            }
//            avg += getSensorReading(name);
//            avg /= filter_length;
//            sen.avg = avg;
//            return avg;
//        }
//
//        else {
//            avg *= filter_length;
//            avg -= values[old_avg_index];
//            avg += getSensorReading(name);
//            avg /= filter_length;
//            return avg;
//        }
//    }

    // If we're returning a single value, we have to have two different functions for the two different return types.
    public synchronized ColorType getColorData(String name){
        return getDominantColor(name);
    }

    // ACTUAL THREAD CODE
    public void run() {
        double value = 0.0;
        ColorType color;

        while (true){
            try {

                SensorContainer us = sensors.get(getSensorsFromType(SensorType.ULTRASONIC)[0]);
                us.usPin.setState(true);
                Thread.sleep(0, 20000);
                us.usPin.setState(false);

                // Can't let any reading happen while updating values
                synchronized (this) {
                    // For every sensor name
                    for (String key : sensors.keySet()) {
                        SensorContainer sen = sensors.get(key);

                        // only update if an update is requested
                        if (sen.update) {
                            switch (sen.type) {
                                case GYRO:
                                    // If the gyrosensor is still calibrating, we should return -1 to indicate
//                                    if (((GyroSensor) sen.sensor).isCalibrating())
                                        value = ((GyroSensor) sen.sensor).getHeading();
//                                    else
//                                        value = -1;
                                    updateArray(key, value);
                                    break;

                                case ENCODER:
                                    value = ((DcMotor) sen.sensor).getCurrentPosition();
                                    updateArray(key, value);
                                    break;

                                case ULTRASONIC:
                                    // Convert from raw voltage to
//                                    sen.usPin.setState(true);
//                                    Thread.sleep(0, 20);
//                                    sen.usPin.setState(false);
                                    value = ((5.0/1023.0)/0.00977) * ((AnalogInput) sen.sensor).getValue();
                                    updateArray(key, value);
//                                    Thread.sleep(0, 20);
                                    break;

                                case COLOR:
                                    color = getDominantColor(key);
                                    updateColorSensor(key, color);
                                    break;

                                case LIGHT:
                                    value = ((AnalogInput) sen.sensor).getValue();
                                    updateArray(key, value);
                                    break;

                                default: break;
                            }
                        }
                    }
                }
                // I need to give getSensorData and the registration functions time to grab the lock.
                Thread.sleep(milli_interval, nano_interval);
            } catch (InterruptedException ex){
                break;
            }
        }
    }
}