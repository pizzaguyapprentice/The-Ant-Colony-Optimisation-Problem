package ant;

import java.io.IOException;

// Class With Main For Debugging Purposes
public class Test{
	public static void main(String[] args) throws IOException{
		World world = new World();

		System.out.println(world.outputWorldAsJson());
	}
}
