package ant;
import java.io.FileNotFoundException;



public class Main{
	public static void main(String[] args) throws FileNotFoundException{

	
		Ant ant = new Ant(WorldCreator.createWorld());

		
		System.out.println(ant.getPosition().getName());

		ant.nextAction();
			
	}
}
