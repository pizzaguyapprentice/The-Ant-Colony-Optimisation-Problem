package ant;
import java.io.FileNotFoundException;
import java.security.SecureRandom;
import java.util.HashMap;

public class Ant {
    private Node position;
    private Node lastPosition = null;
    private boolean collectedFood = false;
	public Edge lastEdge = null;
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

		Path neighbours[] = position.getNeighboursExcluding(lastPosition);
		double visibilityArray[] = new double[neighbours.length];
		double pathProbabilityArray[] = new double[neighbours.length];
        double visibilityTotal = 0;

        visibilityTotal = setVisibility(neighbours, visibilityArray, visibilityTotal);

        calculateProbablePaths(visibilityArray, visibilityTotal, pathProbabilityArray);

        return chooseNextPath(neighbours, pathProbabilityArray);

    }

    // Using the formula in the ACO problem to calculate the heuristic and visibility.
    public double setVisibility(Path[] neighbours, double[] visibilityArray, double visibilityTotal){
        
        for(int i = 0; i < neighbours.length; i++){
            double reciprocal = 1/neighbours[i].getEdge().getDistance();
			reciprocal = Math.pow(reciprocal, Main.distanceImportance);

			double pheromone = neighbours[i].getEdge().getPheromone();
			pheromone = Math.pow(pheromone, Main.pheromoneImportance);

			double visibility = pheromone * reciprocal;
            visibilityTotal += visibility;
            visibilityArray[i] = visibility;
            // System.out.printf("Visibility: %.20f\n", visibility);
        }

        return visibilityTotal;
    }

    // Finally the visibility is turned into a probability, determining how likely each path is to be selected
    public void calculateProbablePaths(double[] visibilityArray, double visibilityTotal, double[] pathProbabilityArray){
		for (int i = 0; i < visibilityArray.length; i++) {
			double visibility = visibilityArray[i];
			double probability = visibility/visibilityTotal;

			pathProbabilityArray[i] = probability;
			// System.out.printf("Path Probability: %.20f\n", probability);
		}
    }

    // Generate random number, check where it lands and traverse it.
    public Boolean chooseNextPath(Path[] possibleNeighbours, double[] pathProbabilityArray){
        double randomNumber = new SecureRandom().nextDouble();

        double incrementedDecision = 0;
        // System.out.println("The decision: "+ randomNumber);

        for(int i = 0; i < possibleNeighbours.length; i++){
            incrementedDecision += pathProbabilityArray[i];
            if(randomNumber <= incrementedDecision){
                System.out.println("Current position: " + position.getName());
                lastPosition = position;

                System.out.println("Moving onto: " + possibleNeighbours[i].getNode().getName());
				edgesTraversed.put(possibleNeighbours[i].getEdge().getName(), possibleNeighbours[i].getEdge());

				lastEdge = possibleNeighbours[i].getEdge();
                position = possibleNeighbours[i].getNode();
				if(position.isFood()){
					collectedFood = true;
					lastPosition = null;
				}
				if(position instanceof HomeNode && collectedFood){
					return true;
				}
				break;
            }
        }
		return false;
    }
}
