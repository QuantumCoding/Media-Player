package IO;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
	private static String javascript;
	
	public static void loadJavaScript(String id) throws IOException {
		String html = Util.downloadFileToString("https://www.youtube.com/watch?v=" + id, false);
		
		Pattern link = Pattern.compile("src=\"([^\"]*player-[^\"]*\\/[^\"]*\\.js)\"");
		Matcher m = link.matcher(html);
		 
		if(m.find()) {
			javascript = Util.downloadFileToString("https://www.youtube.com" + m.group(1), false);
		}
		
//		PrintWriter writer = new PrintWriter(new File("C:\\Users\\samse\\Desktop\\JavaScript.txt"));
//		writer.write(javascript);
	}
	
	public static String decode(String id, String signature) throws IOException {
		Pattern method = Pattern.compile("\\.set\\(\"signature\",([\\w\\d\\$_]+)\\(.+?\\)\\)");
		Matcher match = method.matcher(javascript);
		
		String methodName = null;
		if(match.find()) {
			methodName = match.group(1);
		}
		
		Pattern argumentPattern = Pattern.compile("function\\(([\\w\\d_]+)\\)");
		Matcher ma = argumentPattern.matcher(javascript);
		
		String argument = null;
		if(ma.find()) {
			argument = ma.group(1);
		}
		
//		System.out.println(javascript);
//		
//		System.out.println("------------");
//		
//		System.out.println(methodName);
//		System.out.println(argument);
		
		int lastBracket = javascript.lastIndexOf("}");
//		System.out.println(javascriptFile.substring(lastBracket - 20, lastBracket + 1));
		
		String endCode = javascript.substring(lastBracket);
		String startCode = javascript.substring(0, lastBracket);
//		
		startCode += argument + ".decode=" + methodName + ";";
		endCode += "_yt_player.decode(\""+ signature + "\");";

//		System.out.println(startCode + endCode);
		return (String) Util.executeJavaScript(startCode + endCode);
	}
	
	public static void free(){javascript = null;}
}
