package aco.algorithims;

import java.io.FileNotFoundException;
import java.util.HashMap;

import aco.Main;
import aco.world.Edge;
import aco.world.World;






public class ElitistAnt implements AcoAlgorithim{

    HashMap<String, Double> totalPheromoneMap = new HashMap<>();

    public static double elitistWeight = 1.00;

    public static AntResult antResult;
    //public static AntResult checkBestTour = new AntResult(0.0,null,null);
    public static AntResult bestTour = new AntResult(0.00, null, null);
    //ArrayList<AntResult> Tours = new ArrayList<>();


    public AntResult updateBestTour(AntResult antResult){
        // Adding a list of antResult to find the best so far solution
        if (antResult.totalDistance >= bestTour.totalDistance) {
            bestTour = antResult;
        }
        return antResult;

    }

    public void updateElitistPheromone(){
         bestTour = updateBestTour(antResult);

        for(Edge edge : bestTour.edgesTraversed){
			double pheromones = (Main.pheromoneRate / antResult.totalDistance)+elitistWeight;
			if(totalPheromoneMap.get(edge.getName()) != null){
				totalPheromoneMap.put(edge.getName(), totalPheromoneMap.get(edge.getName()) + pheromones);
			}
			else{
				totalPheromoneMap.put(edge.getName(), pheromones);
			}
		}
    }

    @Override
	public AntResult runSingleAnt(Ant ant) throws FileNotFoundException{
		while(!ant.nextAction()){}
        antResult = ant.getResults();

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
        
        updateElitistPheromone();
		for(String edgeName : totalPheromoneMap.keySet()){
			world.updateEdgePheromone(edgeName, totalPheromoneMap.get(edgeName));
    
		}

		totalPheromoneMap.clear();
	}


    
}
