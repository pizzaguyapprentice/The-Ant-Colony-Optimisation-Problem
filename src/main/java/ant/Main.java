package ant;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;


public class Main{

	static int GRAPH_SIZE = 7;
	public static void main(String[] args) throws FileNotFoundException{
		HashMap<String, Node> nodeMap = new HashMap<>();

		//Reading the file

		Scanner in = new Scanner(new File("src/main/resources/table.txt"));

		String[][] array2d = new String[GRAPH_SIZE][GRAPH_SIZE + 1];

		//First Node is HomeNode
		boolean homeNodeMade = false;
		boolean foodNodeMade = false;

		for(int i = 0; in.hasNext(); i++){
			String[] nodeText = in.nextLine().split("\t");
			array2d[i] = nodeText;
			
			if(!homeNodeMade){
				HomeNode homeNode = new HomeNode(nodeText[0]);
				nodeMap.put(nodeText[0], homeNode);
				homeNodeMade = true;
			}
			else if(i == (GRAPH_SIZE-1) && !foodNodeMade){
				FoodNode foodNode = new FoodNode(nodeText[0]);
				nodeMap.put(nodeText[0], foodNode);
				foodNodeMade = true;
			}
			else{
				Node node = new Node();
				node.setName(nodeText[0]);
				nodeMap.put(nodeText[0], node);
			}

			
		}
		in.close();

		//Fill the neighbours for each node

		//Fill the 
		System.out.println("Is it a home node?");
		System.out.println("Node: "+ nodeMap.get("A").getName() + " "+ nodeMap.get("A").isHomeNode());
		System.out.println("Node: "+ nodeMap.get("B").getName() + " "+ nodeMap.get("B").isHomeNode());
		
		System.out.println("Is it a food node?");
		System.out.println("Node: "+ nodeMap.get("A").getName() + " "+ nodeMap.get("A").isFoodNode());
		System.out.println("Node: "+ nodeMap.get("G").getName() + " "+ nodeMap.get("G").isFoodNode());
		



		for(int rows = 0; rows < array2d.length; rows++){
			
			for (int cols = 1; cols < array2d.length + 1; cols++) {
				
				if(array2d[rows][cols].equals("1") && !nodeMap.get(array2d[rows][0]).hasNeighbour(nodeMap.get(array2d[cols-1][0]))){
					
					Node node1 = nodeMap.get(array2d[rows][0]);
					Node node2 = nodeMap.get(array2d[cols-1][0]);
					Edge edge = new Edge();
					Path path1 = new Path(node2, edge);
					Path path2 = new Path(node1, edge);
					node1.addNeighbour(path1);
					node2.addNeighbour(path2);
				}
			}
		}

		// Display nodes and neighbours
		for(int i = 0; i < array2d.length; i++ ){

			System.out.println("Node: " + array2d[i][0]);

			for (Path strings : nodeMap.get(array2d[i][0]).getNeighbours()){
				System.out.println("\tNeighbours: "+strings.getNode().getName());
			}
			System.out.println();
		}
			
	}
}
