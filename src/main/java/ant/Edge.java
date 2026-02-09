package ant;
public class Edge{
	private String name;
	private double pheromone;
	private double distance;

	public Edge(String name, double distance, double pheromone){
		this.name = name;
		this.distance = distance;
		this.pheromone = pheromone;
	}

	public double getPheromone() {
		return pheromone;
	}

	public void setPheromone(double pheromone) {
		this.pheromone = pheromone;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance){
		this.distance = distance;
	}

	public void addPheromone(double newPheromone){
		this.pheromone = this.pheromone + newPheromone;
	}

	public void dissipatePheromone(double dissipationRate){
		this.pheromone = (dissipationRate * this.pheromone);
	}

	public void updatePheromone(double dissipationRate, double newPheromone){
		this.pheromone = (dissipationRate * this.pheromone) + newPheromone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
