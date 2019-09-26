package net.etfbl.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.net.*;

public class LoginController {

	public static final String PROPERTIES_PATH = "C:\\Users\\Darko\\eclipse-workspace\\ZaposleniApp\\config.properties";
	private static String redisHost;
	private static Integer redisPort;
	
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

	// the jedis connection pool..
	private static JedisPool pool = null;

	public void jedisMain() {
		// configure our pool connection
		pool = new JedisPool(redisHost, redisPort);
	}

	public static String BASE_URL;

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField password;

	@FXML
	private Button prijavaButton;

	@FXML
	void prijavaButton(ActionEvent event) {
		if (usernameField.getText().equals("") || password.getText().equals("")) {
			AlertEmptyFields();
			return;
		}
		setProperties();
		jedisMain();
		Jedis jedis = pool.getResource();
		jedis.lpush("online", usernameField.getText());
		UserOptionsController.username = usernameField.getText();
		try {
			// priprema i otvaranje HTTP zahtjeva
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
				break;
			}
			if (output.equals(password.getText())) {

				prijavaButton.getScene().getWindow().hide();

				Stage s = new Stage();
				FXMLLoader loader = new FXMLLoader();
				Parent root = null;
				try {
					root = loader.load(getClass().getResource("/net/etfbl/view/UserOptions.fxml"));
				} catch (IOException e) {
					log(Level.ALL, LoginController.class.getName(), e);
				}
				Scene scene = new Scene(root);
				s.setScene(scene);
				s.show();
				s.setResizable(false);
			} else {
				AlertWrongPassword();
				return;
			}
			br.close();
			conn.disconnect();
		} catch (Exception e) {
			log(Level.ALL, LoginController.class.getName(), e);
		}
	}

	public void AlertWrongPassword() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Lozinka nije dobra");
		alert.setHeaderText("Greška");
		alert.showAndWait();
	}

	public void AlertEmptyFields() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Korisničko ime ili lozinka nisu unijeti");
		alert.setHeaderText("Morate unijeti lozinku i korisničko ime");
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
		}
		BASE_URL = p.getProperty("BASE_URL");

		redisHost = p.getProperty("redisHost");
		redisPort = Integer.parseInt(p.getProperty("redisPort"));
	}
}
