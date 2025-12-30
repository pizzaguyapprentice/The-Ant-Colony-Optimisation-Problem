package ant;
import java.io.FileNotFoundException;
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

        // System.out.println();

        visibilityTotal = setVisibility(distanceHolder, pheromoneHolder, visibilityArray, visibilityTotal);

		// System.out.println();

        calculateProbablePaths(visibilityArray, visibilityTotal, pathProbabilityArray);

		// System.out.println();

        return chooseNextPath(pathProbabilityArray);

    }

    public void setParameters(ArrayList<Double> distanceHolder, ArrayList<Double> pheromoneHolder){
        for(int i = 0; i < position.getNeighbours().length; i++){

            if(position.getNeighbour(i).getNode() == lastPosition){
                // position.removeNeighbour(i);
				// System.out.println("SKIPPED NEIGHBOUR " + position.getNeighbour(i).getNode().getName());
				continue;
            }

            distanceHolder.add(position.getNeighbour(i).getEdge().getDistance());
			pheromoneHolder.add(position.getNeighbour(i).getEdge().getPheromone());
        }
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
            // System.out.printf("Visibility: %.20f\n", visibility);
        }

        return visibilityTotal;
    }

    public void calculateProbablePaths(ArrayList<Double> visibilityArray, double visibilityTotal, ArrayList<Double> pathProbabilityArray){
		for (int i = 0; i < visibilityArray.size(); i++) {
			double visibility = visibilityArray.get(i);
			double probability = visibility/visibilityTotal;

			pathProbabilityArray.add(probability);
			// System.out.printf("Path Probability: %.20f\n", probability);
		}
    }

    public Boolean chooseNextPath(ArrayList<Double> pathProbabilityArray){

        double randomNumber = new Random().nextDouble();

        double incrementedDecision = 0;
        // System.out.println("The decision: "+ randomNumber);

		Path[] allNeighbours = position.getNeighbours();
		ArrayList<Path> possibleNeighboursList = new ArrayList<>(0);
		for (int i = 0; i < allNeighbours.length; i++) {
			if (allNeighbours[i].getNode() == lastPosition) {
				continue;
			}
			possibleNeighboursList.add(allNeighbours[i]);
		}

		Path[] possibleNeighbours = possibleNeighboursList.toArray(new Path[0]);

        for(int i = 0; i < possibleNeighbours.length; i++){
            incrementedDecision += pathProbabilityArray.get(i);
            if(randomNumber <= incrementedDecision){
                // System.out.println("Chose Path " + i);
                System.out.println("Current position: " + position.getName());
                lastPosition = position;

                System.out.println("Moving onto: " + possibleNeighbours[i].getNode().getName());
				edgesTraversed.put(possibleNeighbours[i].getEdge().getName(), possibleNeighbours[i].getEdge());
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
        // System.out.println("Colected Food: " + collectedFood);
		return false;

    }
}
