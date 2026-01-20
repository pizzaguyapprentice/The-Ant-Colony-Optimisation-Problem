package ant;
import java.util.ArrayList;

public class Node{
	private ArrayList<Path> neighbours = new ArrayList<>(0);
	private String name;
	private boolean isFoodNode = false;

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

	public void removeNeighbour(int i){
		neighbours.remove(i);
	}

	public boolean hasNeighbour(Node node){
		for(Path path : neighbours){
			if(path.getNode() == node){
				return true;
			}
		}
		return false;
	}

	public Path[] getNeighbours(){
		return neighbours.toArray(new Path[0]);
	}

	public int getNumOfNeighbours(){
		return neighbours.size();
	}

	public void setNeighbours(ArrayList<Path> neighbours){
		this.neighbours = neighbours;
	}

	public boolean hasNeighbour(String name){
		for(Path path : neighbours){
			if(name.equals(path.getNode().getName())){
				return true;
			}
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFood() {
		return isFoodNode;
	}

	public void setFood(boolean bool) {
		this.isFoodNode = bool;
	}

	public Path[] getNeighboursExcluding(Node... excludedNodes){
		if(excludedNodes == null){
			return getNeighbours();
		}

		Path[] neighbourArray = new Path[neighbours.size() - excludedNodes.length];

		ArrayList<Path> isThisThePath = (ArrayList<Path>) neighbours.clone();

		for(int i = 0; i < neighbours.size(); i++){
			for (int j = 0; j < excludedNodes.length; j++) {
				if(neighbours.get(i).getNode().equals(excludedNodes[j])) {
					isThisThePath.remove(neighbours.get(i));
				}
			}
		}
		return isThisThePath.toArray(neighbourArray);
	}
}
