package ant;
import java.io.FileNotFoundException;



public class Main{
	public static void main(String[] args) throws FileNotFoundException{

	
		Ant ant = new Ant(WorldCreator.createWorld());

		ant.nextAction();
			
	}
}
