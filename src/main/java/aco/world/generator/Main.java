package aco.world.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
	public static void main(String[] args) throws IOException{
		System.out.println("Hello World");

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
		Date date = new Date(System.currentTimeMillis());
		File folder = new File("src/main/resources/worlds");
		File file = new File("src/main/resources/nodegraphd3.json");
		folder.mkdirs();
		file.createNewFile();

		String json = new WorldGenerator().generateNewWorld();

		FileWriter fw = new FileWriter(file);

		fw.write(json);

		fw.close();

		System.out.println(json);
	}
}
