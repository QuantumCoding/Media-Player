package IO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.web.WebEngine;

public class Util {
	private static Object lock = new Object();
	private static WebEngine engine;
	private static Object result;
	
	public static enum VIDEO_ITAGS { 
		_MP4_720P_AAC_192KBPS(22),
		_WEBM_360P_VORBIS_128KBPS(43),
		_MP4_360p_AAC_96KBPS(18),
		_3GP_240P_AAC_32KBPS(36),
		_3GP_144P_AAC_24KBPS(17),
		_AUDIO_ONLY_WEBM_160KBPS(160),
		_AUDIO_ONLY_M4A_128KBPS(140),
		_AUDIO_ONLY_WEBM_128KBPS(171),
		_AUDIO_ONLY_WEBM_64KBPS(250),
		_AUDIO_ONLY_M4A_48KBPS(139),
		_AUDIO_ONLY_WEBM_48KBPS(249);
		
		private int number;
		private VIDEO_ITAGS(int number){this.number = number;}
		public int getNumber(){return number;}
		
		public static VIDEO_ITAGS lookup(int itag) {
			for(VIDEO_ITAGS tag : values()) {
				if(tag.number == itag)
					return tag;
			}
			
			throw new IllegalArgumentException(itag + " Is not a valid itag");
		}
	}
	
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
		int read;
		
		double sizeOfFile = in.available();
		double progress = 0;
		
//		System.out.println("Downloading File...");
		while ((read = in.read(buffer)) != -1) {
		    result.write(buffer, 0, read);
		    progress += read;
		    
		    if(printProgress) 
		    	System.out.println(((int)(progress / sizeOfFile)) + "%");
		}
		
//		System.out.println("Finished Dowloading!");
		
		in.close();
		connection.disconnect();
		return result.toString("UTF-8");
	}
	
	public static void downloadFile(String url, String location, boolean printProgress) throws IOException {
		downloadFile(new URL(url), new File(location), printProgress);
	}
	
	public static void downloadFile(URL url, File file, boolean printProgress) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream in = connection.getInputStream();
		FileOutputStream out = new FileOutputStream(file);

		byte[] buffer = new byte[1 << 14];
		int read;
		
		double sizeOfFile = connection.getContentLength();
		double progress = 0;
		
		System.out.println("Downloading File...");
		long start = System.currentTimeMillis();
		while((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
			progress += read;
			
			if(printProgress)
				System.out.println((int)((progress / sizeOfFile) * 100)+ "%");
		}
		
		System.out.println("Finished Downloading!");
		System.err.println("Time To Download: " + ((System.currentTimeMillis() - start) / 1000.0) + " Seconds");
		
		out.close();
		in.close();
		connection.disconnect();
	}
	
	public static String decode(String decode) {
		try {
			return URLDecoder.decode(decode, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	public static StringBuffer removeUTFCharacters(String data){
		Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
		Matcher m = p.matcher(data);
		StringBuffer buf = new StringBuffer(data.length());
	
		while (m.find()) {
			String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
			m.appendReplacement(buf, Matcher.quoteReplacement(ch));
		}
		m.appendTail(buf);
		
		return buf;
	}
}
