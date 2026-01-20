package ant;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import com.google.gson.stream.JsonReader;

public class World{
	private HashMap<String, Edge> edgeMap = new HashMap<>();
	private HashMap<String, Node> nodeMap = new HashMap<>();
	private final HomeNode startNode;

	public World() throws IOException{
		startNode = createWorld();
	}

	public HomeNode getStartNode(){
		return startNode;
	}

	private HomeNode createWorld() throws IOException{
		// Gets The Adjacency Table File
		File adjacencyTable = new File("src/main/resources/table.json");

		// Readers To Create A JsonReader To Read The Json Table
		FileReader freader = new FileReader(adjacencyTable);
		JsonReader jreader = new JsonReader(freader);
		jreader.beginObject();

		// For loop To Read The File
		// And Hashmap. Creates The Node
		jreader.nextName();
		jreader.beginArray();

		for(int i = 0; jreader.hasNext(); i++){
			String name = jreader.nextString();
			if(i == 0){
				Node node = new HomeNode(name);
				nodeMap.put(name, node);
			}
			else if(!jreader.hasNext()){
				Node node = new FoodNode(name);
				node.setFood(true);
				nodeMap.put(name, node);
			}
			else{
				Node node = new Node(name);
				nodeMap.put(name, node);
			}
		}

		jreader.endArray();
		jreader.nextName();
		jreader.beginArray();

		while(jreader.hasNext()){
			jreader.beginArray();
			String firstNodeName = jreader.nextString();
			String secondNodeName = jreader.nextString();
			int distance = jreader.nextInt();
			jreader.endArray();

			Node firstNode = nodeMap.get(firstNodeName);
			Node secondNode = nodeMap.get(secondNodeName);

			if(firstNode == null || secondNode == null){
				jreader.close();
				throw new NullPointerException("Adjecency To Non Existant Node");
			}

			Edge edge = new Edge((firstNodeName + secondNodeName).toLowerCase(), distance);
			Path firstPath = new Path(secondNode, edge);
			Path secondPath = new Path(firstNode, edge);
			firstNode.addNeighbour(firstPath);
			secondNode.addNeighbour(secondPath);
			edgeMap.put(edge.getName(), edge);
		}

		jreader.endArray();
		jreader.endObject();

		jreader.close();
		
		return (HomeNode) nodeMap.get("A");
	}

	public void printWorld(){
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
				System.out.printf("\tNeighbour: %s\tDistance: %f\n",strings.getNode().getName(), strings.getEdge().getDistance());
			}
			System.out.println();
		}
	}

	public void dissipatePheromone(double dissipationRate){
		for(String edgeName: edgeMap.keySet()){
			edgeMap.get(edgeName).dissipatePheromone(dissipationRate);
		}
	}

	public void updateEdgePheromone(String edgeName, double pheromone){
		edgeMap.get(edgeName).addPheromone(pheromone);
	}

	public void printEdgePheromone(){
		for(String edgeName: edgeMap.keySet()){
			System.out.printf("Edge %s: Pheromone Count: %f\n",edgeName, edgeMap.get(edgeName).getPheromone());
		}
	}

	public void outputEdgePheromone(PrintWriter pw){
		for(String edgeName: edgeMap.keySet()){
			pw.printf("Edge %s: Pheromone Count: %f\n",edgeName, edgeMap.get(edgeName).getPheromone());
		}
		pw.println();
	}
}