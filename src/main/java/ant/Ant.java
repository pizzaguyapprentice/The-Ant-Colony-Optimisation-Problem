package ant;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Ant {
    private Node position;
    private Node lastPosition;
    private boolean collectedFood = false;
	private HashMap<String, Edge> edgesTraversed = new HashMap<>();

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

	public HashMap<String, Edge> getEdgesTraversed(){
        return edgesTraversed;
    }

	public void resetEdgesTraversed(){
		edgesTraversed.clear();
	}

    public boolean nextAction() throws FileNotFoundException {
        // distanceHolder stores full values for the distances of each path e.g 10 , 15 , 20
        // pheromoneHolder stores full values of the total pheromone of each path.
        // These values will then be used to calculate the visibility metrics for each path in the VisibilityArray

        ArrayList<Double> distanceHolder = new ArrayList<Double>(0);
		ArrayList<Double> pheromoneHolder = new ArrayList<Double>(0);
        ArrayList<Double> visibilityArray = new ArrayList<Double>();
        ArrayList<Double> pathProbabilityArray = new ArrayList<Double>(0);
        double visibilityTotal = 0;

		System.out.println();

        setParameters(distanceHolder,pheromoneHolder);

        System.out.println();

        visibilityTotal = setVisibility(distanceHolder, pheromoneHolder, visibilityArray, visibilityTotal);

		System.out.println();

        calculateProbablePaths(visibilityArray, visibilityTotal, pathProbabilityArray);

		System.out.println();

        return chooseNextPath(pathProbabilityArray);

    }

    public void setParameters(ArrayList<Double> distanceHolder, ArrayList<Double> pheromoneHolder){

        
        for(int i = 0; i < position.getNeighbours().length-1; i++){

            if(position.getNeighbour(i).getNode() == lastPosition){
                position.removeNeighbour(i);
            }

            distanceHolder.add(position.getNeighbour(i).getEdge().getDistance());
			pheromoneHolder.add(position.getNeighbour(i).getEdge().getPheromone());
        }

		System.out.println();
        
    }
    public double setVisibility(ArrayList<Double> distanceHolder, ArrayList<Double> pheromoneHolder, ArrayList<Double> visibilityArray, double visibilityTotal){
        
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

        return visibilityTotal;
    }

    public void calculateProbablePaths(ArrayList<Double> visibilityArray, double visibilityTotal, ArrayList<Double> pathProbabilityArray){
        double probabilityTotal = 0;
		for (int i = 0; i < visibilityArray.size(); i++) {
			double visibility = visibilityArray.get(i);
			double probability = visibility/visibilityTotal;

			pathProbabilityArray.add(probability);
			probabilityTotal += probability;
			System.out.println("Path Probability: " + probability);
		}
    }

    public Boolean chooseNextPath(ArrayList<Double> pathProbabilityArray){

        double randomNumber = new Random().nextDouble();

        double incrementedDecision = 0;
        System.out.println("The decision: "+ randomNumber);

        for(int i = 0; i < position.getNeighbours().length; i++){
            incrementedDecision += pathProbabilityArray.get(i);
            if(randomNumber <= incrementedDecision){
                System.out.println("You got it!");
                System.out.println("Current position: " + position.getName());
                lastPosition = position;

                System.out.println("Current index: " + i);
                
                System.out.println("Moving onto: " + position.getNeighbour(i).getNode().getName());
				edgesTraversed.put(position.getNeighbour(i).getEdge().getName(), position.getNeighbour(i).getEdge());
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
