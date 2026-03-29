package aco.world;

import java.util.ArrayList;

public class Region{
	private ArrayList<String> connectedNodes = new ArrayList<>(0);

	public void addNode(String node){
		connectedNodes.add(node);
	}

	public ArrayList<String> getNodes(){
		return connectedNodes;
	}

	public boolean isInRegion(String node){
		for (String string : connectedNodes) {
			if (string.equals(node)){
				return true;
			}
		}
		return false;
	}
}
