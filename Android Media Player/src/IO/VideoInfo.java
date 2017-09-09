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
	public static String getFromFile(String path, String id, boolean downloadFile) throws IOException {
		String output;
		File file = new File(path);
		
		if(downloadFile) {
			URL getVideoInfoURL = null;
			
			try {
				getVideoInfoURL = new URL("https://www.youtube.com/get_video_info?video_id=" + id);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
			HttpURLConnection connection = (HttpURLConnection) getVideoInfoURL.openConnection();
			InputStream in = connection.getInputStream(); 
			FileOutputStream out = new FileOutputStream(file);
			
			byte[] buffer = new byte[4096];
			int read = -1;
			
			System.out.println("Downloading Get Video Info File...");
			while((read = in.read(buffer)) != -1) 
				out.write(buffer, 0, read);
			System.out.println("File Downloaded To: " + path);
			
			out.close();
			in.close();
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
	
	public static String getDirectly(String id) throws IOException {
		return Util.downloadFileToString(new URL("https://www.youtube.com/get_video_info?video_id=" + id), true);
	}
}
