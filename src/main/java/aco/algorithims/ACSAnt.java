package aco.algorithims;
import java.io.FileNotFoundException;
import java.security.SecureRandom;

import aco.Main;
import aco.world.Node;
import aco.world.Edge;
import aco.world.FoodNode;
import aco.world.HomeNode;

public class ACSAnt extends Ant{
	public ACSAnt(HomeNode homeNode) {
		super(homeNode);
	}

	// @Override
	// public void resetAnt(){
	// 	resetEdgesTraversed();
	// 	solution = position.getName();
	// 	lastPosition = null;
	// 	collectedFood = false;
	// 	distanceTraveled = 0;
	// 	lastEdge = null;
	// }

	public boolean nextAction() throws FileNotFoundException{
		SecureRandom sr = new SecureRandom();
		double exploitationSwitch = sr.nextDouble();
		if(exploitationSwitch < Main.explorationThreashold) {
			Edge neighbours[] = position.getNeighboursExcluding(lastPosition);
			double visibilityArray[] = new double[neighbours.length];
			double pathProbabilityArray[] = new double[neighbours.length];
			double visibilityTotal = 0;

			visibilityTotal = setVisibility(neighbours, visibilityArray);

			calculateProbablePaths(visibilityArray, visibilityTotal, pathProbabilityArray);

			return chooseNextPath(neighbours, pathProbabilityArray);
		}
		else{
			Edge[] possibleNeighbours = position.getNeighboursExcluding(lastPosition);

			Edge edge = possibleNeighbours[sr.nextInt(possibleNeighbours.length)];

			lastPosition = position;

			edgesTraversed.put(edge.getName(), edge);

			if(Main.DEBUG >= 1){
				System.out.println("Current position: " + position.getName());
				System.out.println("Moving onto: " + edge.getOtherNode(position).getName());
			}

			distanceTraveled = distanceTraveled + edge.getDistance();
			position = edge.getOtherNode(position);

			solution = solution + position.getName();

			lastEdge = edge;

			if(position instanceof FoodNode){
				collectedFood = true;
				lastPosition = null;
				solution = solution + " " + position.getName();
			}
			if(position instanceof HomeNode && collectedFood){
				return true;
			}

			return false;
		}
	}
}
