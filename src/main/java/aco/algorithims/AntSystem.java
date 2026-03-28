package aco.algorithims;

import java.io.FileNotFoundException;
import java.util.HashMap;

import aco.Main;
import aco.world.Edge;
import aco.world.World;

public class AntSystem implements AcoAlgorithim{
	HashMap<String, Double> totalPheromoneMap = new HashMap<>();

	@Override
	public AntResult runSingleAnt(Ant ant) throws FileNotFoundException{
		while(!ant.nextAction()){}

		AntResult antResult = ant.getResults();

		for(Edge edge : antResult.edgesTraversed){
			double pheromones = Main.pheromoneRate / antResult.totalDistance;
			if(totalPheromoneMap.get(edge.getName()) != null){
				totalPheromoneMap.put(edge.getName(), totalPheromoneMap.get(edge.getName()) + pheromones);
			}
			else{
				totalPheromoneMap.put(edge.getName(), pheromones);
			}
		}

		return antResult;
	}

	@Override
	public void updatePheromone(World world) {
		world.dissipatePheromone(Main.dissipationRate);

		for(String edgeName : totalPheromoneMap.keySet()){
			world.updateEdgePheromone(edgeName, totalPheromoneMap.get(edgeName));
		}

		totalPheromoneMap.clear();
	}
}
