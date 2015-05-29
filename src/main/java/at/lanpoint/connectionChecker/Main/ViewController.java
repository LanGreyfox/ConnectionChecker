package at.lanpoint.connectionChecker.Main;

import java.net.URL;
import java.util.ResourceBundle;

import at.lanpoint.connectionChecker.businesslayer.BusinessLayer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ViewController implements Initializable{
	
	@FXML
	private ImageView statusImage;
	
	@FXML 
	private Label count;
	
	@FXML
	private Button startButton;
	
	@FXML
	private Button stopButton;
	
	private Image notOk = new Image(getClass().getResourceAsStream("/notok.png"));
	private Image ok = new Image(getClass().getResourceAsStream("/ok.png"));

	private BusinessLayer bl = new BusinessLayer();
	private Thread thread;
	private Thread updateThread;
	private boolean execute = false;
	
	public void initialize(URL location, ResourceBundle resources) {
		statusImage.setImage(notOk);
	}
	
	@FXML
	private void startChecker(){
		bl.execute = true;
		
		thread = new Thread(bl);
		thread.start();
		
		updateThread = new Thread(updateTask);
		updateThread.start();
		
		Platform.runLater(new Runnable(){

			public void run() {
				statusImage.setImage(ok);
				startButton.setDisable(true);
			}
			
		});
		execute = true;
	}
	
	@FXML
	private void stopChecker(){
		bl.execute = false;
		Platform.runLater(new Runnable(){

			public void run() {
				statusImage.setImage(notOk);
				startButton.setDisable(false);
			}
			
		});
		execute = false;
	}
	
	Task<Void> updateTask = new Task<Void>(){

		@Override
		protected Void call() throws Exception {
			
			while(true){
				if(execute){
					try{
						Platform.runLater(new Runnable(){
							public void run() {
								count.textProperty().setValue(bl.count.toString());
								if(bl.error){
									statusImage.setImage(notOk);
								}
								else{
									statusImage.setImage(ok);
								}
							}
						});
						Thread.sleep(1000);
					}catch(Exception e){
						e.printStackTrace();
					}
				}else{
					try{
						Thread.sleep(1000);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	};
}
