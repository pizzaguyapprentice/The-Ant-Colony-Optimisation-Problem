package ant.generator;

import java.io.IOException;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.FormattingStyle;
import com.google.gson.stream.JsonWriter;

public class WorldGenerator{
	private int minConnections = 2;
	private int maxConnections = 5;

	private int minNodes = 26;
	private int maxNodes = 26;

	private int minDistance = 2;
	private int maxDistance = 20;

	private int minRegionConnections = 2;
	private int maxRegionConnections = 3;

	public String generateNewWorld() throws IOException{
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

		return json;
	}
}
