package ant;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Main{
	public static void main(String[] args) throws FileNotFoundException{
		HashMap<String, Node> nodeMap = new HashMap<>();
		Scanner in = new Scanner(new File("table.txt"));

		String[][] array2d = new String[7][8];
		for(int j = 0; in.hasNext(); j++){
			String[] nodeText = in.nextLine().split("\t");
			array2d[j] = nodeText;
			
			Node node = new Node();
			node.setName(nodeText[0]);
			nodeMap.put(nodeText[0], node);
			// for(String text : array2d[j]){
			// 	System.out.println(text);
			// }
		}

		in.close();

		for(int i = 0; i < array2d.length; i++){
			for (int j = 1; j < array2d.length + 1; j++) {
				if(array2d[i][j].equals("1") && !nodeMap.get(array2d[i][0]).hasNeighbour(nodeMap.get(array2d[j-1][0]))){
					Node node1 = nodeMap.get(array2d[i][0]);
					Node node2 = nodeMap.get(array2d[j-1][0]);
					Edge edge = new Edge();
					Path path1 = new Path(node2, edge);
					Path path2 = new Path(node1, edge);
					node1.addNeighbour(path1);
					node2.addNeighbour(path2);
				}
			}
		}

		for (Path strings : nodeMap.get("A").getNeighbours()) {
			System.out.println(strings.getNode().getName());
		}
	}
}
