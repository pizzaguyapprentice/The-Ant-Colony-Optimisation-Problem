package ant;
import java.util.ArrayList;

public class Node{

	private ArrayList<Path> neighbours = new ArrayList<>(0);
	private String name;

	public Node(String name){
		this.name = name;
	}

	public Path getNeighbour(int i){
		return neighbours.get(i);
	}

	public void setNeighbour(int i, Path path){
		neighbours.set(i, path);
	}

	public void addNeighbour(Path path){
		neighbours.add(path);
	}

	public boolean hasNeighbour(Node node){
		for(Path path : neighbours){
			if(path.getNode() == node){
				return true;
			}
		}
		return false;
	}

	public ArrayList<Path> getNeighbours(){
		return neighbours;
	}
	public void setNeighbours(ArrayList<Path> neighbours){
		this.neighbours = neighbours;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
