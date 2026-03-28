package aco.algorithims;

import aco.world.Edge;

public class AntResult {
	public double totalDistance;
	public String solution;
	public Edge[] edgesTraversed;

	public AntResult(double totalDistance, String solution, Edge[] edgesTraversed){
		this.totalDistance = totalDistance;
		this.solution = solution;
		this.edgesTraversed = edgesTraversed;
	}
}
