package ant;

public class FoodNode extends Node{
    
    public FoodNode(String name) {
        this.setName(name);
    }

    boolean isFoodNode() {
        return true;
    }

    boolean isHomeNode() {
        return false;
    }
}
