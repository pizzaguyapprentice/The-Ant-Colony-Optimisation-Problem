package aco.algorithims;

import java.io.FileNotFoundException;
import java.util.HashMap;

import aco.Main;
import aco.world.Edge;
import aco.world.HomeNode;
import aco.world.World;

public class AntColonySystem implements AcoAlgorithim{
	double bestDistance = -1;
	HashMap<String, Double> totalPheromoneMap = new HashMap<>();

	// TODO: Remove Hardcode Value Of Starter Pheromone
	double starterPheromones = 1;

	@Override
	public AntResult runSingleAnt(Ant ant) throws FileNotFoundException{
		while(!ant.nextAction()){
			Edge edge = ant.getLastEdge();
			edge.dissipatePheromone(1 - Main.localDissipationRate);
			edge.addPheromone((Main.localDissipationRate * starterPheromones));
		}

		Edge edge = ant.getLastEdge();
		edge.dissipatePheromone(1 - Main.localDissipationRate);
		edge.addPheromone((Main.localDissipationRate * starterPheromones));

		AntResult antResult = ant.getResults();

		if(antResult.totalDistance < bestDistance || bestDistance == -1){
			bestDistance = antResult.totalDistance;
			totalPheromoneMap.clear();

			for(Edge edge2 : antResult.edgesTraversed){
				double pheromones = Main.pheromoneRate / antResult.totalDistance;
				if(totalPheromoneMap.get(edge2.getName()) != null){
					totalPheromoneMap.put(edge2.getName(), totalPheromoneMap.get(edge2.getName()) + pheromones);
				}
				else{
					totalPheromoneMap.put(edge2.getName(), pheromones);
				}
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

		bestDistance= -1;

		totalPheromoneMap.clear();
	}

	@Override
	public Ant getAnt(HomeNode homenode) {
		return new ACSAnt(homenode);
	}
}
