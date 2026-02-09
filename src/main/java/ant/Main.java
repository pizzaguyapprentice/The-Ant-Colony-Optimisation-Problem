package ant;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Main{
	// Debug Parameter For Extra Printing
	public static final int DEBUG = 0;

	// Global Parameters For ACO Problem
	public static final double distanceImportance = 0.1;
	public static final double pheromoneImportance = 1.4;
	public static final double dissipationRate = 0.2;
	public static final double pheromoneRate = 4;

	// Number Of Ants Per Generation
	public static final int NUM_ANTS = 100;
	public static final int GENS = 1000;

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException{
		World world = new World();
		Ant ant = new Ant(world.getStartNode());
		SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss-SSS");
		Date date = new Date(System.currentTimeMillis());
		File file = new File("src/main/resources/results/output-" + sdf.format(date) + ".txt");
		File folder = new File("src/main/resources/results");
		folder.mkdirs();
		file.createNewFile();
		PrintWriter pw = new PrintWriter(file);
		
		if(Main.DEBUG >= 1){
			world.printWorld();
		}

		// START measuring time in seconds
		Time time = new Time();
		time.startTime();

		for(int i = 1; i <= GENS; i++){
			if(Main.DEBUG >= 1){
				System.out.printf("\nStarted Generation %d\n", i);
			}

			HashMap<String, Double> totalPheromoneMap = new HashMap<>();

			for(int j = 1; j <= NUM_ANTS; j++){
				if(Main.DEBUG >= 1){
					System.out.printf("\nGen %d: Ant %d\n", i, j);
				}

				double totalDistance = 0;
				while(!ant.nextAction()){
					totalDistance = totalDistance + ant.lastEdge.getDistance();
				}
				totalDistance = totalDistance + ant.lastEdge.getDistance();
				
				if(Main.DEBUG >= 1){
					System.out.printf("Total Distance: %f\n", totalDistance);
				}
				

				HashMap<String, Edge> edgesTraversed = ant.getEdgesTraversed();

				for(String edgeName : edgesTraversed.keySet()){
					double pheromones = pheromoneRate / totalDistance;
					if(totalPheromoneMap.get(edgeName) != null){
						totalPheromoneMap.put(edgeName, totalPheromoneMap.get(edgeName) + pheromones);
					}
					else{
						totalPheromoneMap.put(edgeName, pheromones);
					}
				}
				ant.setLastPosition(null);
				ant.resetEdgesTraversed();
				ant.setCollectedFood(false);
			}

			world.dissipatePheromone(dissipationRate);


			
			for(String edgeName : totalPheromoneMap.keySet()){
				world.updateEdgePheromone(edgeName, totalPheromoneMap.get(edgeName));
			}

			world.outputEdgePheromone(pw, i);

			if(Main.DEBUG >= 1){
				world.printEdgePheromone();
				System.out.printf("\nFinished Generation %d\n", i);
			}
		}
		time.elapsedTime();
		pw.close();
	}
}
