package ant;

public class HomeNode extends Node{

    public HomeNode(String name) {
        super(name);
    }

    boolean isHomeNode() {
        return true;
    }

     boolean isFoodNode() {
        return false;
    }
    
}
