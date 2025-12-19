package ant;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Main{
	public static void main(String[] args) throws FileNotFoundException{
		HashMap<String, Node> nodeMap = new HashMap<>();

		//Reading the file

		Scanner in = new Scanner(new File("src/main/resources/table.txt"));

		String[][] array2d = new String[7][8];
		for(int i = 0; in.hasNext(); i++){
			String[] nodeText = in.nextLine().split("\t");
			array2d[i] = nodeText;
			
			Node node = new Node();
			node.setName(nodeText[0]);
			nodeMap.put(nodeText[0], node);
		}
		in.close();

		//Fill the 
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
