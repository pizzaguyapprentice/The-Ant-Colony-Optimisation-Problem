package ant;
import java.util.ArrayList;
import java.util.Random;

public class Ant {
    private HomeNode homeNode;

    private Node position = homeNode;
    private Node lastPosition;
    private boolean hasFood = false;

    private ArrayList<Double> visibilityMetrics = new ArrayList<>();

    Node getPosition(){
        return position;
    }
    void setPosition(Node position){
        position = this.position;
    }

    Node getLastPosition(){
        return lastPosition;
    }

    void setLastPosition(Node lastPosition){
        lastPosition = this.lastPosition;
    }

    boolean hasFood(){
        if(!hasFood){
            return false;
        }
        else{
            return true;
        }
    }

    void setHasFood(boolean hasFood){
        hasFood = this.hasFood;
    }

    void nextAction(){
        //Generate a random double from 0.0 to 1.0
        Random random = new Random();
        double decision = random.nextDouble();

        //Here we will weigh out the visibility metric between all possible routes.

        //View how many neighbours are available to travel to

        for(int potent = 0; potent < position.getNeighbours().size(); potent++){
            System.out.println();
        }

    }


    

    

}
