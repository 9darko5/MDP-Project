package net.etfbl.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.etfbl.util.ProtocolMessages;

public class MonitorController implements Initializable {

	@FXML
	private ImageView screenshotIV;

	public static String username;
	public static boolean end = false;
	private static String KEY_STORE_PATH;
	private static String KEY_STORE_PASSWORD;
	private static String HOST;
	private static int PORT;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setProperties();
		showScreenshot();
	}

	private void showScreenshot() {
		new Thread() {
			@Override
			public void run() {
				System.setProperty("javax.net.ssl.trustStore", KEY_STORE_PATH);
				System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASSWORD);
				while (!end) {
					SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
					SSLSocket s = null;
					BufferedReader in = null;
					PrintWriter out = null;
					try {
						s = (SSLSocket) sf.createSocket(HOST, PORT);
						in = new BufferedReader(new InputStreamReader(s.getInputStream()));
						out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);

						String message = ProtocolMessages.MONITOR_REQUEST.getMessage()
								+ ProtocolMessages.MESSAGE_SEPARATOR.getMessage() + username;
						out.println(message);
						DataInputStream dataInputStream = new DataInputStream(s.getInputStream());

						int size = dataInputStream.readInt();
						byte[] imageAr = new byte[size];
						dataInputStream.readFully(imageAr);

						BufferedImage capture = ImageIO.read(new ByteArrayInputStream(imageAr));
						Image image = SwingFXUtils.toFXImage(capture, null);
						screenshotIV.setImage(image);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							LoginController.log(Level.ALL, MonitorController.class.getName(), e);
						}
					} catch (IOException e) {
						LoginController.log(Level.ALL, MonitorController.class.getName(), e);
					}
				}
			}
		}.start();
		
	}
	
	private void setProperties(){
		FileReader reader;  
	      
	    Properties p=new Properties();  
	    try {
	    	reader=new FileReader(LoginController.PROPERTIES_PATH); 
			p.load(reader);
		} catch (IOException e) {
			LoginController.log(Level.ALL, MonitorController.class.getName(), e);
		}  
	    KEY_STORE_PATH=p.getProperty("KEY_STORE_PATH");
		KEY_STORE_PASSWORD =p.getProperty("KEY_STORE_PASSWORD");
	    PORT=Integer.parseInt(p.getProperty("PORT"));
	    HOST=p.getProperty("HOST");
	}

}
