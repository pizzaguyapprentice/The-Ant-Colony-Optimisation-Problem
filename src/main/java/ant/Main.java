package ant;
import java.io.IOException;

public class Main{
	public static final int distanceImportance = 2;
	public static final int pheromoneImportance = 2;
	public static final double dissipationRate = 0.2;
	public static final double pheromoneRate = 2;
	public static final int GENS = 1;
	public static void main(String[] args) throws IOException{
		Ant ant = new Ant(World.createWorld());

		for(int i = 0; i < GENS; i++){
			while (!ant.nextAction());
			System.out.printf("Finished Iteration %d\n", i);
			Edge[] edgesTraversed = ant.getEdgesTraversed();
			for(Edge edge : edgesTraversed){
				double totalDistance = 0;
				for(Edge edge2 : edgesTraversed){
					totalDistance =+ edge2.getDistance();
				}
				double pheromones = pheromoneRate / totalDistance;
				edge.addPheromone(pheromones);
				System.out.println("Edge Traversed: " + edge.getName());
			}
			World.dissipatePheromone(dissipationRate);
		}
	}
}
