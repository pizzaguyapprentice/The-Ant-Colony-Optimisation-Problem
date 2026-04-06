package aco;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import aco.algorithims.AcoAlgorithim;
import aco.algorithims.Ant;
import aco.algorithims.AntResult;
import aco.algorithims.AntSystem;
import aco.algorithims.ElitistAnt;
import aco.world.World;

public class Main{
	// Debug Parameter For Extra Printing
	public static int DEBUG = 0;

	// Global Parameters For ACO Problem
	public static final double distanceImportance = 0.1;
	public static final double pheromoneImportance = 1.8;
	public static final double dissipationRate = 0.15;
	public static final double pheromoneRate = 3;

	// Number Of Ants Per Generation
	public static final int NUM_ANTS = 100;
	public static final int GENS = 100;

	public static Process p = null;

	// @SuppressWarnings("unused")
	public static void main(String[] args) throws IOException, InterruptedException{
		File worldFile = null;
		AcoAlgorithim acoAlgorithm = new AntSystem();
		boolean runGui = true;
		for(int i = 0; i < args.length; i++){
			if(args[i].equals("-h")){
				System.out.println("Ant Colony Optimisation Program");
				System.out.println();
				System.out.println("A Program To Run The Aco Algorithm To Find The Most Optimal Path In A Given World");
				System.out.println();
				System.out.println("-f FILE\t\t\t\tRead FILE As A World File To Use For The Simulation.");
				System.out.println("\t\t\t\tIf The Option Is Ommited Or No File Is Specified\n\t\t\t\tThen A Random World Is Generated And Outputted");
				System.out.println();
				System.out.println("-v\t\t\t\tBe Verbose");
				System.out.println();
				System.out.println("-a ALGORITHIM\t\t\tRun Aco With Specified Algorithim.\n\t\t\t\tCurrently Supported Options Are AntSystem, ElitistAnt And AntColonySystem\n\t\t\t\tDefault: AntSystem");
				System.out.println();
				System.out.println("-n\t\t\t\tDo Not Run The Gui At The End");
				System.out.println();
				System.out.println("-h\t\t\t\tPrints Help Information For The Program");

				System.exit(0);
			}
			if(args[i].equals("-f") && i+1 < args.length){
				worldFile = new File(args[i+1]);
			}
			if(args[i].equals("-v")){
				DEBUG = 1;
			}
			if(args[i].equals("-a") && i+1 < args.length){
				if(args[i+1].equals("AntSystem")){
					continue;
				}
				else if(args[i+1].equals("ElitistAntSystem")){
					acoAlgorithm = new ElitistAnt();
				}
				else if(args[i+1].equals("AntColonySystem")){
					// acoAlgorithm = new AntColonySystem();
				}
			}
			if(args[i].equals("-n")){
				runGui = false;
			}
		}

		World world;
		if(worldFile == null || !worldFile.exists()){
			world = new World();
			worldFile = new File("world.json");
			worldFile.createNewFile();
			FileWriter fw = new FileWriter(worldFile);
			fw.write(world.outputWorldAsJson());
			fw.close();

			// This Will Be Removed Once The Mess With js Is Figured Out
			File file = new File("src/main/resources/nodegraphd3.json");
			file.createNewFile();
			fw = new FileWriter(file);
			fw.write(world.outputWorldAsJson());
			fw.close();
			//
		}
		else{
			world = new World(worldFile);
		}

		Ant ant = new Ant(world.getStartNode());

		SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss-SSS");
		Date date = new Date(System.currentTimeMillis());
		// I Want To Change This Ones Folder But That Would Require Changing Stuff In The js Which I Unfamiliar With
		File file = new File("src/main/resources/results/output-" + sdf.format(date) + ".txt");
		File folder = new File("src/main/resources/results");
		//

		folder.mkdirs();
		file.createNewFile();

		StatPrinter sp = new StatPrinter(file);

		if(Main.DEBUG >= 1){
			world.printWorld();
		}

		// START measuring time in seconds
		Time time = new Time();
		time.startTime();

		for(int i = 1; i <= GENS; i++){
			if(Main.DEBUG >= 1){
				System.out.printf("\nStarted Generation %d\n", i);
			}

			double bestDistance = -1;
			String bestSolution = "";

			for(int j = 1; j <= NUM_ANTS; j++){
				if(Main.DEBUG >= 1){
					System.out.printf("\nGen %d: Ant %d\n", i, j);

				}

				AntResult results = acoAlgorithm.runSingleAnt(ant);

				ant.resetAnt();

				double totalDistance = results.totalDistance;

				if(Main.DEBUG >= 1){
					System.out.printf("Total Distance: %f\n", totalDistance);
					System.out.printf("Gen %d Ant's Solution: %s\n", i , results.solution);
				}

				// System.out.printf("Ant's Solution: %s\n", ant.getSolution());

				if(bestDistance > totalDistance || bestDistance == -1){
					bestDistance = totalDistance;
					bestSolution = results.solution;
				}
			}

			acoAlgorithm.updatePheromone(world);

			sp.outputEdgePheromone(world, i);

			System.out.printf("Gen %d Best Ant's Solution: %s\n", i , bestSolution);
			bestDistance = -1;
			bestSolution = "";

			if(Main.DEBUG >= 1){
				world.printEdgePheromone();
				System.out.printf("\nFinished Generation %d\n", i);
			}

			if(i == GENS){
				// System.out.println(world.outputWorldAsJson());
			}
		}
		time.elapsedTime();
		sp.close();

		if(runGui){
			ProcessBuilder b = new ProcessBuilder("node", "./src/main/js/server.js");

			p = b.start();

			Runnable r = new Visuals();
			new Thread(r).start();

			p.waitFor();
		}
	}
}
