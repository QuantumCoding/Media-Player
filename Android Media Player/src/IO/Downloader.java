package IO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import Interfaces.IDownloadController;
import Interfaces.IDownloadUI;
import Interfaces.IVideoFormat;

public class Downloader implements IDownloadController {
	private Thread thread;
	
	private InputStream in;
	private OutputStream out;
	private String fileName;

	private IDownloadUI ui;
	
	private boolean isPaused;
	private boolean finished;
	private boolean running;
	
	public Downloader(IVideoFormat format, String save) throws IOException {
		if(!(format instanceof QualityInfo))
			throw new IllegalArgumentException("Downloader Only Accepts Type QualityInfo.class, not " + format.getClass().getSimpleName());
		
		QualityInfo info = (QualityInfo) format;
		URL url = new URL(info.get("url")); 
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		in = connection.getInputStream();
		out = new FileOutputStream(new File(save));
		fileName = save.substring(save.lastIndexOf(File.separator) + 1);

		thread = new Thread(() -> {
			running = true;
			
			try {
				byte[] buffer = new byte[1 << 14];
				int read;
				
				double sizeOfFile = connection.getContentLength();
				double progress = 0;
				
				while(running && (read = in.read(buffer)) != -1) {
					while(isPaused)
						try { Thread.sleep(10); } catch (InterruptedException e) {}
					out.write(buffer, 0, read);
					progress += read;
					ui.updateStatus((int) (progress / sizeOfFile));
				}
			} catch(IOException e) {
				ui.showError(e.toString());
			} finally {
				try { out.close(); in.close(); } 
				catch (IOException e) { }
				
				connection.disconnect();
				finished = true;
			}
		}, fileName + " - Thread");
		
		thread.start();
	}

	@Override
	public void resume() {
		isPaused = false;
	}

	@Override
	public void pause() {
		isPaused = true;
	}

	@Override
	public void cancel() {
		running = false;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	@Override
	public void attachUI(IDownloadUI ui) {
		this.ui = ui;
	}
}
