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
    private boolean collectedFood = false;
	private ArrayList<Edge> edgesTraversed = new ArrayList<>(0);

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

    boolean collectedFood(){
        if(!collectedFood){
            return false;
        }
        else{
            return true;
        }
    }

    void setCollectedFood(boolean collectedFood){
      this.collectedFood = collectedFood;
    }

	public Edge[] getEdgesTraversed(){
        return edgesTraversed.toArray(new Edge[0]);
    }

    boolean nextAction() throws FileNotFoundException {
        // Distance holder stores full values for the distances of each path e.g 10 , 15 , 20
        // These values will then be used to calculate the visibility metrics for each path
        // Position is a local variable that holds the current node the ant is on
        // Reads from position.getNeighbours() to acquire the paths, its edges and their distances
        ArrayList<Double> distanceHolder = new ArrayList<Double>(0);
		ArrayList<Double> pheromoneHolder = new ArrayList<Double>(0);
        for(int i = 0; i < position.getNeighbours().length; i++){
            distanceHolder.add(position.getNeighbour(i).getEdge().getDistance());
			pheromoneHolder.add(position.getNeighbour(i).getEdge().getPheromone());
        }

		System.out.println();

        double visibilityTotal = 0;
        ArrayList<Double> visibilityArray = new ArrayList<>();
        // path visibility without pheromone influence
        for(int i = 0; i < distanceHolder.size(); i++){
            double heuristic = 1/distanceHolder.get(i);
			heuristic = Math.pow(heuristic, Main.distanceImportance);

			double pheromone = pheromoneHolder.get(i);
			pheromone = Math.pow(pheromone, Main.pheromoneImportance);

			double visibility = pheromone * heuristic;
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
				edgesTraversed.add(position.getNeighbour(i).getEdge());
                position = position.getNeighbour(i).getNode();
				if(position.isFood()){
					collectedFood = true;
				}
				if(position instanceof HomeNode && collectedFood){
					return true;
				}
                
                break;
            }
            else{
                System.out.println("You did NOT get it...");
            }
        }
		System.out.println("Colected Food: " + collectedFood);
		return false;
    }
}
