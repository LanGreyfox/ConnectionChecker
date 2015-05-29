package at.lanpoint.connectionChecker.connection;

public interface IConnectionCheck {
	
	/**
	 * checks if a url exists and is avaiable
	 * @param targetUrl
	 * @return
	 */
	public boolean checkIfURLExists(String targetUrl);
	
}
