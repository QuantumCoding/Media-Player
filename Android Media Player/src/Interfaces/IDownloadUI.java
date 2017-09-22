package Interfaces;

public interface IDownloadUI {
	public void updateStatus(int percent);
	
	public void showError(String message);
	public void finish();
}
