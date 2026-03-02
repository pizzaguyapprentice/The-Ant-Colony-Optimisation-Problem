package aco.world;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.FormattingStyle;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class World{
	private HashMap<String, Edge> edgeMap = new HashMap<>();
	private HashMap<String, Node> nodeMap = new HashMap<>();
	private final HomeNode startNode;

	public World() throws IOException{
		StringReader sr = new StringReader(generateNewWorld());
		startNode = createWorld(sr);
	}

	public World(File file) throws IOException{
		FileReader fr = new FileReader(file);
		startNode = createWorld(fr);
	}

	public World(String json) throws IOException{
		StringReader sr = new StringReader(json);
		startNode = createWorld(sr);
	}

	public HomeNode getStartNode(){
		return startNode;
	}

	private HomeNode createWorld(Reader r) throws IOException{
		// Readers To Create A JsonReader To Read The Json Table
		
		JsonReader jreader = new JsonReader(r);
		jreader.beginObject();

		// For loop To Read The File
		// And Hashmap. Creates The Node
		jreader.nextName();
		jreader.beginArray();

		for(int i = 0; jreader.hasNext(); i++){
			jreader.beginObject();
			jreader.nextName();
			String name = jreader.nextString();
			jreader.endObject();
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
			jreader.beginObject();
			jreader.nextName();
			String firstNodeName = jreader.nextString();
			jreader.nextName();
			String secondNodeName = jreader.nextString();
			jreader.nextName();
			double pheromone = jreader.nextDouble();
			jreader.nextName();
			int distance = jreader.nextInt();
			jreader.endObject();

			Node firstNode = nodeMap.get(firstNodeName);
			Node secondNode = nodeMap.get(secondNodeName);

			if(firstNode == null || secondNode == null){
				jreader.close();
				throw new NullPointerException("Adjecency To Non Existaco-algorithims Node");
			}

			Edge edge = new Edge((firstNodeName + secondNodeName).toLowerCase(), distance, pheromone);
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

	private static int minConnections = 2;
	private static int maxConnections = 2;

	private static int minNodes = 26;
	private static int maxNodes = 26;

	private static int minDistance = 2;
	private static int maxDistance = 20;

	private static int minRegionConnections = 2;
	private static int maxRegionConnections = 3;

	public static String generateNewWorld() throws IOException{
		SecureRandom r = new SecureRandom();

		int noOfNodes = r.nextInt(minNodes, maxNodes+1);
		ArrayList<Character> nodes = new ArrayList<Character>(noOfNodes);
		HashMap<Character, ArrayList<Character>> neighbourMap = new HashMap<>();
		ArrayList<Region> regions = new ArrayList<>(0);

		String json = "";
		
		StringWriter s = new StringWriter();

		JsonWriter w = new JsonWriter(s);
		w.setFormattingStyle(FormattingStyle.PRETTY);

		w.beginObject();
		w.name("nodes");

		w.beginArray();

		int i;
		for(i = 0; i < noOfNodes; i++){
			char c = (char) (65 + i);
			nodes.add(c);
			neighbourMap.put(c, new ArrayList<Character>(0));

			w.beginObject();
			w.name("name");
			w.value(c + "");
			w.endObject();
		}
		w.endArray();

		w.name("links");

		w.beginArray();
		for(i = 0; i < nodes.size(); i++){
			Character node = nodes.get(i);
			Region nodeRegion = null;

			if(regions.isEmpty()){
				Region newRegion = new Region();
				newRegion.addNode(node);
				regions.add(newRegion);
				nodeRegion = newRegion;
			}

			for(int j = 0; j < regions.size(); j++){
				Region region = regions.get(j);
				if(region.isInRegion(node)){
					nodeRegion = region;
					break;
				}
				if(j == regions.size() - 1){
					Region newRegion = new Region();
					newRegion.addNode(node);
					regions.add(newRegion);
					nodeRegion = newRegion;
					break;
				}
			}

			int edges = r.nextInt(minConnections, maxConnections+1);

			if (neighbourMap.get(node).size() >= edges){
				continue;
			}

			for(int j = 0; j < edges; j++){
				ArrayList<Character> possibleNeighbours = (ArrayList<Character>) nodes.clone();
				possibleNeighbours.remove(node);
				possibleNeighbours.removeAll(neighbourMap.get(node));

				if (possibleNeighbours.size() == 0) {
					break;
				}

				w.beginObject();

				Character newNeighbour = possibleNeighbours.get(r.nextInt(0, possibleNeighbours.size()));
				nodeRegion.addNode(newNeighbour);
				if(node < newNeighbour){
					System.out.printf("%s\n", "" + node + newNeighbour);
					w.name("source");
					w.value(node + "");

					w.name("target");
					w.value(newNeighbour + "");
				}
				else if(node > newNeighbour){
					System.out.printf("%s\n", "" + newNeighbour + node);
					w.name("source");
					w.value(newNeighbour + "");

					w.name("target");
					w.value(node + "");
				}
				neighbourMap.get(node).add(newNeighbour);
				neighbourMap.get(newNeighbour).add(node);

				w.name("pheromone");
				w.value(1);
				w.name("distance");
				w.value(r.nextInt(minDistance, maxDistance + 1));
				w.endObject();
			}
		}
		w.endArray();

		w.endObject();

		w.close();

		json = s.toString();

		// File folder = new File("src/main/resources/worlds");
		File file = new File("src/main/resources/nodegraphd3.json");
		// folder.mkdirs();
		file.createNewFile();

		FileWriter fw = new FileWriter(file);

		fw.write(json);

		fw.close();

		System.out.println(json);

		return json;
	}
}