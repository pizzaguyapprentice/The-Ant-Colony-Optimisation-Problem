package aco;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Visuals extends Application implements Runnable{
	@Override
	public void run() {
		launch();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		WebView webView = new WebView();

		// File f = new File("html.html");
		// Scanner fw = new Scanner(f);
		// String s = "";
		// while (fw.hasNext()) {
		// 	s = s + fw.nextLine();
		// }

		// fw.close();

		primaryStage.setOnCloseRequest((e)->{
			System.exit(0);
		});

		webView.getEngine().load("http://127.0.0.1:6767/");

		Scene scene = new Scene(webView);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
