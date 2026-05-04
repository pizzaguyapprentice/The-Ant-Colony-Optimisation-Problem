package aco;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
		webView.setFocusTraversable(true);

		// File f = new File("html.html");
		// Scanner fw = new Scanner(f);
		// String s = "";
		// while (fw.hasNext()) {
		// 	s = s + fw.nextLine();
		// }

		// fw.close();

		primaryStage.setOnCloseRequest((e)->{
			Main.p.destroy();
			primaryStage.close();
		});

		webView.getEngine().load("http://127.0.0.1:6767/");

		Scene scene = new Scene(webView);
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.COMMA) {
				event.consume();
				webView.getEngine().executeScript("previousIteration()");
			} else if (event.getCode() == KeyCode.PERIOD) {
				event.consume();
				webView.getEngine().executeScript("nextIteration()");
			}
			else if (event.getCode( ) == KeyCode.A){
				event.consume();
				webView.getEngine().executeScript("previousIteration()");
			}
			 else if (event.getCode() == KeyCode.D){
				event.consume();
				webView.getEngine().executeScript("nextIteration()");
			}
		});
		primaryStage.setScene(scene);
		primaryStage.show();
		Platform.runLater(webView::requestFocus);
	}
}
