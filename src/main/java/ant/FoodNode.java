package ant;

public class FoodNode extends Node{
    
    public FoodNode(String name) {
        super(name);
    }

	@Override
	public String toString(){
		return "Food" + super.toString();
	}
}
