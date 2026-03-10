package aco.world;
import java.util.ArrayList;

public class Node{
	private String name;
	private transient ArrayList<Edge> neighbours = new ArrayList<>(0);

	public Node(String name){
		this.name = name;
	}

	public Node getNeighbour(int i){
		if(neighbours.get(i).getSource() == this){
			return neighbours.get(i).getTarget();
		}
		return neighbours.get(i).getSource();
	}

	public void setNeighbour(int i, Edge path){
		neighbours.set(i, path);
	}

	public void addNeighbour(Edge path){
		neighbours.add(path);
	}

	public void removeNeighbour(int i){
		neighbours.remove(i);
	}

	public Edge[] getNeighbours(){
		return neighbours.toArray(new Edge[0]);
	}

	public int getNumOfNeighbours(){
		return neighbours.size();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Edge[] getNeighboursExcluding(Node... excludedNodes){
		if(excludedNodes == null){
			return getNeighbours();
		}

		Edge[] neighbourArray = new Edge[neighbours.size() - excludedNodes.length];

		ArrayList<Edge> isThisThePath = (ArrayList<Edge>) neighbours.clone();

		for(int i = 0; i < neighbours.size(); i++){
			for (int j = 0; j < excludedNodes.length; j++) {
				if(getNeighbour(i).equals(excludedNodes[j])) {
					isThisThePath.remove(neighbours.get(i));
				}
			}
		}
		return isThisThePath.toArray(neighbourArray);
	}

	public boolean hasNeighbour(String name){
		for(Edge path : neighbours){
			if(name.equals(path.getOtherNode(this).getName())){
				return true;
			}
		}
		return false;
	}

	public boolean hasNeighbour(Node node){
		for(Edge path : neighbours){
			if(path.getOtherNode(this) == node){
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString(){
		// God I Will Be Killed For This
		// Please Dont Read HomeNode And FoodNode
		return "Node";
	}
}
