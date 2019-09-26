package net.etfbl.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.*;

public class LoginController {

	
	public static final String PROPERTIES_PATH = "C:\\Users\\Darko\\eclipse-workspace\\RestServer\\config.properties";

	public static String BASE_URL;

	public static Logger log;
	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField password;

	@FXML
	private Button prijavaButton;

	public static Handler handler;

	{
		try {
			handler = new FileHandler("log"+File.separator+"error.log");
			Logger.getLogger(LoginController.class.getName()).addHandler(handler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void log(Level level, String name, Exception ex) {
		Logger logger = Logger.getLogger(name);
		logger.log(level, ex.fillInStackTrace().toString());
	}
	
	@FXML
	void prijavaButton(ActionEvent event) {
		setProperties();
		try {
			AdminOptionsController.username = usernameField.getText();
			URL url = new URL(BASE_URL + usernameField.getText());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "text/plain");
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			// citanje odgovora
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output = null;
			while ((output = br.readLine()) != null) {
				// System.out.println(output);
				break;
			}
			if (output.equals(password.getText())) {

				prijavaButton.getScene().getWindow().hide();

				Stage s = new Stage();
				FXMLLoader loader = new FXMLLoader();
				Parent root = null;
				try {
					root = loader.load(getClass().getResource("/net/etfbl/view/AdminOptions.fxml"));
				} catch (IOException e) {
					log(Level.ALL, LoginController.class.getName(), e);
					e.printStackTrace();
				}
				Scene scene = new Scene(root);
				s.setScene(scene);
				s.show();
				s.setResizable(false);
			} else {
				AlertWrongPassword();
				return;
			}
			// os.close();
			br.close();
			conn.disconnect();
		} catch (Exception e) {
			log(Level.ALL, LoginController.class.getName(), e);
			e.printStackTrace();
		}
	}

	public void AlertWrongPassword() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Lozinka nije dobra");
		alert.setHeaderText("Greška");
		alert.showAndWait();
	}

	private void setProperties() {
		FileReader reader;

		Properties p = new Properties();
		try {
			reader = new FileReader(PROPERTIES_PATH);
			p.load(reader);
		} catch (IOException e) {
			log(Level.ALL, LoginController.class.getName(), e);
			e.printStackTrace();
		}
		BASE_URL = p.getProperty("BASE_URL");
	}
}
