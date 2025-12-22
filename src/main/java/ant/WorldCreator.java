package ant;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.stream.JsonReader;

public class WorldCreator{
	public static HomeNode createWorld() throws IOException{
		// Gets The Adjacency Table File
		File adjacencyTable = new File("src/main/resources/table.json");

		// Hashmap To Store The Nodes Temporarly
		HashMap<String, Node> nodeMap = new HashMap<>();;

		// Readers To Create A JsonReader To Read The Json Table
		FileReader freader = new FileReader(adjacencyTable);
		JsonReader jreader = new JsonReader(freader);
		jreader.beginObject();

		// For loop To Read The File
		// And Hashmap. Creates The Node
		for(int i = 0; jreader.hasNext(); i++){
			String name = jreader.nextName();
			ArrayList<String> neighbourList = new ArrayList<>(0);
			ArrayList<Integer> distanceList = new ArrayList<>(0);

			jreader.beginObject();
			jreader.nextName();
			jreader.beginArray();

			// neighbourList.add(name);

			while(jreader.hasNext()){
				neighbourList.add(jreader.nextString());
			}

			jreader.endArray();
			jreader.nextName();
			jreader.beginArray();

			while(jreader.hasNext()){
				distanceList.add(jreader.nextInt());
			}
			jreader.endArray();
			jreader.endObject();

			// adjacencyMatrix.add(adjacencyList.toArray(new String[0]));
			// distanceMatrix.add(distanceList.toArray(new Integer[0]));

			Node node = nodeMap.get(name);
			if(i == 0){
				node = new HomeNode(name);
				nodeMap.put(name, node);
			}
			else if(!jreader.hasNext() && node == null){
				node = new FoodNode(name);
				nodeMap.put(name, node);
			}
			else if(node == null){
				node = new Node(name);
				nodeMap.put(name, node);
			}

			if(!jreader.hasNext() && node != null){
				node.setFood(true);
			}
			
			for(int j = 0; j < neighbourList.size(); j++){
				String neighbourName = neighbourList.get(j);
				if(node.hasNeighbour(neighbourName)){
					continue;
				}
				Node neighbourNode = nodeMap.get(neighbourName);
				if(neighbourNode == null){
					neighbourNode = new Node(neighbourName);
					nodeMap.put(neighbourName, neighbourNode);
				}
				Edge edge = new Edge();
				edge.setDistance(distanceList.get(j));
				Path path1 = new Path(neighbourNode, edge);
				Path path2 = new Path(node, edge);
				node.addNeighbour(path1);
				neighbourNode.addNeighbour(path2);
			}
		}

		jreader.close();

		// For Loop To Print Out Generated Graph
		for(String nodeName : nodeMap.keySet()){
			String nodeType;

			if(nodeMap.get(nodeName) instanceof HomeNode){
				nodeType = "HomeNode";
			}
			else if(nodeMap.get(nodeName) instanceof FoodNode || nodeMap.get(nodeName).isFood()){
				nodeType = "FoodNode";
			}
			else{
				nodeType = "Node";
			}

			System.out.println(nodeType + ": " + nodeName);

			for(Path strings : nodeMap.get(nodeName).getNeighbours()){
				System.out.printf("\tNeighbour: %s\tDistance: %d\n",strings.getNode().getName(), strings.getEdge().getDistance());
			}
			System.out.println();
		}
		
		return (HomeNode) nodeMap.get("A");
	}
}