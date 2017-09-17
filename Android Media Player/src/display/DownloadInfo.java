package display;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

public class DownloadInfo extends JFrame {
	private static final long serialVersionUID = -1925184381338746365L;
	
	private JPanel contentPane;
	private JProgressBar progressBar;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	
	public static void main(String[] args) {
		System.setOut(new DownloadInfo("N/A").getWriter());
		
		System.out.println("Hello");
		System.out.println("My Name Is Steave!");
		System.out.println("What up DOg");
	}

	public DownloadInfo(String videoId) {
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) { }
		
		setTitle("Info for: " + videoId);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(450, 300); setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 5));
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		contentPane.add(progressBar, BorderLayout.SOUTH);
		
		scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 14));
		textArea.setBorder(new EmptyBorder(3, 3, 3, 3));
		textArea.setEditable(false);
		
		setVisible(true);
	}
	
	private static class TextAreaOutputStream extends OutputStream {
		private JTextArea out;
		public TextAreaOutputStream(JTextArea out) { this.out = out; }
		
		public void write(int b) throws IOException { 
			out.append((char) b + ""); 
			out.setCaretPosition(out.getDocument().getLength());
		}
		
		public void write(byte b[], int off, int len) throws IOException {
			if(len == 0) return;
			else if(b == null) throw new NullPointerException();
			else if(off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) 
				throw new IndexOutOfBoundsException();
			
			out.append(new String(b, off, len));
			out.setCaretPosition(out.getDocument().getLength());
		}
	}
	
	private PrintStream writer;
	public PrintStream getWriter() {
		return writer == null ? writer = new PrintStream(new TextAreaOutputStream(textArea)) : writer;
	}
	
	public void updateProgress(int current, int max) {
		progressBar.setMaximum(max);
		progressBar.setValue(current);
		progressBar.setString((int)((float) current / max * 100) + "%");
	}
}
