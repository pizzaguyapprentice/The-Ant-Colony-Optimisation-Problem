package ant;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class Main{
	public static final double distanceImportance = 0.5;
	public static final double pheromoneImportance = 2;
	public static final double dissipationRate = 0.7;
	public static final double pheromoneRate = 3;
	// Number Of Ants Per Generation
	public static final int NUM_ANTS = 100;
	public static final int GENS = 50;
	public static void main(String[] args) throws IOException{
		Ant ant = new Ant(World.createWorld());

		File file = new File("src/main/resources/" + System.currentTimeMillis() + ".txt");
		file.createNewFile();
		PrintWriter pw = new PrintWriter(file);

		for(int i = 1; i <= GENS; i++){
			HashMap<String, Double> totalPheromoneMap = new HashMap<String, Double>();

			for(int j = 0; j < NUM_ANTS; j++){
				while(!ant.nextAction());

				HashMap<String, Edge> edgesTraversed = ant.getEdgesTraversed();

				double totalDistance = 0;
				for(Edge secondEdgeName : ant.edgesTraversed2){
					totalDistance = totalDistance + secondEdgeName.getDistance();
				}

				System.out.println(totalDistance);

				for(String edgeName : edgesTraversed.keySet()){
					double pheromones = pheromoneRate / totalDistance;
					if(totalPheromoneMap.get(edgeName) != null){
						totalPheromoneMap.put(edgeName, totalPheromoneMap.get(edgeName) + pheromones);
					}
					else{
						totalPheromoneMap.put(edgeName, pheromones);
					}
					// System.out.printf("Edge Traversed: %s  Pheromones Deposited: %.5f\n", edgeName, pheromones);
				}

				ant.edgesTraversed2.clear();
				ant.setLastPosition(null);
				ant.resetEdgesTraversed();
				ant.setCollectedFood(false);
			}
			System.out.printf("Finished Iteration %d\n", i);
			
			World.dissipatePheromone(dissipationRate);

			for(String edgeName : totalPheromoneMap.keySet()){
				World.updateEdgePheromone(edgeName, totalPheromoneMap.get(edgeName));
			}

			World.printEdgePheromone();

			pw.println("Gen " + i);
			World.outputEdgePheromone(pw);

			
			// double totalDistance = 0;
			// for(String secondEdgeName : edgesTraversed.keySet()){
			// 	totalDistance = totalDistance + edgesTraversed.get(secondEdgeName).getDistance();
			// }

			// for(Edge edgeName : ant.edgesTraversed2){
			// 	double pheromones = pheromoneRate / totalDistance;
			// 	edgeName.addPheromone(pheromones);
			// 	System.out.printf("Edge Traversed: %s  Pheromones Deposited: %.5f\n", edgeName.getName(), pheromones);
			// }
		}
		pw.close();
	}
}
