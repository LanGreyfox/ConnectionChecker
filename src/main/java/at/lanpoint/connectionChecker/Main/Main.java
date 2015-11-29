package at.lanpoint.connectionChecker.Main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import at.lanpoint.connectionChecker.businesslayer.BusinessLayer;

public class Main extends Application{
	
	BusinessLayer bl = new BusinessLayer();
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		GridPane root = (GridPane)FXMLLoader.load(getClass().getResource("/View.fxml"));
		Scene scene = new Scene(root,300,95);
		scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
		primaryStage.setMaxHeight(130);
		primaryStage.setMaxWidth(300);
		primaryStage.setTitle("ConnectionChecker");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
		primaryStage.setScene(scene);	
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
			public void handle(WindowEvent event) {
				System.exit(0);
			}
		});
	}

}
