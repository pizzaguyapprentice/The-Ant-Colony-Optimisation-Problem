package ant;
import java.io.IOException;

public class Main{
	public static final int distanceImportance = 2;
	public static final int pheromoneImportance = 2;
	public static final double dissipationRate = 0.2;
	public static final double pheromoneRate = 0.4;
	public static void main(String[] args) throws IOException{
		Ant ant = new Ant(World.createWorld());

		for(int i = 0; i < 10; i++){
			while (!ant.nextAction());
			System.out.printf("Finished Iteration %d\n", i);
			Edge[] edgesTraversed = ant.getEdgesTraversed();
			for(Edge edge : edgesTraversed) {
				edge.addPheromone(pheromoneRate);
				System.out.println("Edge Traversed: " + edge.getName());
			}
			World.dissipatePheromone(dissipationRate);
		}
	}
}
