package net.etfbl.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class ChangePasswordController implements Initializable {

	public static String username;
	public static String BASE_URL;

	@FXML
	private Button changePasswordBTN;

	@FXML
	private PasswordField passwordConfirmField;

	@FXML
	private PasswordField passwordField;

	@FXML
	void changePasswordAction(ActionEvent event) {
		try {
			// priprema i otvaranje HTTP zahtjeva
			URL url = new URL(BASE_URL + username);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "text/plain");
			String input=passwordField.getText();
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			// podaci za body dio zahtjeva
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			// citanje odgovora
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output = null;
			if ((output = br.readLine()).equals("false")) {
				changePasswordBTN.getScene().getWindow().hide();
				Stage s = new Stage();
				FXMLLoader loader = new FXMLLoader();
				Parent root = null;
				try {
					root = loader.load(getClass().getResource("/net/etfbl/view/UserOptions.fxml"));
				} catch (IOException e) {
					LoginController.log(Level.ALL, ChangePasswordController.class.getName(), e);
				}
				Scene scene = new Scene(root);
				s.setScene(scene);
				s.show();
				s.setResizable(false);
				AlertSuccefullyChanging();
			}
			else {
				AlertUnsuccefullyChanging();
			}
		} catch (Exception e) {
			LoginController.log(Level.ALL, ChangePasswordController.class.getName(), e);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setProperties();
	}
	
	public void AlertUnsuccefullyChanging() {
    	Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle("Greška");
    	alert.setHeaderText("Neuspješna izmjena lozinke");
    	alert.showAndWait();
    }
	
	public void AlertSuccefullyChanging() {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Bravo");
    	alert.setHeaderText("Uspješna izmjena lozinke");
    	alert.showAndWait();
    }

	private void setProperties(){
		FileReader reader;  
	      
	    Properties p=new Properties();  
	    try {
	    	reader=new FileReader(LoginController.PROPERTIES_PATH); 
			p.load(reader);
		} catch (IOException e) {
			LoginController.log(Level.ALL, ChangePasswordController.class.getName(), e);
		}  
		BASE_URL=p.getProperty("BASE_URL");
	}
}
