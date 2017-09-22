package IO;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import Interfaces.IDownloadController;
import Interfaces.IDownloadInfo;
import Interfaces.IVideoFormat;

public class DownloadInfo implements IDownloadInfo{

	private String name;
	private Image thumbnail;
	private IVideoFormat[] formats;
	
	public DownloadInfo(String id) throws IOException {
		HashMap<String, String> infoMap = getVideoInfoMap(id);
		HashMap<String, String> results = extractInfo(infoMap, id);

		name = results.get("title");
		formats = getQualities(results.get("adaptive"), results.get("mapstream"), id).toArray(new QualityInfo[0]);
		thumbnail = ImageIO.read(new URL(results.get("thumbnail_url")));
	}
	
	private HashMap<String, String> getVideoInfoMap(String id) throws IOException {
		String output = VideoInfo.getDirectly(id);

		HashMap<String, String> firstMap = new HashMap<>();
		String[] firstANDSplit = output.split("&");
		for(String s : firstANDSplit) {
			String[] equalSignSplit = s.split("=", 2);
			if(equalSignSplit.length == 2) {
				firstMap.put(equalSignSplit[0], equalSignSplit[1]);
			}
		}
		
		return firstMap;
	}
	
	
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
	private HashMap<String, String> extractInfo(HashMap<String, String> infoMap, String id) throws IOException {
		HashMap<String, String> results = new HashMap<>();
		
		String reason = infoMap.get("reason");
		if(reason != null) {
			String html = Util.downloadFileToString("https://www.youtube.com/watch?v=" + id, false);
			
			Pattern method = Pattern.compile("\"adaptive_fmts\":\\s*\"([^\"]*)\"");
			Matcher match = method.matcher(html);
			if(match.find()) {
				String adaptive = match.group(1);
				adaptive.replaceAll("\"", "");
				adaptive = Util.removeUTFCharacters(adaptive).toString();
				
				results.put("adaptive", adaptive);
			}
			
			method = Pattern.compile("\"url_encoded_fmt_stream_map\":\"([^\"]*)\"");
			match = method.matcher(html);
			if(match.find()) {
				String mapStream = match.group(1);
				mapStream.replaceAll("\"", "");
				mapStream = Util.removeUTFCharacters(mapStream).toString();
				
				results.put("mapstream", mapStream);
			}

			method = Pattern.compile("\"title\":\"([^\"]*)\"");
			match = method.matcher(html);
			if(match.find()) {
				results.put("title", match.group(1));
			}
			
			method = Pattern.compile("\"author\":\"([^\"]*)\"");
			match = method.matcher(html);
			if(match.find()) {
				results.put("author", match.group(1));
			}
			
			method = Pattern.compile("\"thumbnail_url\":\"([^\"]*)\"");
			match = method.matcher(html);
			if(match.find()) {
				results.put("thumbnail_url", match.group(1));
			}
		} else {
			results.put("adaptive", infoMap.get("adaptive_fmts"));                                                    
			results.put("mapstream", infoMap.get("url_encoded_fmt_stream_map"));        
			results.put("thumbnail_url", infoMap.get("thumbnail_url"));
			results.put("title", infoMap.get("title") != null ? Util.decode(infoMap.get("title")) : "Default Title");
			results.put("author", infoMap.get("author") != null ? Util.decode(infoMap.get("author")) : null);        
		}
		
		results.put("title", results.get("title").replaceAll("[\\\\\\/\\:\\*\\?\\\"\\<\\>\\|]", " "));
		
		return results;
	}
	
	private ArrayList<QualityInfo> getQualities(String adaptive, String mapstream, String id) throws IOException {
		String[] decodedQualityInfo = (adaptive + "," + mapstream).split(",");
		
		ArrayList<QualityInfo> infos = new ArrayList<>();
		
		boolean loadedJavaScript = false;
		for(String s : decodedQualityInfo) {
			if(s != null) {
				String[] temp = s.split("&");
				
				if(temp.length > 1) {
					QualityInfo qualityInfo = new QualityInfo(temp);

					try {
						qualityInfo.getFormatName();
					} catch (IllegalArgumentException e) {
						continue;
					}
					
					infos.add(qualityInfo);
					
					if(qualityInfo.getInfo().containsKey("s")) {
						if(!loadedJavaScript) {
							SignatureDecoder.loadJavaScript(id);
							loadedJavaScript  = true;
						}
						
						qualityInfo.getInfo().put("url", qualityInfo.get("url") + "&signature=" + SignatureDecoder.decode(id, qualityInfo.get("s")));
					}
				}
			}
		}	 
		
		return infos;
	}
	
	@Override
	public Image getThumbnail() {
		return thumbnail;
	}

	@Override
	public String getSongName() {
		return name;
	}

	@Override
	public IVideoFormat[] getFormats() {
		return formats;
	}

	@Override
	public IDownloadController download(IVideoFormat formate, String saveTo) {
		return null;
	}
}
