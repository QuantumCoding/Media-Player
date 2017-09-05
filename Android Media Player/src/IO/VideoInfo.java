package IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class VideoInfo {
	public static String getFromFile(String path, String id, boolean downloadFile) {
		String output = null;
		File file = new File(path);
		
		if(downloadFile) {
			URL getVideoInfoURL = null;
			
			try {
				getVideoInfoURL = new URL("https://www.youtube.com/get_video_info?video_id=" + id);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
			HttpURLConnection connection = null;
			InputStream in = null; 
			FileOutputStream out = null;
			
			try {
				connection = (HttpURLConnection) getVideoInfoURL.openConnection();
				in = connection.getInputStream();
				out = new FileOutputStream(file);
			} catch (IOException e) {
				e.printStackTrace();
			}	
			
			byte[] buffer = new byte[4096];
			int read = -1;
			
			try {
				read = in.read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("Downloading Get Video Info File...");
			while(read != -1) {
				try {
					out.write(buffer, 0, read);
					read = in.read(buffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("File Downloaded To: " + path);
		}
		
		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		StringBuilder builder = new StringBuilder();
		
		System.out.println("Reading File...");
		while(sc.hasNext()) 
			builder.append(sc.nextLine());
		sc.close();
		
		System.out.println("File Read");
		
		output = builder.toString();
		
		return output;
	}
	
	public static String getDirectly(String id) {
		String output = null;
		
		URL getVideoInfoURL = null;
		
		try {
			getVideoInfoURL = new URL("https://www.youtube.com/get_video_info?video_id=" + id);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		HttpURLConnection connection = null;
		
		try {
			connection = (HttpURLConnection) getVideoInfoURL.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		InputStream in = null; 
		
		try {
			in = connection.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int read = -1;
		byte[] buffer = new byte[4096];
		
		try {
			read = in.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Downloading Video Info Directly...");
		while(read != -1) {
			for(int i = 0; i < read; i++) 
				output += Character.toString((char) buffer[i]);
			
			try {
				read = in.read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return output;
	}
}
