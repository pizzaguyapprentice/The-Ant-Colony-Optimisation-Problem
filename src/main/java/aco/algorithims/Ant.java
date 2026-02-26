package aco.algorithims;
import java.io.FileNotFoundException;
import java.security.SecureRandom;
import java.util.HashMap;

import aco.Main;
import aco.world.Edge;
import aco.world.FoodNode;
import aco.world.HomeNode;
import aco.world.Node;
import aco.world.Path;

public class Ant {
    private Node position;
    private Node lastPosition = null;
    private boolean collectedFood = false;
	public Edge lastEdge = null;
	private HashMap<String, Edge> edgesTraversed = new HashMap<>();
	private String solution = "";

    public String getSolution() {
		return solution;
	}

	public Ant(HomeNode homeNode){
        this.position = homeNode;
		solution = homeNode.getName();
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

	public void resetAnt(){
		resetEdgesTraversed();
		solution = position.getName();
		lastPosition = null;
		collectedFood = false;
	}

    public boolean nextAction() throws FileNotFoundException {
        // distanceHolder stores full values for the distances of each path e.g 10 , 15 , 20
        // pheromoneHolder stores full values of the total pheromone of each path.
        // These values will then be used to calculate the visibility metrics for each path in the VisibilityArray

		Path neighbours[] = position.getNeighboursExcluding(lastPosition);
		double visibilityArray[] = new double[neighbours.length];
		double pathProbabilityArray[] = new double[neighbours.length];
        double visibilityTotal = 0;

        visibilityTotal = setVisibility(neighbours, visibilityArray);

        calculateProbablePaths(visibilityArray, visibilityTotal, pathProbabilityArray);

        return chooseNextPath(neighbours, pathProbabilityArray);

    }

    // Using the formula in the ACO problem to calculate the heuristic and visibility.
    private double setVisibility(Path[] neighbours, double[] visibilityArray){
		double visibilityTotal = 0;

		for(int i = 0; i < neighbours.length; i++){
            double reciprocal = 1/neighbours[i].getEdge().getDistance();
			reciprocal = Math.pow(reciprocal, Main.distanceImportance);

			double pheromone = neighbours[i].getEdge().getPheromone();
			pheromone = Math.pow(pheromone, Main.pheromoneImportance);

			double visibility = pheromone * reciprocal;
            visibilityTotal += visibility;
            visibilityArray[i] = visibility;
        }

        return visibilityTotal;
    }

    // Finally the visibility is turned into a probability, determining how likely each path is to be selected
    private void calculateProbablePaths(double[] visibilityArray, double visibilityTotal, double[] pathProbabilityArray){
		for (int i = 0; i < visibilityArray.length; i++) {
			double visibility = visibilityArray[i];
			double probability = visibility/visibilityTotal;

			pathProbabilityArray[i] = probability;
		}
    }

    // Generate random number, check where it lands and traverse it.
	private Boolean chooseNextPath(Path[] possibleNeighbours, double[] pathProbabilityArray){
		double randomNumber = new SecureRandom().nextDouble();
		double incrementedDecision = 0;

		for(int i = 0; i < possibleNeighbours.length; i++){
			incrementedDecision += pathProbabilityArray[i];
			if(randomNumber < incrementedDecision){
				lastPosition = position;

				edgesTraversed.put(possibleNeighbours[i].getEdge().getName(), possibleNeighbours[i].getEdge());

				if(Main.DEBUG >= 1){
					System.out.println("Current position: " + position.getName());
					System.out.println("Moving onto: " + possibleNeighbours[i].getNode().getName());
				}

				lastEdge = possibleNeighbours[i].getEdge();
				position = possibleNeighbours[i].getNode();

				solution = solution + position.getName();

				if(position instanceof FoodNode){
					collectedFood = true;
					lastPosition = null;
					solution = solution + " " + position.getName();
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
