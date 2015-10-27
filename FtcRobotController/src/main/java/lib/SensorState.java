package lib;


import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LightSensor;

import java.util.HashMap;

public class SensorState implements Runnable{
    // To make this as useful as possible compared to last year, interval should probably be pretty long (~50 - 100 milli)
    // Assuming autonomous lasts for 30 seconds, we only need array sizes of around 300 to 600, and then we never need to block loop.

    // As long as the private methods are ONLY CALLED FROM run(), no synchronization errors should occur.

    // To make run() more readable
    public enum sensorType {
        GYRO, ULTRASONIC, COLOR, LIGHT, ENCODER
    }

    // We could do one hashmap of custom sensor objects, but we'll never use those outside of here, and this is not so inefficient.
    private HashMap<String, Object> sensors;
    private HashMap<String, Boolean> updates;       // Whether or not to update the sensor on each pass.
    private HashMap<String, double[]> sensor_data;  // Arrays of recent sensor data, reading chronologically from index up.
    private HashMap<String, Integer> indices;       // Stores the index of the most recent entry of sensor data.
    private HashMap<String, sensorType> types;      // Stores enums marking each sensor type for appropriate data retrieval
    private HashMap<String, String[]> rev_types;      // Stores sensor_names indexed by type (like in sensorType) but in Strings. Inverse of types.
    // Later, should change to HashMap<sensorType, String[]> rev_types;
    // Theoretically, after transition to Sensor objects, this would be one of two remaining HashMaps.
    // The other would be HashMap<String, Sensor> sensors

    // Other good possible change:
    // Instead of repeating registration functions, let them pass in a sensorType
    // Then use a HashMap that looks like {ULTRASONIC: hmap.analogInput, GYRO: hmap.gyroSensor, ... ... }
    // Call .get() on the corresponding hashmaps in there.
    // Might need to use variable types, as all sensor types will be different.

    private HardwareMap hmap;

    // interval determines how long run() waits between updates.
    private int interval;

    public SensorState(HardwareMap hmap, int interval) {
        sensors = new HashMap<String, Object>();
        updates = new HashMap<String, Boolean>();
        sensor_data = new HashMap<String, double[]>();
        indices = new HashMap<String, Integer>();
        types = new HashMap<String, sensorType>();
        rev_types = new HashMap<String, String[]>();

        rev_types.put("ULTRASONIC", new String[0]);
        rev_types.put("LIGHT", new String[0]);
        rev_types.put("GYRO", new String[0]);
        rev_types.put("COLOR", new String[0]);
        rev_types.put("ENCODER", new String[0]);

        this.hmap = hmap;
        this.interval = interval;
    }

    public String[] getRevTypes(String type){
        return rev_types.get(type);
    }

    private void addToRevTypes(String name, String type){
        // Pull out the old list of sensors of this type. Transfer them to a new list, and also add the new sensor.
        String[] old_sensors = rev_types.get(type);
        int old_length = old_sensors.length;
        String[] new_sensors = new String[old_length + 1];

        for (int i = 0; i < old_length; i++){
            new_sensors[i] = old_sensors[i];
        }
        new_sensors[old_length] = name;
        rev_types.put(type, new_sensors);
    }

    private void registerSensor(String name, boolean update, int data_length){
        updates.put(name, update);
        sensor_data.put(name, new double[data_length]);
        indices.put(name, 0);
    }

    // Sensor writing
    public synchronized void changeUpdateStatus(String name, boolean update){
        updates.put(name, update);
    }

    // Sensor registration
    // Have to be synchronized to avoid conflict with run()
    public synchronized void registerUltrasonic(String name, boolean update, int data_length){
        sensors.put(name, hmap.analogInput.get(name));
        types.put(name, sensorType.ULTRASONIC);
        addToRevTypes(name, "ULTRASONIC");
        registerSensor(name, update, data_length);

    }

    public synchronized void registerGyro(String name, boolean update, int data_length){
        sensors.put(name, hmap.gyroSensor.get(name));
        types.put(name, sensorType.GYRO);
        addToRevTypes(name, "GYRO");
        registerSensor(name, update, data_length);
    }

    public synchronized void registerEncoder(String name, boolean update, int data_length){
        sensors.put(name, hmap.dcMotor.get(name));
        types.put(name, sensorType.ENCODER);
        addToRevTypes(name, "ENCODER");
        registerSensor(name, update, data_length);
    }

    public synchronized void registerLight(String name, boolean update, int data_length){
        sensors.put(name, hmap.lightSensor.get(name));
        types.put(name, sensorType.LIGHT);
        addToRevTypes(name, "LIGHT");
        registerSensor(name, update, data_length);
    }

    // Not storing color values over time.
    public synchronized void registerColor(String name, boolean update){
        sensors.put(name, hmap.colorSensor.get(name));
        types.put(name, sensorType.COLOR);
        addToRevTypes(name, "COLOR");
        updates.put(name, update);
        indices.put(name, 0);
        sensor_data.put(name, new double[5]);
    }

    private void updateArray(String name, double value){
        int index = indices.get(name);
        double[] data = sensor_data.get(name);

        // Increment index, or loop back around to beginning if at end.
        index = (index + 1) % (data.length);
        data[index] = value;
        sensor_data.put(name, data);
        indices.put(name, index);
    }

    private void updateColorSensor(String key){
        // I don't know a good way to do this that avoids the unnecessary data pull without memory leaks or a lot of deallocation.
        double[] data = sensor_data.get(key);
        ColorSensor sen = (ColorSensor) sensors.get(key);
        data[0] = sen.alpha();
        data[1] = sen.blue();
        data[2] = sen.green();
        data[3] = sen.red();
        sensor_data.put(key, data);
    }

    public SensorData getSensorData(String name){
        // The last entry in the returned array is the index of the most recently acquired reading.
        // If the sensor is a colorsensor, the last entry is always 0.
        try {
            Thread.sleep(0, 10);
        } catch (InterruptedException ex){}

        synchronized (this) {
            return new SensorData(indices.get(name), sensor_data.get(name));
        }
    }

    public void run() {
        double value = 0.0;

        while (true){
            try {

                // Can't let any reading happen while updating values
                synchronized (this) {
                    // For every sensor name
                    for (String key : sensors.keySet()) {

                        // only update if an update is requested
                        if (updates.get(key)) {
                            switch (types.get(key)) {
                                case GYRO:
                                    value = ((GyroSensor) sensors.get(key)).getRotation();
                                    updateArray(key, value);
                                    break;
                                case ENCODER:
                                    value = ((DcMotor) sensors.get(key)).getCurrentPosition();
                                    updateArray(key, value);
                                    break;
                                case ULTRASONIC:
                                    value = ((AnalogInput) sensors.get(key)).getValue();
                                    updateArray(key, value);
                                    break;
                                case COLOR:
                                    updateColorSensor(key);
                                    break;
                                case LIGHT:
                                    value = ((LightSensor) sensors.get(key)).getLightDetected();
                                    updateArray(key, value);
                                    break;
                            }
                        }
                    }
                }
                // I need to give getSensorData and the registration functions time to grab the lock.
                Thread.sleep(interval);
            } catch (InterruptedException ex){
                break;
            }
        }
    }
}


class SensorData{
    // values is all the sensor data in chronological order, starting at index and wrapping around.

    public int index;
    public double[] values;

    public SensorData(int index, double[] values) {
        this.index = index;
        this.values = values;
    }
}

//class Sensor {
//    // Potential container to replace the HashMap mess. Could also use SensorData instead of index + values.
//    public int index;
//    public double[] values;
//    public Object sensor;
//    public boolean update;
//    public SensorState.sensorType type;
//
//    public Sensor(int index, double[] values, Object sensor, boolean update, SensorState.sensorType type) {
//        this.index = index;
//        this.values = values;
//        this.sensor = sensor;
//        this.update = update;
//        this.type = type;
//    }
//}