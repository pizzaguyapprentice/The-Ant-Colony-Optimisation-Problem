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
        //Generate a random double from 0.0 to 1.0
        Random random = new Random();
        double decision = random.nextDouble();

        //Here we will weigh out the visibility metric between all possible routes.

        //View how many neighbours are available to travel to

        //Create visibility for each available route, for now using the distance
        for(int i = 0; i < position.getNeighbours().length; i++){
            visibilityMetrics.add(i, (double)position.getNeighbour(i).getEdge().getDistance()); 
            System.out.println("Visibility metric: "+visibilityMetrics.get(i));
        }
        
        //Calculate the probability so that all visibilityMetrics == 1

        double temp = 0;
        for(int i = 0; i < position.getNeighbours().length; i++){
            temp += visibilityMetrics.get(i);
        }
        //Using BigDecimal for precision when rounding the temp
        BigDecimal rounding = BigDecimal.valueOf(temp);
        rounding = rounding.setScale(15, RoundingMode.HALF_UP);

        if(rounding == BigDecimal.valueOf(1)){
            System.out.println("Success, all visibilityMetrics are equal to 1 ");
        }
        else{
            System.out.println("Failure, all visibilityMetrics are NOT equal to 1 ");
        }

        //

    }


    

    

}
