package ant;
import java.io.IOException;



public class Main{
	public static void main(String[] args) throws IOException{

	
		Ant ant = new Ant(WorldCreator.createWorld());


		System.out.println(ant.getPosition().getName());

		for(int i = 0; i < 5; i++){
			ant.nextAction();
		}
		
			
	}
}
