package aco;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import aco.world.Edge;
import aco.world.World;

public class StatPrinter implements Closeable{
	private PrintWriter pw;

	public StatPrinter(File file) throws FileNotFoundException{
		this.pw = new PrintWriter(file);
		pw.println("Edges, Generation, Pheromone");
		pw.flush();
	}

	public void outputEdgePheromone(World world, int generation){
		for(Edge edge: world.getAllEdges()){
			pw.printf("%s, %d, %f\n", edge.getName(), generation, edge.getPheromone());
		}
	}

	@Override
	public void close() throws IOException {
		pw.close();
	}
}