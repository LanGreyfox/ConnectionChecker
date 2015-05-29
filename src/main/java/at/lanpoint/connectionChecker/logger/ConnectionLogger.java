package at.lanpoint.connectionChecker.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import org.apache.log4j.Logger;

public class ConnectionLogger {

	final static Logger logger = LogManager.getLogger();
	
	public static void info(String message){
		logger.info(message);
	}
	
	public static void info(String message, Exception e){
		logger.info(message, e);
	}
	
	public static void warning(String message){
		logger.warn(message);
	}
	
	public static void warning(String message, Exception e){
		logger.warn(message, e);
	}
	
	public static void error(String message){
		logger.error(message);
	}
	
	public static void error(String message, Exception e){
		logger.error(message, e);
	}
}
