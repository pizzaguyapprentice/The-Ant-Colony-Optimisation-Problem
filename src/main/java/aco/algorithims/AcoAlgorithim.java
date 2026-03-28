package aco.algorithims;

import java.io.FileNotFoundException;

import aco.world.World;

/*
	Interface For Implementing New Aco Algorithims

	You May Look At The AntSystem Class For Help With How To Use This Interface

	But The Gist Of It Is That ant.getResults() Should Be Called To Get The
	Ants Stats In "runSingleAnt" And Do What You Must With The New Results.
	Also Run The ant.nextAction() To Actually Run The Ant. You Must Also
	Return The Results For Main To Print Things.

	Method "updatePheromone" Is To Update The Pheromone On The World.
	You Must Both Do The Dissipation And Addition Of Pheromones There.
	Also Clear Any Storage Of Pheromones At The End Of The Method
	E.g. totalPheromoneMap.clear()

*/
public interface AcoAlgorithim{
	public void updatePheromone(World world);

	public AntResult runSingleAnt(Ant ant) throws FileNotFoundException;
}
