package application.controller.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

public class FileController implements Initializable {
	private ServerSocket ss = null;
	private Socket socket = null;
	private int port = 9865;
	private File file;
	private String path;
	public FileController() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {

					ss = new ServerSocket(port);
					System.out.println("파일전송 1:1 서버 생성");

					socket = ss.accept();
					System.out.println(socket.getInetAddress().getAddress() + "에서 접속.");
					InputStream is = socket.getInputStream();
					

//						vo = (ObjectVO) ois.readObject();
					FileOutputStream fos = new FileOutputStream(path);
					byte[] buffer = new byte[10000];
					int length;
					while ((length = is.read(buffer)) != -1) {
						fos.write(buffer, 0, length);
					}
					System.out.println("Complete");
						
					is.close();
					fos.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						if (socket != null) {
							socket.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						if (ss != null) {
							ss.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.start();

	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}
	
	public void setPath(String path) {
		this.path = path;
	}

}
