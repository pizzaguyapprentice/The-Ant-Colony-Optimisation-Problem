package ant;

public class HomeNode extends Node{

    public HomeNode(String name) {
        this.setName(name);
    }

    boolean isHomeNode() {
        return true;
    }

     boolean isFoodNode() {
        return false;
    }
    
}
