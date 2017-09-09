package IO;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignatureDecoder {
/*
	1. Dowload html for video
		http://www.youtube.com/watch?v=<id>
	
	2. Find script tag with
		Not Java = src=\"([^\"]*player-[^\"]*\\/[^\"]*\\.js)\"
		Java = src=\"([^\"]*player-[^\"]*\\/[^\"]*\\.js)\"
		
	3. Download Javascript File
	
	4. Search for .set("signature",
		Not Java Regex: \.set\("signature",([\w\d\$_]+)\(.+?\)\)
		Java Regex: \\.set\\(\"signature\",([\\w\\d\\$_]+)\\(.+?\\)\\)
		
		Gives method name to decode
	
	5. Get function argument (At start)
		Not Java Regex: function\(([\w\d_]+)\)
		Java Regex: function\\(([\\w\\d_]+)\\)	
		
	6. Add code to end of Javascript File
		after second to last semi colon
		Add (step 5).decode=(step 4 name);
		Very end: _yt_player.decode("(signature)")
		
	7. Submit to Javascript engine (returns decoded signature)
*/
	
	public static void decode(String id, String signature) throws IOException {
		String html = Util.downloadFileToString("https://www.youtube.com/watch?v=" + id, false);
		
		Pattern link = Pattern.compile("src=\"([^\"]*player-[^\"]*\\/[^\"]*\\.js)\"");
		Matcher m = link.matcher(html);
		 
		String javascriptFile = null;
		if(m.find()) {
			javascriptFile = Util.downloadFileToString("https://www.youtube.com" + m.group(1), false);
		}
		
		Pattern method = Pattern.compile("\\.set\\(\"signature\",([\\w\\d\\$_]+)\\(.+?\\)\\)");
		Matcher match = method.matcher(javascriptFile);
		
		String methodName = null;
		if(match.find()) {
			methodName = match.group(1);
		}
	}
}
