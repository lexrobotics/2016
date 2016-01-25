package lib;

/**
 * Created by lhscompsci on 12/7/15.
 */
public class Filter {
    public double[] data;
    private double avg;

    private int size;
    private int index;  // always points to the location of the most recent value
    private int stdevs;
    private int filter_length;

    // Describes whether filter_length values have been collected yet, so the average will be meaningful.
    private boolean filled;

    public Filter(int size){
        this(size, -1, size);
    }

    public Filter(int size, int stdevs){
        this(size, stdevs, size);
    }

    public Filter(int size, int stdevs, int filter_length){
        assert(filter_length <= size);

        data = new double[size];
        filled = false;
        index = 0;
        avg = 0;

        this.stdevs = stdevs;
        this.filter_length = filter_length;
        this.size = size;
    }

    public double findDeviation(double[] nums) {
        double squareSum = 0;
        for (int i = 0; i < nums.length; i++) {
            squareSum += Math.pow(nums[i] - avg, 2);
        }
        return Math.sqrt((squareSum) / (nums.length - 1));
    }

    private void addToAverage(double val){
        int oldest_index = (index + 1 + size - filter_length) % size;

        avg *= filter_length;
        avg -= data[oldest_index];
        avg += val;
        avg /= filter_length;

        index++;
        index %= size;
        data[index] = val;

        if (index == filter_length - 1) {
            filled = true;
        }
    }

    public void update(double val){

//        Robot.tel.addData("ata: " + val + "  avg: " + avg + "  stdevs: " + stdevs, "");
        if (stdevs >-1) {

            if (Math.abs(val - avg) < findDeviation(data) * stdevs) {
                addToAverage(val);
            }
        }
        else {
            addToAverage(val);
        }
    }

    public void changeFilter_length(int fl){
        if (fl == filter_length)
            return;

        assert(fl <= size);

        avg *= filter_length;
        int sign, from, to;

        // If the new length is more than the previous, we need to add new values. Otherwise, we need to subtract.
        // Will change from "from" to "to" inclusive
        if (fl > filter_length){
            sign = 1;
            from = (index - fl + 1 + size) % size;
            to = (index - filter_length + size) % size; //Dimitri was here
        }
        else {
            sign = -1;
            from = (index - filter_length + 1 + size) % size;
            to = (index - fl + size) % size;
        }

        for (int i = from; i != to; i++){
            if (i >= size)
                i %= size;
            avg += data[i] * sign;
        }
        avg += data[to] * sign;

        filter_length = fl;
        avg /= filter_length;
    }

    public boolean isFilled(){
        return filled;
    }

    public double getAvg(){
//        Robot.tel.addData("From filter", avg);
        return avg; }

    public double[] getData(){ return data; }

    public double getLastValue(){
        return data[(index - 1 + size) % size];
    }

    public Filter clone(){
        Filter new_filter = new Filter(size);
        new_filter.avg = avg;
        new_filter.size = size;
        new_filter.index = index;
        new_filter.stdevs = stdevs;
        new_filter.filled = filled;

        new_filter.data = data.clone();
        return new_filter;
    }
}


//Dimitri was here