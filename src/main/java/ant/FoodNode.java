package ant;

public class FoodNode extends Node{
    
    public FoodNode(String name) {
        super(name);
    }

    boolean isFoodNode() {
        return true;
    }

    boolean isHomeNode() {
        return false;
    }
}
