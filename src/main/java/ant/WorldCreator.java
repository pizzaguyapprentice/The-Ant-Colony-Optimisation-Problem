package ant;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class WorldCreator{
	public static Node createWorld() throws FileNotFoundException{
		File adjecencyTable = new File("src/main/resources/table.txt");
		HashMap<String, Node> nodeMap = new HashMap<>();
		Scanner in = new Scanner(adjecencyTable);

		ArrayList<String[]> adjecencyMatrix = new ArrayList<String[]>(0);
		
		int adjecencyListLength = 0;

		for(int i = 0; in.hasNextLine(); i++){
			String[] nodeText = in.nextLine().split("\t");
			adjecencyMatrix.add(nodeText);

			if(i == 0){
				adjecencyListLength = nodeText.length;
				Node node = new HomeNode(nodeText[0]);
				nodeMap.put(nodeText[0], node);
			}
			else if(!in.hasNextLine()){
				Node node = new FoodNode(nodeText[0]);
				nodeMap.put(nodeText[0], node);
			}
			else{
				Node node = new Node(nodeText[0]);
				nodeMap.put(nodeText[0], node);
			}

			if(nodeText.length != adjecencyListLength){
				in.close();
				throw new RuntimeException("The Adjecency Table Is Formated Incorrectly");
			}
		}

		in.close();

		for(int i = 0; i < adjecencyMatrix.size(); i++){
			for (int j = 2 + i; j < adjecencyMatrix.size() + 1; j++) {
				if(adjecencyMatrix.get(i)[j].equals("1")){
					Node node1 = nodeMap.get(adjecencyMatrix.get(i)[0]);
					Node node2 = nodeMap.get(adjecencyMatrix.get(j-1)[0]);
					Edge edge = new Edge();
					Path path1 = new Path(node2, edge);
					Path path2 = new Path(node1, edge);
					node1.addNeighbour(path1);
					node2.addNeighbour(path2);
				}
			}
		}

		for(int i = 0; i < adjecencyMatrix.size(); i++){
			String nodeType;

			if(nodeMap.get(adjecencyMatrix.get(i)[0]) instanceof HomeNode){
				nodeType = "HomeNode";
			}
			else if(nodeMap.get(adjecencyMatrix.get(i)[0]) instanceof FoodNode){
				nodeType = "FoodNode";
			}
			else{
				nodeType = "Node";
			}
			
			System.out.println(nodeType + ": " + adjecencyMatrix.get(i)[0]);

			for (Path strings : nodeMap.get(adjecencyMatrix.get(i)[0]).getNeighbours()){
				System.out.println("\tNeighbour: " + strings.getNode().getName());
			}
			System.out.println();
		}
		
		return nodeMap.get("A");
	}
}