package ant;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;

public class Ant {
    private HomeNode homeNode;
    private Node position;
    private Node lastPosition;
    private boolean hasFood = false;

    private ArrayList<Double> visibilityMetrics = new ArrayList<>();

    public Ant(HomeNode homeNode){
        this.position = homeNode;
    }


    //temporary dogshit code
    void setVisibilityMetrics(int i){
        visibilityMetrics.add(i, (double)position.getNeighbour(i).getEdge().getDistance()); 
    }

    Node getPosition(){
        return position;
    }
    void setPosition(Node position){
      this.position = position;
    }

    Node getLastPosition(){
        return lastPosition;
    }

    void setLastPosition(Node lastPosition){
       this.lastPosition = lastPosition; 
    }

    boolean hasFood(){
        if(!hasFood){
            return false;
        }
        else{
            return true;
        }
    }

    void setHasFood(boolean hasFood){
      this.hasFood = hasFood;
    }

    void nextAction(){

        ArrayList<Double> distancestemp = new ArrayList<Double>();
        distancestemp.add(0,10.0);
        distancestemp.add(0,7.0);
        distancestemp.add(0,12.0);


        //Generate a random double from 0.0 to 1.0
        Random random = new Random();
        double decision = random.nextDouble();

        //Here we will weigh out the visibility metric between all possible routes.

        //View how many neighbours are available to travel to

        //Create visibility for each available route, for now using the distance
        for(int i = 0; i < position.getNeighbours().length; i++){
            setVisibilityMetrics(i);
            //System.out.println("Visibility metric: "+visibilityMetrics.get(i));
        }

        //Calculate the probability so that all visibilityMetrics == 1



        double temptotal = 0;
        for(int i = 0; i < position.getNeighbours().length; i++){
            //temp += visibilityMetrics.get(i);
            temptotal += distancestemp.get(i);
            System.out.println("Temporary distance: "+distancestemp.get(i));
        }

        double totaldividedvisibility = 0.0;
        double tempdividedvisibility = 0.0;
        for(int i = 0; i < distancestemp.size(); i++){
            tempdividedvisibility = distancestemp.get(i)/temptotal;
            totaldividedvisibility += tempdividedvisibility;
            System.out.println("Actual ratio visibility without phermone: "+tempdividedvisibility);
        }

        //Using BigDecimal for precision when rounding the temp
        BigDecimal rounding = BigDecimal.valueOf(totaldividedvisibility);
        rounding = rounding.setScale(16, RoundingMode.HALF_UP);

        System.out.println("Rounded number:" + rounding);
        
        if(rounding.compareTo(BigDecimal.ONE) == 0){
            System.out.println("Rounded number equals 1");
        }
        else{
            System.out.println("Rounded number does not equal 1");
        }


    }

}
