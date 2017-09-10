package IO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.web.WebEngine;

public class Util {
	private static Object lock = new Object();
	private static WebEngine engine;
	private static Object result;
	
	static {
		@SuppressWarnings("unused")
		final JFXPanel panel = new JFXPanel();
		Platform.runLater(new Runnable() {
			public void run() {
				engine = new WebEngine();
				synchronized (lock) {
					lock.notify();
				}
			}
		});
	}

	public static Object executeJavaScript(final String eval) {
		Platform.runLater(new Runnable() {
			public void run() {
				result = engine.executeScript(eval);
				synchronized (lock) {
					lock.notify();
				}
			}
		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
			}
		}
		return result;
	}
	
	public static String downloadFileToString(String url, boolean printProgress) throws IOException {
		return downloadFileToString(new URL(url), printProgress);
	}
	
	public static String downloadFileToString(URL url, boolean printProgress) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream in = connection.getInputStream(); 
		
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1 << 14];
		int length;
		
		double sizeOfFile = in.available();
		double progress = 0;
		
		System.out.println("Downloading File...");
		while ((length = in.read(buffer)) != -1) {
		    result.write(buffer, 0, length);
		    progress += length;
		    
//		    System.out.println("Downloading");
		    
		    if(printProgress) {
//		    	System.out.println(((int)(progress / sizeOfFile)) + "%");
		    }
		}
		
		System.out.println("Finished Dowloading!");
		
		in.close();
		return result.toString("UTF-8");
	}
	
	public static String decode(String decode) {
		try {
			return URLDecoder.decode(decode, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
