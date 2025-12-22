package ant;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class WorldCreator{
	public static HomeNode createWorld() throws IOException{
		// Gets The Adjacency Table File
		File adjacencyTable = new File("src/main/resources/table.json");

		// Hashmap To Store The Nodes Temporarly
		HashMap<String, Node> nodeMap = new HashMap<>();

		// Scanner To Read File
		// Scanner in = new Scanner(adjacencyTable);


		FileReader freader = new FileReader(adjacencyTable);

		System.out.println();
		System.out.println();

		Gson gson = new Gson();

		JsonReader jreader = new JsonReader(freader);
	
		// Array To Store Adjacency Matrix
		ArrayList<String[]> adjacencyMatrix = new ArrayList<String[]>(0);
		ArrayList<Integer[]> distanceMatrix = new ArrayList<Integer[]>(0);
		
		// Integer To Check If The Length Of The Table Is Consistant
		int adjacencyListLength = 0;

		// For loop To Read The File And Output To Array
		// And Hashmap. Creates The Node
		jreader.beginObject();

		// System.out.println(jreader.nextName());
		for(int i = 0; jreader.hasNext(); i++){
			String name = jreader.nextName();
			ArrayList<String> adjacencyList = new ArrayList<>(0);
			ArrayList<Integer> distanceList = new ArrayList<>(0);

			jreader.beginObject();
			jreader.nextName();
			jreader.beginArray();

			adjacencyList.add(name);

			if(i == 0){
				Node node = new HomeNode(name);
				nodeMap.put(name, node);
			}
			// Shh You Dont See This
			else if(name.equals("G")){
				Node node = new FoodNode(name);
				nodeMap.put(name, node);
			}
			else{
				Node node = new Node(name);
				nodeMap.put(name, node);
			}

			for (int j = 0; jreader.hasNext(); j++) {
				adjacencyList.add(Integer.toString(jreader.nextInt()));
			}
			jreader.endArray();
			jreader.nextName();
			jreader.beginArray();
			for (int j = 0; jreader.hasNext(); j++) {
				distanceList.add(jreader.nextInt());
			}
			jreader.endArray();
			jreader.endObject();

			adjacencyMatrix.add(adjacencyList.toArray(new String[0]));
			distanceMatrix.add(distanceList.toArray(new Integer[0]));
		}
		// for(int i = 0; jreader.hasNext(); i++){
		// 	String[] nodeText = in.nextLine().split("\t");
		// 	adjacencyMatrix.add(nodeText);

		// 	if(i == 0){
		// 		adjacencyListLength = nodeText.length;
		// 		Node node = new HomeNode(nodeText[0]);
		// 		nodeMap.put(nodeText[0], node);
		// 	}
		// 	else if(!in.hasNextLine()){
		// 		Node node = new FoodNode(nodeText[0]);
		// 		nodeMap.put(nodeText[0], node);
		// 	}
		// 	else{
		// 		Node node = new Node(nodeText[0]);
		// 		nodeMap.put(nodeText[0], node);
		// 	}

		// 	if(nodeText.length != adjacencyListLength){
		// 		in.close();
		// 		throw new RuntimeException("The Adjacency Table Is Formated Incorrectly");
		// 	}
		// }

		jreader.close();

		// For Loop To Create The Adjacency Pairs
		for(int i = 0; i < adjacencyMatrix.size(); i++){
			for (int j = 2 + i; j < adjacencyMatrix.size() + 1; j++) {
				if(adjacencyMatrix.get(i)[j].equals("1")){
					Node node1 = nodeMap.get(adjacencyMatrix.get(i)[0]);
					Node node2 = nodeMap.get(adjacencyMatrix.get(j-1)[0]);
					Edge edge = new Edge();
					edge.setDistance(distanceMatrix.get(i)[j-1]);
					Path path1 = new Path(node2, edge);
					Path path2 = new Path(node1, edge);
					node1.addNeighbour(path1);
					node2.addNeighbour(path2);
				}
			}
		}

		// For Loop To Print Out Generated Graph
		for(int i = 0; i < adjacencyMatrix.size(); i++){
			String nodeType;

			if(nodeMap.get(adjacencyMatrix.get(i)[0]) instanceof HomeNode){
				nodeType = "HomeNode";
			}
			else if(nodeMap.get(adjacencyMatrix.get(i)[0]) instanceof FoodNode){
				nodeType = "FoodNode";
			}
			else{
				nodeType = "Node";
			}

			System.out.println(nodeType + ": " + adjacencyMatrix.get(i)[0]);

			for(Path strings : nodeMap.get(adjacencyMatrix.get(i)[0]).getNeighbours()){
				System.out.printf("\tNeighbour: %s    Distance: %d\n",strings.getNode().getName(), strings.getEdge().getDistance());
			}
			System.out.println();
		}
		
		return (HomeNode) nodeMap.get("A");
	}
}