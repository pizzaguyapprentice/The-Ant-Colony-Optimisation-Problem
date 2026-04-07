package aco;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class AnalysisTools {

    static final String RES_PATH = "src/main/resources/results";
    final static int STREAK = 3;
    public static void main(String[] args) throws FileNotFoundException{


        File file = AnalysisTools.getOutputFile();
        System.out.println("Reading the file:" +file.getName());

        ArrayList<String[]> allLines = readFile(file);

        getConvergedPaths(allLines);

        // for (String[] line : allLines) {
        //     System.out.println("Edge: " + line[0] + " Generation: " + line[1] + " Pheromone: " + line[2]);
        // }
    }

    public static ArrayList<String[]> readFile(File file) throws FileNotFoundException{
        ArrayList<String[]> allLines = new ArrayList<>();

        Scanner r = new Scanner(file);
        while(r.hasNextLine()){
            String line = r.nextLine();

            if(line.startsWith("Edges")){
                continue;
            }
            String[] tokens = line.split(",");
            allLines.add(tokens);
        }
        r.close();
        return allLines;


    }

    public static void getConvergedPaths(ArrayList<String[]> allLines){

        String currentGen = "";
        ArrayList<String[]> perGenData = new ArrayList<>();

        String lastGenPath = "";
        int concurent = 0;

        for(int i = 0; i < allLines.size();i++){
            String[] line = allLines.get(i);
            String generation = line[1];

            if(!generation.equals(currentGen)){
                String genPath = processGeneration(perGenData);

                System.out.println("Generation "+ currentGen + "- Path: "+genPath);
                
                if(genPath.equals(lastGenPath)){
                    concurent++;
                }
                else{
                    concurent =1;
                }
            
                if(concurent >= STREAK){
                    System.out.println("PATH HAS CONVERGED ON: ");
                    System.out.println("Generation: " + currentGen);
                    System.out.println("Path: " + genPath);
                    System.out.println("Concurent for " + concurent + " generations");
                }

                lastGenPath = genPath;
                perGenData.clear();
            }
            //System.out.println("Edge "+line[0]+" Gen "+line[1]+ " Pheromone "+line[2]);
            currentGen = generation;
            perGenData.add(line);
        
        }

        
    }

    public static String processGeneration(ArrayList<String[]> perGenData){
        
        String path = "";

        for(String[] line : perGenData){
            String edge = line[0];
            double pheromone = Double.parseDouble(line[2]);

            if(pheromone > 1.00){
                path += edge;
            }
        }
        return path;
    }

    
    public static File getOutputFile(){
        File currentFile = new File(RES_PATH);
        //System.out.println(RES_PATH);
        File[] files = currentFile.listFiles();

        File mostRecent = files[0];

        for(File file: files){
            if (file.getName().compareTo(mostRecent.getName()) > 0) {
                mostRecent = file;
            }

        }
        return mostRecent;
    }
}
