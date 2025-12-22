package ant;
import java.io.FileNotFoundException;
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

    void nextAction() throws FileNotFoundException {
        // Distance holder stores full values for the distances of each path e.g 10 , 15 , 20
        // These values will then be used to calculate the visibility metrics for each path
        // Position is a local variable that holds the current node the ant is on
        // Reads from position.getNeighbours() to acquire the paths, its edges and their distances
        ArrayList<Double> distanceHolder = new ArrayList<Double>();
        for(int i = 0; i < position.getNeighbours().length; i++){
            distanceHolder.add(i, (double)position.getNeighbour(i).getEdge().getDistance());
        }

        // Generate a random double from 0.0 to 1.0
        Random random = new Random();
        double rollForDecision = random.nextDouble();

        // Calculate visibility metric based on distance and pheromone levels
        // Phermone not yet implemented
        for(int i = 0; i < position.getNeighbours().length; i++){
            setVisibilityMetrics(i);
            //System.out.println("Visibility metric: "+visibilityMetrics.get(i));
        }

        // Calculate the probability so that all visibilityMetrics == 1
        double temptotal = 0;
        for(int i = 0; i < position.getNeighbours().length; i++){
            //temp += visibilityMetrics.get(i);
            temptotal += distanceHolder.get(i);
            System.out.println("Temporary distance: "+distanceHolder.get(i));
        }

        double totaldividedvisibility = 0.0;
        double tempdividedvisibility = 0.0;
        

        // TEMPORARY ARRAYLIST FOR THE ACTUAL DIVIDED DISTANCES + VISIBILITY METRICS

        ArrayList<Double> tempdividedvisibilityarray = new ArrayList<>();
        
        // path visibility without phermone influence
        for(int i = 0; i < distanceHolder.size(); i++){
            tempdividedvisibility = distanceHolder.get(i)/temptotal;
            totaldividedvisibility += tempdividedvisibility;
            tempdividedvisibilityarray.add(tempdividedvisibility);

            System.out.println("Actual ratio visibility without phermone: "+ tempdividedvisibility);
        }

        // Using BigDecimal for precision when rounding the temp
        BigDecimal rounding = BigDecimal.valueOf(totaldividedvisibility);
        rounding = rounding.setScale(16, RoundingMode.HALF_UP);

        System.out.println("Rounded number:" + rounding);
        
        if(rounding.compareTo(BigDecimal.ONE) == 0){
            System.out.println("Rounded number equals 1");
        }
        else{
            System.out.println("Rounded number does not equal 1");
        }
        // Decision made based on the rolled number and the visibility metrics
        Double incrementedDecision = 0.00;


        
        System.out.println("The decision: "+ rollForDecision);

        int indexOfNextPosition;
        for(int i = 0; i < position.getNeighbours().length; i++){
            // testing temp variable
            //double tempcheck = 0.99;
            // decide if the roll is less than or equal to the incremented decision
            incrementedDecision += tempdividedvisibilityarray.get(i);
            if(rollForDecision <= incrementedDecision){
                System.out.println("You got it!");
                System.out.println("Current position: " + position.getName());
                lastPosition = position;

                System.out.println("Current index: " + i);
                
                System.out.println("Moving onto: " + position.getNeighbour(i).getNode().getName());
                position = position.getNeighbour(i).getNode();
                
                break;
            }
            else{
                System.out.println("You did NOT get it...");
            }

        }


    }

}
