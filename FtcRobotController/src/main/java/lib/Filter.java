package lib;

/**
 * Created by lhscompsci on 12/7/15.
 */
public class Filter {
    private double[] data;
    private double avg;

    private int size;
    private int index;

    private boolean filled;

    public Filter(int size){
        data = new double[size];
        filled = false;
        index = 0;

        this.size = size;
    }

    public void update(double val){
        data[index] = val;
        if (index == size - 1){
            filled = true;
        }

        index ++;
        index %= size;

        avg *= size;
        avg -= data[index];
        avg += val;
        avg /= size;
    }

    public boolean isFilled(){
        return filled;
    }

    public double getAvg(){
        return avg;
    }
}
