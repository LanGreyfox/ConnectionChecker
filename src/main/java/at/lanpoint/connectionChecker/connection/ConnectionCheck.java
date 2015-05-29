package at.lanpoint.connectionChecker.connection;

import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionCheck implements IConnectionCheck {

	public boolean checkIfURLExists(String targetUrl) {
		HttpURLConnection httpUrlConn;
		try {
			httpUrlConn = (HttpURLConnection) new URL(targetUrl).openConnection();

			// A HEAD request is just like a GET request, except that it asks
			// the server to return the response headers only, and not the
			// actual resource (i.e. no message body).
			// This is useful to check characteristics of a resource without
			// actually downloading it,thus saving bandwidth. Use HEAD when
			// you don't actually need a file's contents.
			httpUrlConn.setRequestMethod("HEAD");

			// Set timeouts in milliseconds
			httpUrlConn.setConnectTimeout(5000);
			httpUrlConn.setReadTimeout(5000);

			return (httpUrlConn.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			return false;
		}
	}
}
