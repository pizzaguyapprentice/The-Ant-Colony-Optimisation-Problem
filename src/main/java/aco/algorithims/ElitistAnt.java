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
    public static AntResult bestTour = new AntResult(Double.MAX_VALUE, null, null);
    //ArrayList<AntResult> Tours = new ArrayList<>();


    public AntResult updateBestTour(AntResult antResult){
        // Adding a list of antResult to find the best so far solution
        if (antResult.totalDistance <= bestTour.totalDistance) {
            bestTour = antResult;
        }
        return bestTour;

    }

    public void updateElitistPheromone(){

        bestTour = updateBestTour(antResult);

		if(bestTour == null || bestTour.edgesTraversed == null){
            System.out.println("bestTour or edgesTraversed is null");
            return;
        }

        for(Edge edge : bestTour.edgesTraversed){
			//(Main.pheromoneRate / antResult.totalDistance)
			double pheromones = (Main.pheromoneRate / bestTour.totalDistance)*elitistWeight;
			if(totalPheromoneMap.get(edge.getName()) != null){
				totalPheromoneMap.put(edge.getName(), totalPheromoneMap.get(edge.getName()) + pheromones);
			}
			else{
				totalPheromoneMap.put(edge.getName(), pheromones);
			}
			
			
		}
		if(Main.DEBUG == 1){
			System.out.println("UPDATED ELITIST PHEROMONE");
		}

    }

    @Override
	public AntResult runSingleAnt(Ant ant) throws FileNotFoundException{
		while(!ant.nextAction()){}

        antResult = ant.getResults();
		updateBestTour(antResult);
		
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
		System.out.println("=== updatePheromone called ===");
		try {
			world.dissipatePheromone(Main.dissipationRate);
			
			for(String edgeName : totalPheromoneMap.keySet()){
				world.updateEdgePheromone(edgeName, totalPheromoneMap.get(edgeName));
			}
			updateElitistPheromone();
			totalPheromoneMap.clear();
    	} 
		catch(Exception e) {
        	e.printStackTrace(); 
    	}
	}


    
}
