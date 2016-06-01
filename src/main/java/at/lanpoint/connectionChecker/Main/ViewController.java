package at.lanpoint.connectionChecker.Main;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;

import at.lanpoint.connectionChecker.businesslayer.BusinessLayer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.EventHandler;

public class ViewController implements Initializable {

	@FXML
	private ImageView statusImage;

	@FXML
	private Label count;

	@FXML
	private Button startButton;

	@FXML
	private Button stopButton;

	@FXML
	private Button graphButton;

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
	private Map<Integer, Integer> errorsDuringDay = new HashMap<Integer, Integer>();

	public void initialize(URL location, ResourceBundle resources) {
		statusImage.setImage(notOk);
	}

	@FXML
	private void startChecker() {
		bl.execute = true;

		thread = new Thread(bl);
		thread.start();

		updateThread = new Thread(updateTask);
		updateThread.start();

		Platform.runLater(new Runnable() {

			public void run() {
				statusImage.setImage(ok);
				startButton.setDisable(true);
			}

		});
		execute = true;
	}

	@FXML
	private void stopChecker() {
		bl.execute = false;
		Platform.runLater(new Runnable() {

			public void run() {
				statusImage.setImage(notOk);
				startButton.setDisable(false);
			}

		});
		execute = false;
	}

	@FXML
	private void showChart() {
		errorsDuringDay = bl.getErrorsDuringDay();
		Stage stage = new Stage();
		stage.setTitle("Anzahl der Fehler pro Stunde");
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

		final NumberAxis xAxis = new NumberAxis(0, 23, 1);
		final NumberAxis yAxis = new NumberAxis();
		
		yAxis.setTickLabelFormatter(new IntegerStringConverter());

		final AreaChart<Number, Number> ac = new AreaChart<Number, Number>(xAxis, yAxis);
		ac.setTitle("Fehler pro Stunde");

		XYChart.Series seriesErrors = new XYChart.Series();
		seriesErrors.setName("Anzahl der Fehler");

		for (int i = 0; i <= 23; i++) {
			if (errorsDuringDay.size() >= 23) {
				seriesErrors.getData().add(new XYChart.Data(i, errorsDuringDay.get(i)));
			} else {
				seriesErrors.getData().add(new XYChart.Data(i, 0));
			}
		}

		Scene scene = new Scene(ac, 800, 600);
		ac.getData().addAll(seriesErrors);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * task which updates all gui variables
	 */
	Task<Void> updateTask = new Task<Void>() {

		@Override
		protected Void call() throws Exception {

			while (true) {
				if (execute) {
					try {
						Platform.runLater(new Runnable() {
							public void run() {
								count.textProperty().setValue(bl.count.toString());
								if (bl.error) {
									statusImage.setImage(notOk);
									// notification
									if (!alreadyNotificatedOffline) {
										alreadyNotificatedOffline = true;
										icon.displayMessage("ConnectionChecker", "Achtung der Computer ist offline!",
												TrayIcon.MessageType.WARNING);
										alreadyNotificatedOnline = false;
									}
								} else {
									statusImage.setImage(ok);
									// notification
									if (!alreadyNotificatedOnline) {
										alreadyNotificatedOnline = true;
										icon.displayMessage("ConnectionChecker", "Der Computer ist online!",
												TrayIcon.MessageType.INFO);
										alreadyNotificatedOffline = false;
									}
								}

								errorsDuringDay = bl.getErrorsDuringDay();
							}
						});
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	/**
	 * register application response on close request
	 * 
	 * @param stage
	 */
	public void setStage(Stage stage) {
		this.controllerStage = stage;

		if (SystemTray.isSupported()) {
			Platform.setImplicitExit(false);
			setTrayIcon(controllerStage);
		} else {

			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent event) {
					System.exit(0);
				}
			});
		}
	}

	/**
	 * sets tray icon of application
	 * 
	 * @param primaryStage
	 */
	private void setTrayIcon(final Stage primaryStage) {
		SystemTray systemTray = SystemTray.getSystemTray();
		URL url = getClass().getResource("/icon_small.png");
		java.awt.Image simage = null;
		try {
			simage = ImageIO.read(url);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// set action listers for popup menue
		ActionListener listenerShow = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Platform.runLater(new Runnable() {

					public void run() {
						primaryStage.show();
					}
				});
			}
		};

		ActionListener listenerClose = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

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
		} catch (Exception e) {
			System.err.println(e);
		}

	}

	class IntegerStringConverter extends StringConverter<Number> {
		public IntegerStringConverter() {
		}

		@Override
		public String toString(Number object) {
			if (object.intValue() != object.doubleValue())
				return "";
			return "" + (object.intValue());
		}

		@Override
		public Number fromString(String string) {
			Number val = Double.parseDouble(string);
			return val.intValue();
		}
	}
}
