package IO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Util {
	public static String decode(String decode) {
		try {
			return URLDecoder.decode(decode, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object executeJavaScript(String eval) {
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
	     try {
	    	 return engine.eval(eval);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	    return null;
	}
	
	public static String downloadFileToString(String url, boolean printProgress) throws IOException {
		return downloadFileToString(new URL(url), printProgress);
	}
	
	public static String downloadFileToString(URL url, boolean printProgress) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream in = connection.getInputStream(); 
		
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int length;
		
		int sizeOfFile = connection.getContentLength();
		double progress = 0;
		
		System.out.println("Downloading File...");
		while ((length = in.read(buffer)) != -1) {
		    result.write(buffer, 0, length);
		    progress += length;
		    
		    System.out.println("Downloading");
		    
		    if(printProgress) 
		    	System.out.println(((int)(progress / sizeOfFile) * 100) + "%");
		}
		
		System.out.println("Finished Dowloading!");
		
		in.close();
		return result.toString("UTF-8");
	}
}
