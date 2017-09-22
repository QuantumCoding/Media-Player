package Interfaces;

public interface IDownloadController {
	public void resume();
	public void pause();
	public void cancel();
	
	public String getFileName();
	public boolean isFinished();
	
	public void attachUI(IDownloadUI ui);
}