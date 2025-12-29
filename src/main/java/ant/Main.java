package ant;
import java.io.IOException;
import java.util.HashMap;

public class Main{
	public static final int distanceImportance = 2;
	public static final int pheromoneImportance = 2;
	public static final double dissipationRate = 0.2;
	public static final double pheromoneRate = 2;
	public static final int GENS = 75;
	public static void main(String[] args) throws IOException{
		Ant ant = new Ant(World.createWorld());

		for(int i = 0; i < GENS; i++){
			while (!ant.nextAction());
			System.out.printf("Finished Iteration %d\n", i);
			HashMap<String, Edge> edgesTraversed = ant.getEdgesTraversed();
			for(String edgeName : edgesTraversed.keySet()){
				Edge edge = edgesTraversed.get(edgeName);
				double totalDistance = 0;
				for(String secondEdgeName : edgesTraversed.keySet()){
					totalDistance =+ edgesTraversed.get(secondEdgeName).getDistance();
				}
				double pheromones = pheromoneRate / totalDistance;
				edge.addPheromone(pheromones);
				System.out.println("Edge Traversed: " + edge.getName());
			}
			ant.resetEdgesTraversed();
			ant.setCollectedFood(false);
			World.dissipatePheromone(dissipationRate);
		}
	}
}
