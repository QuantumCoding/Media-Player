package Interfaces;

public abstract class IVideoFormat {
	public abstract String getFileExt();
	public abstract String getFormatName();
	
	public String toString() { return getFormatName(); }
}
