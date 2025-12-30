package ant;
import java.io.IOException;
import java.util.HashMap;

public class Main{
	public static final int distanceImportance = 2;
	public static final double pheromoneImportance = 3;
	public static final double dissipationRate = 0.90;
	public static final double pheromoneRate = 3;
	public static final int GENS = 1000;
	public static void main(String[] args) throws IOException{
		Ant ant = new Ant(World.createWorld());

		for(int i = 1; i <= GENS; i++){
			while (!ant.nextAction());
			System.out.printf("Finished Iteration %d\n", i);
			HashMap<String, Edge> edgesTraversed = ant.getEdgesTraversed();
			
			World.dissipatePheromone(dissipationRate);

			double totalDistance = 0;
			for(String secondEdgeName : edgesTraversed.keySet()){
				totalDistance = totalDistance + edgesTraversed.get(secondEdgeName).getDistance();
			}

			for(String edgeName : edgesTraversed.keySet()){
				Edge edge = edgesTraversed.get(edgeName);
				double pheromones = pheromoneRate / totalDistance;
				edge.addPheromone(pheromones);
				System.out.printf("Edge Traversed: %s  Pheromones Deposited: %.5f\n", edge.getName(), pheromones);
			}

			World.printEdgePheromone();

			ant.resetEdgesTraversed();
			ant.setCollectedFood(false);
		}
	}
}
