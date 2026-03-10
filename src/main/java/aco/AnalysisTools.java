package aco;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class AnalysisTools {

    static final String RES_PATH = "src/main/resources/results";
    
    public static void main(String[] args) throws FileNotFoundException{


        File[] files = AnalysisTools.getOutputFiles();
        for (int i = 0; i < files.length; i++) {
            //File fileOutput = new File(getConvergedPath(files[i]));
            Scanner reader = new Scanner(files[i]);
            System.out.println("File: " + files[i]);

            
            String[] secondTokens = {null,null,null};
            
            while(reader.hasNextLine()){
                // READ FROM THE  LINE
                String data = reader.nextLine();
                //! Skip the initial row of edge,gen,pheromone
                if (data.startsWith("Edges")) {
                    continue;
                }

                //! Split the data into tokens
                String[] firstTokens = data.split(",");

                //! First row of edge,gen,pheromone needs to be compared to the second generation
            
                ArrayList<String> firstGen = new ArrayList<>();
                ArrayList<String> secondGen = new ArrayList<>();

                while (firstTokens[1] == secondTokens[1]) { 
                    firstGen.add(firstTokens[0]);
                    firstGen.add(firstTokens[1]);
                    firstGen.add(firstTokens[2]);
                }
                System.out.println("");

                secondGen.deepCop
                
                
            }
             
            System.out.println(files[i]);
        }
       
        
    }

    public static String getConvergedPaths(String edge, String generation, String pheromone){

        if (true) {
            
        }

        return "Paths have been converged on Generation " + generation;
    }

    
    public static File[] getOutputFiles(){
        File currentFile = new File(RES_PATH);
        System.out.println(RES_PATH);
        File[] files = currentFile.listFiles();
        if(files == null){
            System.out.println("Wrong directory kid.");
        }
        return files;
    }
}
