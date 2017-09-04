package IO;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class QualityInfo {

	private String url;
	private String type, typeExtension;
	private int itag;
	
	public QualityInfo(String[] info) {
		for(String s : info) {
			if(s.startsWith("itag=")) {
				itag = Integer.valueOf(s.substring(s.indexOf("=") + 1));
			} else if(s.startsWith("url=")) {
				try {
					url = URLDecoder.decode(s.substring(s.indexOf("=") + 1), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else if(s.startsWith("type=")) {
				String type = null;
				try {
					type = URLDecoder.decode(s.substring(s.indexOf("=") + 1), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				typeExtension = type.substring(type.indexOf("/") + 1, type.indexOf(";"));
				this.type = type.substring(0, type.indexOf("/"));
			}
			
			if(itag != -1 && url != null && type != null) 
				break;
		}
	}

	public String toString() {
		return "Itag: " + itag + "\nType: " + type + "\nTypeExtension: " + typeExtension + "\nURL: " + url;
	}
	
	public String getUrl(){return url;}
	public String getType(){return type;}
	public String getTypeExtension(){return typeExtension;}
	public int getItag(){return itag;}
}
