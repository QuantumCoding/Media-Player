package IO;

import java.util.HashMap;

public class QualityInfo {

	private String url;
	private String type, typeExtension;
	private HashMap<String, String> info;
	private int itag;
	
	public QualityInfo(String[] info) {
		this.info = new HashMap<>();
		
		for(String s : info) {
			String[] temp = s.split("=", 2);
			if(temp.length == 2) 
				this.info.put(temp[0], temp[1]);
		}

		itag = Integer.valueOf(this.info.get("itag"));
		type = Util.decode(this.info.get("type"));
		url = Util.decode(this.info.get("url"));
		
		typeExtension = type.substring(type.indexOf("/") + 1, type.indexOf(";"));
		this.type = type.substring(0, type.indexOf("/"));
	}

	public String toString() {
		return "Itag: " + itag + "\nType: " + type + "\nTypeExtension: " + typeExtension + "\nURL: " + url;
	}
	
	public HashMap<String, String> getInfo(){return info;}
	public String getUrl(){return url;}
	public String getType(){return type;}
	public String getTypeExtension(){return typeExtension;}
	public int getItag(){return itag;}
}
