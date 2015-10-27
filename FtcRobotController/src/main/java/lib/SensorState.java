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


    // New version would require only 3 hashmaps.
    private HashMap<sensorType, HardwareMap.DeviceMapping> maps;    // HashMap of DeviceMappings from HardwareMap to grab sensor objects.
    private HashMap<String, Sensor> sensors;                        // Stores Sensor objects (definition at bottom)
    private HashMap<sensorType, Sensor[]> types_inv;                // Allows recovery of all sensors of a certain type.



    // Later, should change to HashMap<sensorType, String[]> rev_types;
    // Theoretically, after transition to Sensor objects, this would be one of two remaining HashMaps.
    // The other would be HashMap<String, Sensor> sensors

    // Other good possible change:
    // Instead of repeating registration functions, let them pass in a sensorType
    // Then use a HashMap that looks like {ULTRASONIC: hmap.analogInput, GYRO: hmap.gyroSensor, ... ... }
    // Call .get() on the corresponding hashmaps in there.
    // Might need to use variable types, as all sensor types will be different. No, actually thats fine.

    // interval determines how long run() waits between updates.
    private int interval;

    public SensorState(HardwareMap hmap, int interval) {
        maps = new HashMap<sensorType, HardwareMap.DeviceMapping>();
        sensors = new HashMap<String, Sensor>();
        types_inv = new HashMap<sensorType, Sensor[]>();

        this.interval = interval;

        // For conversion to single registration function
        maps.put(sensorType.ULTRASONIC, hmap.colorSensor);
        maps.put(sensorType.LIGHT, hmap.colorSensor);
        maps.put(sensorType.GYRO, hmap.colorSensor);
        maps.put(sensorType.COLOR, hmap.colorSensor);
        maps.put(sensorType.ENCODER, hmap.colorSensor);

        // For conversion to using only enums for access.
        types_inv.put(sensorType.ULTRASONIC, new Sensor[0]);
        types_inv.put(sensorType.LIGHT, new Sensor[0]);
        types_inv.put(sensorType.GYRO, new Sensor[0]);
        types_inv.put(sensorType.COLOR, new Sensor[0]);
        types_inv.put(sensorType.ENCODER, new Sensor[0]);
    }

    // For registration in one function
    public synchronized void registerSensor(String name, sensorType type, boolean update, int data_length){
        Object sensor_obj = maps.get(type).get(name);
        Sensor sen;

        if (type == sensorType.COLOR){
            sen = new Sensor(0, new double[5], sensor_obj, update, type);
        }
        else {
            sen = new Sensor(0, new double[0], sensor_obj, update, type);
        }
        sensors.put(name, sen);
        addToRevTypes(sen, type);
    }

    public Sensor[] getSensorsFromType(sensorType type){
        return types_inv.get(type);
    }

    private void addToRevTypes(Sensor sen, sensorType type){
        // Pull out the old list of sensors of this type. Transfer them to a new list, and also add the new sensor.
        Sensor[] old_sensors = types_inv.get(type);
        int old_length = old_sensors.length;
        Sensor[] new_sensors = new Sensor[old_length + 1];

        for (int i = 0; i < old_length; i++){
            new_sensors[i] = old_sensors[i];
        }

        new_sensors[old_length] = sen;
        types_inv.put(type, new_sensors);
    }

    // Sensor writing
    public synchronized void changeUpdateStatus(String name, boolean update){
        sensors.get(name).update = update;
    }

    private void updateArray(String name, double value){
        Sensor sen = sensors.get(name);
        int index = sen.index;
        double[] data = sen.values;

        // Increment index, or loop back around to beginning if at end.
        index = (index + 1) % (data.length);
        data[index] = value;
        sen.values = data;
        sen.index = index;
    }

    private void updateColorSensor(String key){
        // I don't know a good way to do this that avoids the unnecessary data pull without memory leaks or a lot of deallocation.
        Sensor sen = sensors.get(key);
        double[] data = sen.values;
        ColorSensor sen_obj = (ColorSensor) sen.sensor;
        data[0] = sen_obj.alpha();
        data[1] = sen_obj.blue();
        data[2] = sen_obj.green();
        data[3] = sen_obj.red();
        sen.values = data;
    }

    public SensorData getSensorData(String name){
        // The last entry in the returned array is the index of the most recently acquired reading.
        // If the sensor is a colorsensor, the last entry is always 0.
        try {
            Thread.sleep(0, 10);
        } catch (InterruptedException ex){}

        Sensor sen = sensors.get(name);
        synchronized (this) {
            return new SensorData(sen.index, sen.values);
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
                        Sensor sen = sensors.get(key);

                        // only update if an update is requested
                        if (sen.update) {
                            switch (sen.type) {
                                case GYRO:
                                    value = ((GyroSensor) sen.sensor).getRotation();
                                    updateArray(key, value);
                                    break;
                                case ENCODER:
                                    value = ((DcMotor) sen.sensor).getCurrentPosition();
                                    updateArray(key, value);
                                    break;
                                case ULTRASONIC:
                                    value = ((AnalogInput) sen.sensor).getValue();
                                    updateArray(key, value);
                                    break;
                                case COLOR:
                                    updateColorSensor(key);
                                    break;
                                case LIGHT:
                                    value = ((LightSensor) sen.sensor).getLightDetected();
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

class Sensor {
    // Potential container to replace the HashMap mess. Could also use SensorData instead of index + values.
    public int index;
    public double[] values;
    public Object sensor;
    public boolean update;
    public SensorState.sensorType type;

    public Sensor(int index, double[] values, Object sensor, boolean update, SensorState.sensorType type) {
        this.index = index;
        this.values = values;
        this.sensor = sensor;
        this.update = update;
        this.type = type;
    }
}