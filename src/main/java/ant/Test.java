package ant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// Class With Main For Debugging Purposes
public class Test{
	public static void main(String[] args) throws IOException{
		World world = new World();

		System.out.println(world.outputWorldAsJson());

		FileWriter fw = new FileWriter(new File("help.json"));

		fw.write(world.outputWorldAsJson());

		fw.close();
	}
}
