package ant;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import com.google.gson.FormattingStyle;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

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
			Node node;
			if(i == 0){
				node = new HomeNode(name);
			}
			else if(!jreader.hasNext()){
				node = new FoodNode(name);
			}
			else{
				node = new Node(name);
			}
			nodeMap.put(name, node);
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

	public Node[] getAllNodes(){
		return nodeMap.values().toArray(new Node[nodeMap.size()]);
	}

	public Edge[] getAllEdges(){
		return edgeMap.values().toArray(new Edge[edgeMap.size()]);
	}

	public void printWorld(){
		// For Loop To Print Out Generated Graph
		for(String nodeName : nodeMap.keySet()){
			System.out.println(nodeMap.get(nodeName).toString() + ": " + nodeName);

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

	public String outputWorldAsJson() throws IOException{
		String json = "";
		
		StringWriter s = new StringWriter();

		JsonWriter w = new JsonWriter(s);
		w.setFormattingStyle(FormattingStyle.PRETTY);

		w.beginObject();
		w.name("nodes");

		w.beginArray();
		for(String nodeName : nodeMap.keySet()){
			w.beginObject();
			w.name("name");
			w.value(nodeName);
			w.endObject();
		}
		w.endArray();

		w.name("links");
		w.beginArray();
		for(String edgeName : edgeMap.keySet()){
			w.beginObject();
			
			boolean found;
			for(String nodeName : nodeMap.keySet()){
				found = false;
				for(int i = 0; i < nodeMap.get(nodeName).getNumOfNeighbours(); i++){
					if(nodeMap.get(nodeName).getNeighbour(i).getEdge() == edgeMap.get(edgeName)){
						w.name("source");
						w.value(nodeName);

						w.name("target");
						w.value(nodeMap.get(nodeName).getNeighbour(i).getNode().getName());

						found = true;
						break;
					}
				}
				if(found){
					break;
				}
			}

			w.name("pheromone");
			w.value(edgeMap.get(edgeName).getPheromone());
			w.name("distance");
			w.value(edgeMap.get(edgeName).getDistance());
			w.endObject();
		}
		w.endArray();


		w.endObject();

		json = s.toString();
		w.close();
		return json;
	}

	// Remove This Method
	// Should Create A Class For Outputting To A File
	public void outputEdgePheromone(PrintWriter pw, int i){
		for(String edgeName: edgeMap.keySet()){
			pw.printf("%s, %d, %f\n", edgeName, i, edgeMap.get(edgeName).getPheromone());
		}
	}
}