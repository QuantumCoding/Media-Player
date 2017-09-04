package IO;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Util {
	public static String decode(String decode) {
		try {
			return URLDecoder.decode(decode, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object executeJavaScript(String eval) {
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
	     try {
	    	 return engine.eval(eval);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	    return null;
	}
}
