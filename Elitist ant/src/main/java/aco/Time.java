package aco;

public class Time {
    long start;
    long finish;
    double elapsedtime;
    public long startTime(){
        start = System.nanoTime();
        return start;
    }
    public double elapsedTime(){
        finish = System.nanoTime() - start;
        //divided by 1 bil for seconds
        elapsedtime = (finish)/1000000000.0; 
        System.out.println("Elapsed time: "+ elapsedtime+" seconds");
        return elapsedtime;
    }
    
}
