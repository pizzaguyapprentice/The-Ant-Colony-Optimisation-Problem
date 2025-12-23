package ant;
public class Edge{
	private double phermone;
	private double distance;

	public Edge(double distance){
		this.distance = distance;
		this.phermone = 1;
	}

	public Edge(double distance, double phermone){
		this.distance = distance;
		this.phermone = phermone;
	}
	
	public double getPhermone() {
		return phermone;
	}
	public void setPhermone(double phermone) {
		this.phermone = phermone;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public void addPhermone(double phermone) {
		this.phermone = this.phermone + phermone;
	}
}
