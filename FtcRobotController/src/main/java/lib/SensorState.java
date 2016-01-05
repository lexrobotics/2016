package lib;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.HashMap;

/**
 * public enum SensorType { GYRO, ULTRASONIC, COLOR, LIGHT, ENCODER }
 * public enum ColorType{ RED, BLUE, WHITE, CLEAR, NONE;
     * public static int toInt(ColorType c)
     * public static int[] toInt(ColorType[] c)
     * public static ColorType toColor(int i){
     * public static ColorType[] toColor(int[] i)
 * }
 *
 * public SensorState(HardwareMap hmap, int milli_interval, int nano_interval)
 * public synchronized void registerSensor(String name, SensorType type, boolean update, int data_length)
 * public void setUltrasonicPin(String pin_name)
 * public boolean gyroIsCalibrating(String gyro_name)
 * public synchronized ColorType getColorData(String name)
 * public synchronized double getSensorReading(String name)
 * public synchronized void changeUpdateStatus(String name, boolean update)
 * public synchronized Filter getFilter(String name)
 * public synchronized double getAvgSensorData(String name)
 * public synchronized String[] getSensorsFromType(SensorType type)
 * public synchronized boolean filterIsFilled(String name)
 * public synchronized void changeFilterLength(String name, int fl)
 * public void run()
 * */

/*
PRECAUTIONS:
Never try reading gyro values until it's calibrated.
Some of the private functions don't need to be synchronized only because they are only ever called from run()
NEVER make them public. Very sneaky things could follow.
The ColorSensors get their own functions. Don't try to use the general-purpose ones for color.
Early in the program, remember that not all of the filter array will be filled, so the averages will start near zero.
Remember to delete SensorData objects you get to avoid leaks.  (Actually that's probably handled by garbage collection)
Also, NEVER EVER return an actual filter object being used in the SensorState. only return clones, to avoid synchronization issues.
 */

//New system: all of our data is in a filter. Colors become ints before they go in, and we get the colors back with a Sensorstate function
// use ordinal and .values()

/**
 * TODO:
 *  - Find out whether the getter functions need delays to not block run()
 *  - Exponential averages
 *  - Investigate problem of volatility:
 *
 // Potential problem that should be investigated: If two functions operate on a shared variable without synchronization,
 //    They might be reading or writing to variables only local to that thread.
 //    With synchronization, the problem disappears, but not all of the functions below are synchronized like that.
 //    Can also be declared volatile to fix.
 // Or, make more synchronized functions.
 */

public class SensorState implements Runnable{
    public static SensorState state;

    /**
     * Container class that stores a sensor object and relevant information to that object.
     */
    private static class SensorContainer {
        public Filter filter;

        public Object sensor;
        public SensorState.SensorType type;

        public boolean update;
        public String name;

        public SensorContainer(Object sensor, SensorState.SensorType type, String name, boolean update, int size) {
            this.filter = new Filter(size);
            this.sensor = sensor;
            this.update = update;
            this.type = type;
            this.name = name;
        }
    }

    // To make run() more readable
    public enum SensorType { GYRO, ULTRASONIC, COLOR, LIGHT, ENCODER }

    public enum ColorType{ RED, BLUE, WHITE, CLEAR, NONE;
        public static int toInt(ColorType c){
            return c.ordinal();
        }

        public static int[] toInt(ColorType[] c){
            int[] newArr = new int[c.length];
            for (int a = 0; a < c.length; a++){
                newArr[a] = toInt(c[a]);
            }
            return newArr;
        }

        public static ColorType toColor(int i){
            return values()[i];
        }

        public static ColorType[] toColor(int[] i){
            ColorType[] newArr = new ColorType[i.length];
            for (int a = 0; a < i.length; a++){
                newArr[a] = toColor(i[a]);
            }
            return newArr;
        }
    }

    private HashMap<SensorType, HardwareMap.DeviceMapping> maps;    // HashMap of DeviceMappings from HardwareMap to grab sensor objects in registration.
    private HashMap<String, SensorContainer> sensorContainers;                        // Stores SensorContainer objects (definition at bottom)
    private HashMap<SensorType, SensorContainer[]> types_inv;                // Allows recovery of all sensors of a certain type.
    private HardwareMap hmap;

    private DigitalChannel usPin;
    private boolean usPinWasSet;

    // interval determines how long run() waits between updates.
    private int milli_interval;
    private int nano_interval;

    /**
     * Constructor accepts the interval of delay for run() and the relevant hardwareMap, taken from the running opMode.
     */
    public SensorState(HardwareMap hmap, int milli_interval, int nano_interval) {
        this.hmap = hmap;
        maps = new HashMap<SensorType, HardwareMap.DeviceMapping>();
        sensorContainers = new HashMap<String, SensorContainer>();
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

        usPinWasSet = false;
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
     *
     * @param name          The name of the sensor to register, as recorded in the config
     * @param type          The type of the sensor to register, either GYRO, ULTRASONIC, COLOR, LIGHT, or ENCODER
     * @param update        Whether or not to update the sensor on each pass
     * @param data_length   The number of sensor readings to store for the sensor
     */
    public synchronized void registerSensor(String name, SensorType type, boolean update, int data_length){
        Object sensor_obj = maps.get(type).get(name);                                   // Get underlying sensor object for the sensor
        SensorContainer sen = new SensorContainer(sensor_obj, type, name, update, data_length);      // Make a SensorContainer to wrap around the object

        if (type == SensorType.GYRO)
            ((GyroSensor) sensor_obj).calibrate();

        if (type == SensorType.COLOR) {
            ((ColorSensor) sensor_obj).enableLed(false);
        }

        sensorContainers.put(name, sen);
        updateTypes_Inv(sen);
    }

    /**
     * The ultrasonics can interfere with each other if they fall out of sync. The pin lets us
     * notify them in a consistent way.
     *
     * @param pin_name      The name of the digitalChannel in the config that connects to the ultrasonics
     */
    public void setUltrasonicPin(String pin_name){
        if (!this.usPinWasSet) {
            this.usPin = hmap.digitalChannel.get(pin_name);
            this.usPinWasSet = true;
        }
    }

    /**
     * Updates the types_inv HashMap.
     *
     * @param sen   SensorContainer to add to the HashMap. This will be grouped into the array indexed by the sensor's type
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
    ************************
    PUBLIC RUNTIME FUNCTIONS
    ************************
     */

    /**
     * Returns true if the given gyro is currently calibrating, and therefore can't give good values.
     */
    public synchronized boolean gyroIsCalibrating(String gyro_name){
        return ((GyroSensor)sensorContainers.get(gyro_name).sensor).isCalibrating();
    }

    /**
     * Returns true if all the values of the filter array have been filled, allowing averaging.
     */
    public synchronized boolean filterIsFilled(String name){
        return sensorContainers.get(name).filter.isFilled();
    }

    public synchronized void changeFilterLength(String name, int fl){
        sensorContainers.get(name).filter.changeFilter_length(fl);
    }

    /**
     * These two are the public wrappers around the private getDominantColor and getSensorReading functions.
     * Allows us to access by name only in the public functions.
     */
    public synchronized ColorType getColorData(String name){
        return getDominantColor(sensorContainers.get(name));
    }

    /**
     * Immediately return a value for the given sensor, without waiting for another run().
     */
    public synchronized double getSensorReading(String name){
        return getSensorReading(sensorContainers.get(name));
    }

    /**
     * Start or stop updating the sensor values. Might want to use to prevent interference, or save
     * cpu cycles.
     */
    public synchronized void changeUpdateStatus(String name, boolean update){
        sensorContainers.get(name).update = update;
    }

    /**
     * Used to get all currently stored chronological sensor data for a sensor, along with the most recent index.
     * Clones to avoid sync issues, Should therefore be deleted after use.
     *
     * @return      The SensorData object corresponding to the given sensor.
     */
    public synchronized Filter getFilter(String name){
        return sensorContainers.get(name).filter.clone();
    }

    /**
     * Using the most recent chronological sensor data, average the last several readings
     */
    public synchronized double getAvgSensorData(String name) {
        return sensorContainers.get(name).filter.getAvg();
    }

    /**
     * Get a String[] array of all sensor names belonging to sensors of a certain type.
     */
    public synchronized String[] getSensorsFromType(SensorType type){
        SensorContainer[] sens = types_inv.get(type);
        String[] ret = new String[sens.length];

        for (int i = 0; i < sens.length; i++)       // Transfer names to new array
            ret[i] = sens[i].name;
        return ret;
    }






    /*
    *********************************
    SENSORCONTAINER WRITING FUNCTIONS
    *********************************
     */

    /**
     * Get the most important color visible to the sensor
     * Also used by run() to update the ColorSensor.
     * There shouldn't be any sync issues.
     *
     * @return          The most dominant color visible, as a SensorState.ColorType
     */

    //should be private, no synch
    private ColorType getDominantColor(SensorContainer sen) {
        ColorSensor sen_obj = (ColorSensor) sen.sensor;
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
     * Returns a single sensor reading immediately, for when you can't wait a millisecond to get the reading.
     * Use getDominantColor instead for ColorSensors.
     * The pin fiddling with UltraSonic prevents interference.
     */
    private double getSensorReading(SensorContainer sen){
        double value;

        synchronized (this) {
            switch (sen.type) {
                case GYRO:
                    return ((GyroSensor) sen.sensor).getHeading();

                case ENCODER:
                    return ((DcMotor) sen.sensor).getCurrentPosition();

                case ULTRASONIC:
                    // If it hasn't been set, we get a nullpointer exception
                    if (this.usPinWasSet) {
                        try {
                            usPin.setState(true);
                            Thread.sleep(0, 20);
                            usPin.setState(false);
                            value = (0.50026463999) * ((AnalogInput) sen.sensor).getValue();
                            Thread.sleep(0, 20);
                            return value;

                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        throw new RuntimeException();
                    }

                case LIGHT:
                    return ((AnalogInput) sen.sensor).getValue();

                case COLOR:
                    return (double) ColorType.toInt(getDominantColor(sen));

                default:
                    return 0.0;
            }
        }
    }






    /*
    *************
    ACTUAL THREAD
    *************
     */

    /**
     * The body of the thread. This function continually updates all sensorContainers for which an
     * update is requested.
     * It loops through every sensor in the sensors HashMap and then uses getSensorReading with updateArray
     * to update the SensorContainer.
     *
     * It then waits for the specified interval to allow other functions to run.
     */
    public void run() {
        while (true){
            try {
                for (SensorContainer sen : sensorContainers.values()) {
                    synchronized (this) {
                        sen.filter.update(getSensorReading(sen));
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