package ant;
public class Edge{
	private String name;
	private double pheromone;
	private double distance;

	public Edge(double distance){
		this.distance = distance;
		this.pheromone = 1;
	}

	public Edge(double distance, double pheromone){
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
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public void addPheromone(double pheromone) {
		this.pheromone = this.pheromone + pheromone;
	}
	public void removePheromone(double pheromone) {
		this.pheromone = this.pheromone - pheromone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
