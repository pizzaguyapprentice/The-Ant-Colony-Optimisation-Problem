package ant;

public class HomeNode extends Node{

    public HomeNode(String name) {
        super(name);
    }

	@Override
	public String toString(){
		return "Home" + super.toString();
	}
}
