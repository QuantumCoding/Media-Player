package IO;

import java.util.HashMap;

import Interfaces.IVideoFormat;

public class QualityInfo extends IVideoFormat {

	private HashMap<String, String> info;
	private int itag;
	private String typeExtension;
	
	public QualityInfo(String[] info) {
		this.info = new HashMap<>();
		
		for(String s : info) {
			String[] temp = s.split("=", 2);
			if(temp.length == 2) 
				this.info.put(temp[0], temp[1]);
		}

		itag = Integer.valueOf(this.info.get("itag"));
		
		String type = Util.decode(this.info.get("type"));
		
		this.info.put("url", Util.decode(this.info.get("url")));
		
		typeExtension = type.substring(type.indexOf("/") + 1, type.indexOf(";"));
		
		this.info.put("type", type.substring(0, type.indexOf("/")));
	}

	public String toString() {
		return "Itag: " + itag + "\nType: " + info.get("type") + "\nTypeExtension: " + typeExtension + "\nURL: " + info.get("url");
	}
	
	public HashMap<String, String> getInfo(){return info;}
	public String getTypeExtension(){return typeExtension;}
	public String get(String key) {return info.get(key);}
	
	public int getItag(){return itag;}

	@Override
	public String getFileExt() {
		return typeExtension;
	}

	@Override
	public String getFormatName() {
		return Util.VIDEO_ITAGS.lookup(itag).toString().replace("_", " ").substring(1);
	}
}
