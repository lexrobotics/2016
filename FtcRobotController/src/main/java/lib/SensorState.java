package lib;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LightSensor;

import java.util.HashMap;

public class SensorState implements Runnable{
    private enum sensorType {
        GYRO, ULTRASONIC, COLOR, LIGHT, ENCODER
    }

    private HashMap<String, Object> sensors;
    private HashMap<String, Boolean> updates;       // Whether or not to update the sensor on each pass.
    private HashMap<String, double[]> sensor_data;  // Arrays of recent sensor data, reading chronologically from index up.
    private HashMap<String, Integer> indices;       // Stores the index of the most recent entry of sensor data.
    private HashMap<String, sensorType> types;      // Stores enums marking each sensor type for appropriate data retrieval
    private HardwareMap hmap;

    public SensorState(HardwareMap hmap) {
        sensors = new HashMap<String, Object>();
        updates = new HashMap<String, Boolean>();
        sensor_data = new HashMap<String, double[]>();
        indices = new HashMap<String, Integer>();
        types = new HashMap<String, sensorType>();
        this.hmap = hmap;
    }

    // Sensor registration
    public void registerUltrasonic(String name, boolean update, int data_length){
        sensors.put(name, hmap.analogInput.get(name));
        types.put(name, sensorType.ULTRASONIC);
        registerSensor(name, update, data_length);
    }

    public void registerGyro(String name, boolean update, int data_length){
        sensors.put(name, hmap.gyroSensor.get(name));
        types.put(name, sensorType.GYRO);
        registerSensor(name, update, data_length);
    }

    public void registerEncoder(String name, boolean update, int data_length){
        sensors.put(name, hmap.dcMotor.get(name));
        types.put(name, sensorType.ENCODER);
        registerSensor(name, update, data_length);
    }

    public void registerLight(String name, boolean update, int data_length){
        sensors.put(name, hmap.lightSensor.get(name));
        types.put(name, sensorType.LIGHT);
        registerSensor(name, update, data_length);
    }

    // Not storing color values over time.
    public void registerColor(String name, boolean update){
        sensors.put(name, hmap.colorSensor.get(name));
        types.put(name, sensorType.COLOR);
        updates.put(name, update);
        indices.put(name, 0);
        sensor_data.put(name, new double[5]);
    }

    public void registerSensor(String name, boolean update, int data_length){
        updates.put(name, update);
        // Leave space for the recent index, which must be returned as well from getSensorData()
        sensor_data.put(name, new double[data_length + 1]);
        indices.put(name, 0);
    }

    // Sensor writing
    public void changeUpdateStatus(String name, boolean update){
        updates.put(name, update);
    }

    private void updateArray(String name, double value){
        int index = indices.get(name);

        double[] data = sensor_data.get(name);

        // Loop back around to beginning.
        index = (index + 1)% (data.length - 1);
        data[index] = value;
        indices.put(name, index);
    }

    public void updateColorSensor(String key){
        double[] data = sensor_data.get(key);
        ColorSensor sen = (ColorSensor) sensors.get(key);
        data[0] = sen.alpha();
        data[1] = sen.blue();
        data[2] = sen.green();
        data[3] = sen.red();
        sensor_data.put(key, data);
    }

    public double[] getSensorData(String name){
        // The last entry in the returned array is the index of the most recently acquired reading.
        // If the sensor is a colorsensor, the last entry is always 0.
        synchronized(this){
            double[] data = sensor_data.get(name);
            data[data.length - 1] = indices.get(name);
            return sensor_data.get(name);
        }
    }

    // Might be more efficient to synchronize on this instead.
    public synchronized void run() {
        double value = 0.0;

        while (true){
            try {

                synchronized (this) {
                    // For every sensor name
                    for (String key : sensors.keySet()) {

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
                // I need to give getSensorData time to grab the lock.
                Thread.sleep(30);
            } catch (InterruptedException ex){
                break;
            }
        }
    }
}
