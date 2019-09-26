package net.etfbl.controller;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.xml.rpc.ServiceException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import net.etfbl.soapServer.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class AddUserController implements Initializable {

	@FXML
	private TextField usernameTF;

	@FXML
	private Button addUserBTN;

	@FXML
	private TextField passwordTF;

	@FXML
	private TextField nameTF;

	@FXML
	private TextField lastnameTF;

	public static boolean addUser = false;

	public static int userNumber = 5;

	private static String redisHost;
	private static Integer redisPort;

	// the jedis connection pool..
	private static JedisPool pool = null;

	public void jedisMain() {
		// configure our pool connection
		pool = new JedisPool(redisHost, redisPort);
	}

	@FXML
	void addUserAction(ActionEvent event) {
		AccountSettingsServiceLocator loc = new AccountSettingsServiceLocator();

		try {
			AccountSettings service = loc.getAccountSettings();
			if (service.addAccount(nameTF.getText(), lastnameTF.getText(), usernameTF.getText(),
					passwordTF.getText())) {
				AlertAddedUserSuccefully();
				addUserBTN.getScene().getWindow().hide();
				openForm("AdminOptions");
			} else {
				AlertUnsuccefullyAdding();
				return;
			}
		} catch (ServiceException | RemoteException e) {
			LoginController.log(Level.ALL, AddUserController.class.getName(), e);
		}
	}

	public void AlertAddedUserSuccefully() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Bravo");
		alert.setHeaderText("Uspješno dodan nalog");
		userNumber++;
		alert.showAndWait();
	}

	public void AlertUnsuccefullyAdding() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Greška");
		alert.setHeaderText("Neuspješno dodan nalog");
		alert.showAndWait();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setProperties();
	}
	
	private void openForm(String formName) {
		Stage s = new Stage();
		FXMLLoader loader = new FXMLLoader();
		Parent root = null;
		try {
			root = loader.load(getClass().getResource("/net/etfbl/view/"+formName+".fxml"));
		} catch (IOException e) {
			LoginController.log(Level.ALL, AddUserController.class.getName(), e);
		}
		Scene scene = new Scene(root);
		s.setScene(scene);
		s.show();
		s.setResizable(false);
	}
	
	private void setProperties(){
		FileReader reader;  
	      
	    Properties p=new Properties();  
	    try {
	    	reader=new FileReader(LoginController.PROPERTIES_PATH); 
			p.load(reader);
		} catch (IOException e) {
			LoginController.log(Level.ALL, AddUserController.class.getName(), e);
		} 

		redisHost = p.getProperty("redisHost");
		redisPort = Integer.parseInt(p.getProperty("redisPort"));
	}
}
