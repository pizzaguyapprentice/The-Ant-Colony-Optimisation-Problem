package ant;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;

public class Ant {
    // private HomeNode homeNode;
    private Node position;
    private Node lastPosition;
    private boolean hasFood = false;

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

    void nextAction() throws FileNotFoundException {
        // Distance holder stores full values for the distances of each path e.g 10 , 15 , 20
        // These values will then be used to calculate the visibility metrics for each path
        // Position is a local variable that holds the current node the ant is on
        // Reads from position.getNeighbours() to acquire the paths, its edges and their distances
        ArrayList<Double> distanceHolder = new ArrayList<Double>(0);
		ArrayList<Double> phermoneHolder = new ArrayList<Double>(0);
        for(int i = 0; i < position.getNeighbours().length; i++){
            distanceHolder.add(position.getNeighbour(i).getEdge().getDistance());
			phermoneHolder.add(position.getNeighbour(i).getEdge().getPhermone());
        }

		System.out.println();

        double visibilityTotal = 0;
        ArrayList<Double> visibilityArray = new ArrayList<>();
        // path visibility without phermone influence
        for(int i = 0; i < distanceHolder.size(); i++){
            double heuristic = 1/distanceHolder.get(i);
			heuristic = Math.pow(heuristic, Main.distanceImportance);

			double phermone = phermoneHolder.get(i);
			phermone = Math.pow(phermone, Main.phermoneImportance);

			double visibility = phermone * heuristic;
            visibilityTotal += visibility;
            visibilityArray.add(visibility);

            System.out.println("Visibility: " + visibility);
        }

		System.out.println();

		double probabilityTotal = 0;
		ArrayList<Double> pathProbabilityArray = new ArrayList<>(0);
		for (int i = 0; i < visibilityArray.size(); i++) {
			double visibility = visibilityArray.get(i);
			double probability = visibility/visibilityTotal;

			pathProbabilityArray.add(probability);
			probabilityTotal += probability;
			System.out.println("Path Probability: " + probability);
		}

		System.out.println();

        // Using BigDecimal for precision when rounding the temp
        BigDecimal rounding = BigDecimal.valueOf(probabilityTotal);
        rounding = rounding.setScale(16, RoundingMode.HALF_UP);

        System.out.println("Rounded number: " + rounding);
        
        if(rounding.compareTo(BigDecimal.ONE) == 0){
            System.out.println("Rounded number equals 1");
        }
        else{
            System.out.println("Rounded number does not equal 1");
        }
        // Decision made based on the rolled number and the visibility metrics

		// Generate a random double from 0.0 to 1.0
		double randomNumber = new Random().nextDouble();

        double incrementedDecision = 0;
        System.out.println("The decision: "+ randomNumber);

        for(int i = 0; i < position.getNeighbours().length; i++){
            // testing temp variable
            // double tempcheck = 0.99;
            // decide if the roll is less than or equal to the incremented decision
            incrementedDecision += pathProbabilityArray.get(i);
            if(randomNumber <= incrementedDecision){
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
