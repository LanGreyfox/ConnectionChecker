package at.lanpoint.connectionChecker.businesslayer;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import at.lanpoint.connectionChecker.connection.ConnectionCheck;
import at.lanpoint.connectionChecker.connection.IConnectionCheck;
import at.lanpoint.connectionChecker.logger.ConnectionLogger;

public class BusinessLayer implements Runnable {
	
	private IConnectionCheck connectionCheck = new ConnectionCheck();
	private Map<Integer, Integer> errorsDuringDay = new HashMap<Integer, Integer>(); //map records number of errors for each hour
	public boolean execute = true;
	public boolean error = false;
	public Integer count = 0;
	
	public BusinessLayer(){
		initialize();
	}
	
	/**
	 * initializes errors of Day
	 */
	private void initialize(){
		for(int i = 1; i < 25; i++){
			errorsDuringDay.put(i, 0);
		}
	}
	
	public void run() {
		count = 0;
		
		while(execute){
			if(connectionCheck.checkIfURLExists("http://www.google.com/")){ 
				ConnectionLogger.info("Ping ok");
				error = false;
			}
			else{ //no internet connection
				ConnectionLogger.warning("Internet connection problem!");
				error = true;
				count++;
				
				//raise error on hour
				int hour = LocalDateTime.now().getHour();
				if(errorsDuringDay.containsKey(hour)){
					int errorsOfHour = errorsDuringDay.get(hour);
					errorsOfHour++;
					errorsDuringDay.put(hour, errorsOfHour);
				}
				else{
					errorsDuringDay.put(hour, 1);
				}
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}	
	}
	
	public Map<Integer, Integer> getErrorsDuringDay() {
		return errorsDuringDay;
	}
}
