package at.lanpoint.connectionChecker.Main;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.stage.WindowEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;

import at.lanpoint.connectionChecker.businesslayer.BusinessLayer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.EventHandler;

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
	private Stage controllerStage = null;
	private TrayIcon icon = null;
	private Boolean alreadyNotificatedOffline = false;
	private Boolean alreadyNotificatedOnline = false;
	
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
									//notification
									if(!alreadyNotificatedOffline){
										alreadyNotificatedOffline = true;
										icon.displayMessage("ConnectionChecker", "Achtung der Computer ist offline!", TrayIcon.MessageType.WARNING);
										alreadyNotificatedOnline = false;
									}
								}
								else{
									statusImage.setImage(ok);
									//notification
									if(!alreadyNotificatedOnline){
										alreadyNotificatedOnline = true;
										icon.displayMessage("ConnectionChecker", "Der Computer ist online!", TrayIcon.MessageType.INFO);
										alreadyNotificatedOffline = false;
									}
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
	
	public void setStage(Stage stage){
		this.controllerStage = stage;
		
		if(SystemTray.isSupported()){
			Platform.setImplicitExit(false);
			setTrayIcon(controllerStage);
		}else{
		
			stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
				public void handle(WindowEvent event) {
					System.exit(0);
				}
			});
		}
	}
	
	private void setTrayIcon(final Stage primaryStage){
		SystemTray systemTray = SystemTray.getSystemTray();
		URL url = getClass().getResource("/icon_small.png");
		java.awt.Image simage = null;
		try{
			simage = ImageIO.read(url);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//set action listers for popup menue
		ActionListener listenerShow = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						primaryStage.show();
					}
				});
			}
		};

		ActionListener listenerClose = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {
				primaryStage.hide();
			}
		});

		PopupMenu popup = new PopupMenu();
		MenuItem showItem = new MenuItem("Öffnen");
		MenuItem exitItem = new MenuItem("Beenden");

		showItem.addActionListener(listenerShow);
		exitItem.addActionListener(listenerClose);

		popup.add(showItem);
		popup.add(exitItem);

		icon = new TrayIcon(simage, "ConnectionChecker", popup);

		try {
			systemTray.add(icon);
		}
		catch (Exception e) {
			System.err.println(e);
		}

	}
}
