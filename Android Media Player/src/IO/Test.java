package IO;

import java.io.IOException;
import java.util.HashMap;

public class Test {

	private static String justS = "Deext6875ZI", regular = "LSMJcFiOjBc", fail = "3tmd-ClpJxA";
	
	/*
	 * If fails - pull adaptive and fmts straight outta the html
	 * Regex:
	 * 
	 * \"adaptive_fmts\":\\s*\"([^\"]*)\"
	 * \"url_encoded_fmt_stream_map\":\"([^\"]*)\"
	 */
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
		
//		for(String s : firstMap.keySet()) {
//			System.out.println(s + ", " + firstMap.get(s));
//		}
		
		String reason = firstMap.get("reason");
		if(reason != null) {
			for(String s : firstMap.keySet())
				System.err.println(s + ", " + firstMap.get(s));
			return;
		}
		
		String adaptive = firstMap.get("adaptive_fmts");
		String mapStream = firstMap.get("url_encoded_fmt_stream_map");
		String title = firstMap.get("title") != null ? Util.decode(firstMap.get("title")) : "Default Title";
		String author = firstMap.get("author") != null ? Util.decode(firstMap.get("author")) : null;
		
		title = title.replaceAll("[\\\\\\/\\:\\*\\?\\\"\\<\\>\\|]", " ");
		
		System.out.println("------------");
		System.out.println("Author: " + author);
		System.out.println("Title: " + title);
		System.out.println("Reason: " + reason);
		System.out.println("------------");
		
		adaptive = Util.decode(adaptive);
		mapStream = Util.decode(mapStream);
		
		String[] decodedQualityInfo = (adaptive + "," + mapStream).split(",");
		
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
				
				qualityInfo.getInfo().put("url", qualityInfo.get("url") + "&signature=" + SignatureDecoder.decode(id, qualityInfo.get("s")));
			}
			
			if(qualityInfo.getItag() == 22) {
				Util.downloadFile(qualityInfo.get("url"), System.getProperty("user.home") + "/Desktop/" + title + "." + qualityInfo.getTypeExtension(), true);
				
				break;
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		new Test("L8laWhgRRA8");
		System.exit(0);
	}
}
