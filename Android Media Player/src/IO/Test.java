package IO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;

public class Test {

	public Test(String id) {
		String output = VideoInfo.getFromFile("C:\\Users\\Sam\\Desktop\\get_video_info", id, true);
//		String output = VideoInfo.getDirectly(id);

		HashMap<String, String> firstMap = new HashMap<>();
		String[] firstANDSplit = output.split("&");
		for(String s : firstANDSplit) {
			String[] equalSignSplit = s.split("=", 2);
			if(equalSignSplit.length == 2) {
				firstMap.put(equalSignSplit[0], equalSignSplit[1]);
			}
		}
		
//		for(String s : firstMap.keySet()) {
//			System.out.println(s + ", " + firstMap.get(s));
//		}
		String status = firstMap.get("status");
		if(status.equals("fail")) {
			System.err.println("Welp, don't got access to that one");
			return;
		}
		
		String adaptive = firstMap.get("adaptive_fmts");
		String mapStream = firstMap.get("url_encoded_fmt_stream_map");
		String title = Util.decode(firstMap.get("title"));
		
		title = title.replaceAll("[^a-zA-Z0-9.-]", " ");
		
		System.out.println("------------");
		System.out.println("Title: " + title);
		System.out.println("Status: " + status);
		System.out.println("------------");
		
		try {
			adaptive = URLDecoder.decode(adaptive, "UTF-8");
			mapStream = URLDecoder.decode(mapStream, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String[] decodedQualityInfo = (adaptive + "," + mapStream).split(",");
//		for(String s : decodedQualityInfo) {
//			System.out.println(s);
//		}
		
//		System.out.println("------------");
//		String[] temp = decodedQualityInfo.clone();
//		
//		for(String s : temp) { 
//			for(String info : s.split("&")) {
//				System.out.println(info);
//			}
//			System.out.println("");
//		}
//		
//		System.out.println("------------");
		
		for(String s : decodedQualityInfo) {
			QualityInfo qualityInfo = new QualityInfo(s.split("&"));
			System.out.println(qualityInfo);
			System.out.println("------------");
			if(qualityInfo.getInfo().containsKey("s")) {
				System.err.println("Welp, don't got access to that one");
				return;
			}
			
//			if(qualityInfo.getItag() == 22) {
//				File file = new File("C:\\Users\\Sam\\Desktop\\" + title + ".mp4");
//				
//				HttpURLConnection connection = null;
//				InputStream in = null;
//				FileOutputStream out = null;
//				
//				try {
//					connection = (HttpURLConnection) new URL(qualityInfo.getUrl()).openConnection();
//					in = connection.getInputStream();
//					out = new FileOutputStream(file);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				
//				double length = connection.getContentLength();
//				int num = 0;
//				
//				byte[] buffer = new byte[4096];
//				int read = -1;
//				
//				try {
//					read = in.read(buffer);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//				while(read != -1) {
//					try {
//						num += read;
//						out.write(buffer, 0, read);
//						read = in.read(buffer);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					
//					System.out.println((num / length) * 100.0);
//				}
//				
//				System.out.println("Got Video!");
//				
//				try {
//					out.close();
//					in.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
		}
	}
	
	public static void main(String[] args) {
		new Test("Deext6875ZI");	
	}
}
