package ant;
import java.io.File;
import java.io.FileNotFoundException;
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
                String data = reader.nextLine();
                String[] firstTokens = data.split(",");
                System.out.println("\t" + firstTokens[0] + ", " + firstTokens[1] + " " + firstTokens[2]);
                
                if (secondTokens[0] == null) {
                    continue;
                }
                else if (secondTokens[0] != null) {
                    double firstTokenPheromone = Double.parseDouble(firstTokens[2]);
                    double secondTokenPheromone = Double.parseDouble(secondTokens[2]);
                    
                }
                
                
                
                secondTokens = firstTokens;
                
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
