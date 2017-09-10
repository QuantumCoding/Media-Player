package IO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	private static String justS = "Deext6875ZI", regular = "LSMJcFiOjBc", fail = "3tmd-ClpJxA";
	
	public Test(String id) throws IOException {
//		String output = VideoInfo.getFromFile("C:\\Users\\Sam\\Desktop\\get_video_info", id, true);
		String output = VideoInfo.getDirectly(id);

		HashMap<String, String> firstMap = new HashMap<>();
		String[] firstANDSplit = output.split("&");
		for(String s : firstANDSplit) {
			String[] equalSignSplit = s.split("=", 2);
			if(equalSignSplit.length == 2) {
				firstMap.put(equalSignSplit[0], equalSignSplit[1]);
			}
		}
		
		for(String s : firstMap.keySet()) {
			System.out.println(s + ", " + firstMap.get(s));
		}
		
		String reason = firstMap.get("reason");
		if(reason != null) {
			System.out.println("");
			System.out.println("Welp, don't got access to that one");
			System.err.println(reason);
			return;
		}
		
		String adaptive = firstMap.get("adaptive_fmts");
		String mapStream = firstMap.get("url_encoded_fmt_stream_map");
		String title = Util.decode(firstMap.get("title") == null ? "No Title" : firstMap.get("title"));
		String author = Util.decode(firstMap.get("author"));
		
//		title = title.replaceAll("[^a-zA-Z0-9.-]", " ");
		
		System.out.println("------------");
		System.out.println("Author: " + author);
		System.out.println("Title: " + title);
		System.out.println("Reason: " + reason);
		System.out.println("------------");
		
		adaptive = Util.decode(adaptive);
		mapStream = Util.decode(mapStream);
		
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
		
		boolean loadedJavaScript = false;
		for(String s : decodedQualityInfo) {
			QualityInfo qualityInfo = new QualityInfo(s.split("&"));
			System.out.println(qualityInfo);
			System.out.println("------------");
			
			if(qualityInfo.getInfo().containsKey("s")) {
				if(!loadedJavaScript) {
					SignatureDecoder.loadJavaScript(id);
					loadedJavaScript  = true;
				}
				
				qualityInfo.getInfo().put("url", qualityInfo.getUrl() + "&signature=" + SignatureDecoder.decode(id, qualityInfo.getInfo().get("s")));
			}
			
			if(qualityInfo.getItag() == 22) {
				File file = new File("C:\\Users\\samse\\Desktop\\" + title + ".mp4");
				
				HttpURLConnection connection = (HttpURLConnection) new URL(qualityInfo.getInfo().get("url")).openConnection();
				InputStream in = connection.getInputStream();
				FileOutputStream out = new FileOutputStream(file);
				
				double length = connection.getContentLength();
				int num = 0;
				
				byte[] buffer = new byte[4096];
				int read = -1;
				
				while((read = in.read(buffer)) != -1) {
					num += read;
					out.write(buffer, 0, read);
					
					System.out.println((num / length) * 100.0);
				}
				
				System.out.println("Got Video!");
				
				out.close();
				in.close();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		new Test(justS);
		System.exit(0);
	}
}
