package ant;
public class Edge{
	private int phermone;
	private int distance;

	public Edge(int distance){
		this.distance = distance;
		this.phermone = 0;
	}

	public Edge(int distance, int phermone){
		this.distance = distance;
		this.phermone = phermone;
	}
	
	public int getPhermone() {
		return phermone;
	}
	public void setPhermone(int phermone) {
		this.phermone = phermone;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public void addPhermone(int phermone) {
		this.phermone = this.phermone + phermone;
	}
}
