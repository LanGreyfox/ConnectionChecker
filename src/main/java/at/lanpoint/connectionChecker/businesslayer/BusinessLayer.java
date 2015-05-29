package at.lanpoint.connectionChecker.businesslayer;

import at.lanpoint.connectionChecker.connection.ConnectionCheck;
import at.lanpoint.connectionChecker.connection.IConnectionCheck;
import at.lanpoint.connectionChecker.logger.ConnectionLogger;

public class BusinessLayer implements Runnable {
	
	private IConnectionCheck connectionCheck = new ConnectionCheck();
	public boolean execute = true;
	public boolean error = false;
	public Integer count = 0;

	public void run() {
		count = 0;
		
		while(execute){
			if(connectionCheck.checkIfURLExists("http://www.google.com/")){
				ConnectionLogger.info("Ping ok");
				error = false;
			}
			else{
				ConnectionLogger.warning("Internet connection problem!");
				error = true;
				count++;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}	
	}
}
