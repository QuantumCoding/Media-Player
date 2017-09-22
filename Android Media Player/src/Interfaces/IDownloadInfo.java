package Interfaces;

import java.awt.Image;

public interface IDownloadInfo {
	public Image getThumbnail();
	
	public String getSongName();
	public IVideoFormat[] getFormats();
	
	public IDownloadController download(IVideoFormat formate, String saveTo);
}
