package IO;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	private static String justS = "Deext6875ZI", regular = "LSMJcFiOjBc", fail = "3tmd-ClpJxA";
	
	/*
	 * If fails - pull adaptive and fmts straight outta the html
	 * Regex:
	 * 
	 * \"adaptive_fmts\":\\s*\"([^\"]*)\"
	 * "adaptive_fmts":\s*"([^"]*)"
	 * 
	 * \"url_encoded_fmt_stream_map\":\"([^\"]*)\"
	 * 
	 * remove "
	 * split at : (gets rid of adaptive =) left with everything else
	 * split at commas (gets list of video details)
	 * have each split at \u0026 
	 * Same as the rest from there
	 * 
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
		
		String adaptive = null;  
		String mapStream = null; 
		String title = null;     
		String author = null;    
		
		boolean decodeAdaptive = true, decodeMap = true;
		
		String reason = firstMap.get("reason");
		if(reason != null) {
			String html = Util.downloadFileToString("https://www.youtube.com/watch?v=" + id, false);
			
			Pattern method = Pattern.compile("\"adaptive_fmts\":\\s*\"([^\"]*)\"");
			Matcher match = method.matcher(html);
			if(match.find()) {
				adaptive = match.group(1);
				adaptive.replaceAll("\"", "");
				adaptive = Util.removeUTFCharacters(adaptive).toString();
//				System.out.println(adaptive);
			}
			
			method = Pattern.compile("\"url_encoded_fmt_stream_map\":\"([^\"]*)\"");
			match = method.matcher(html);
			if(match.find()) {
				mapStream = match.group(1);
				mapStream.replaceAll("\"", "");
				mapStream = Util.removeUTFCharacters(mapStream).toString();
				decodeMap = false;
				System.out.println(mapStream);
			}

			method = Pattern.compile("\"title\":\"([^\"]*)\"");
			match = method.matcher(html);
			if(match.find()) {
				title = match.group(1);
			}
			
			
			method = Pattern.compile("\"author\":\"([^\"]*)\"");
			match = method.matcher(html);
			if(match.find()) {
				author = match.group(1);
			}
		} else {
			adaptive = firstMap.get("adaptive_fmts");                                                    
			mapStream = firstMap.get("url_encoded_fmt_stream_map");                                      
			title = firstMap.get("title") != null ? Util.decode(firstMap.get("title")) : "Default Title";
			author = firstMap.get("author") != null ? Util.decode(firstMap.get("author")) : null;        
		}
		
		
		title = title.replaceAll("[\\\\\\/\\:\\*\\?\\\"\\<\\>\\|]", " ");
		
		System.out.println("------------");
		System.out.println("Author: " + author);
		System.out.println("Title: " + title);
		System.out.println("------------");
		
		if(decodeAdaptive)
			adaptive = Util.decode(adaptive);
		if(decodeMap)
			mapStream = Util.decode(mapStream);
		
		String[] decodedQualityInfo = (adaptive + "," + mapStream).split(",");
		
//		for(String s : decodedQualityInfo) {
//			System.out.println(s);
//		}
		
		boolean loadedJavaScript = false;
		for(String s : decodedQualityInfo) {
			if(s != null) {
				String[] temp = s.split("&");
				
				if(temp.length > 1) {
					QualityInfo qualityInfo = new QualityInfo(temp);
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
		}
	}
	
	public static void main(String[] args) throws IOException {
		new Test("_PBlykN4KIY");
		System.exit(0);
	}
}
