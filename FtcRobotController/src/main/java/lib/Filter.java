package lib;

/**
 * Created by lhscompsci on 12/7/15.
 */
public class Filter {
    public double[] data;
    private double avg;

    private int size;
    private int index;
    private int stdevs;


    private boolean filled;


    public Filter(int size){
        data = new double[size];
        filled = false;
        index = 0;
        this.stdevs = -1;

        this.size = size;
    }

    public Filter(int size, int stdevs){
        data = new double[size];
        filled = false;
        index = 0;
        this.stdevs = stdevs;

        this.size = size;
    }

    public double findDeviation(double[] nums) {
        double squareSum = 0;
        for (int i = 0; i < nums.length; i++) {
            squareSum += Math.pow(nums[i] - avg, 2);
        }
        return Math.sqrt((squareSum) / (nums.length - 1));
    }
    public void update(double val){
        if (stdevs >-1) {

            if (Math.abs(val - avg) < findDeviation(data) * stdevs) {
                data[index] = val;
                if (index == size - 1) {
                    filled = true;
                }

                index++;
                index %= size;

                avg *= size;
                avg -= data[index];
                avg += val;
                avg /= size;
            }
        }
        else {
            data[index] = val;
            if (index == size - 1) {
                filled = true;
            }

            index++;
            index %= size;

            avg *= size;
            avg -= data[index];
            avg += val;
            avg /= size;
        }
    }

    public boolean isFilled(){
        return filled;
    }

    public double getAvg(){
        return avg;
    }
}
