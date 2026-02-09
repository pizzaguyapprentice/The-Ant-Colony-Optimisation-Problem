package ant.generator;

import java.util.ArrayList;

public class Region{
	private ArrayList<Character> connectedNodes = new ArrayList<>(0);

	public void addNode(char node){
		connectedNodes.add(node);
	}

	public ArrayList<Character> getNodes(){
		return connectedNodes;
	}

	public boolean isInRegion(char node){
		for (char string : connectedNodes) {
			if (string == node) {
				return true;
			}
		}
		return false;
	}
}
