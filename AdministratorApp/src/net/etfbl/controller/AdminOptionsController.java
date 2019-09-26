package net.etfbl.controller;

import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class AdminOptionsController implements Initializable {

	@FXML
	private MenuItem changePasswordMeniItem;
	
	@FXML
	private TextArea adminMessageTA;

	@FXML
	private MenuItem logoutMenuItem;

	@FXML
	private ComboBox<String> chooseUserToUpdateCMB;

	@FXML
	private Button sendAdminMessageBTN;

	@FXML
	private MenuItem addUserMenuItem;
	
    @FXML
    private Button monitorBTN;
    
    @FXML
    private Button statisticsBTN;
    
    @FXML
    private Button refreshBTN;
    
	private static int MULTICAST_PORT;
	private static String MULTICAST_HOST;

	private static String redisHost;
	private static Integer redisPort;
	
	public static String username="";

	// the jedis connection pool..
	private static JedisPool pool = null;

	@FXML
    void statisticsAction(ActionEvent event) {
		refreshAction(event);
		if(chooseUserToUpdateCMB.getSelectionModel().getSelectedItem()==null) {
			notSelectedUserAlert();
			return;
		}
		StatisticsController.username = getUsernameByName(chooseUserToUpdateCMB.getSelectionModel().getSelectedItem().toString().split(" ")[0]);
		openForm("Statistics");
    }
	
	@FXML
	void sendAdminMessageAction(ActionEvent event) {
		MulticastSocket socket = null;
		byte[] buf = new byte[256];
		try {
			socket = new MulticastSocket();
			InetAddress address = InetAddress.getByName(MULTICAST_HOST);
			socket.joinGroup(address);
			String msg = adminMessageTA.getText();
			buf = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, MULTICAST_PORT);
			socket.send(packet);
			adminMessageTA.clear();
		} catch (IOException ioe) {
			LoginController.log(Level.ALL, AdminOptionsController.class.getName(), ioe);
		}
	}
	@FXML
	void changePasswordAction(ActionEvent event) {
		sendAdminMessageBTN.getScene().getWindow().hide();
		ChangePasswordController.username=username;
		openForm("ChangePassword");
	}
	
	@FXML
    void refreshAction(ActionEvent event) {
		pullIntoCMB();
    }
	
	@FXML
    void monitorAction(ActionEvent event) {
		refreshAction(event);
		if (chooseUserToUpdateCMB.getSelectionModel().getSelectedItem() == null) {
			notSelectedUserAlert();
			return;
		}
		MonitorController.username = getUsernameByName(chooseUserToUpdateCMB.getSelectionModel().getSelectedItem().toString().split(" ")[0]);
		MonitorController.end=false;
		Stage s = new Stage();
		FXMLLoader loader = new FXMLLoader();
		Parent root = null;
		try {
			root = loader.load(getClass().getResource("/net/etfbl/view/Monitor.fxml"));
		} catch (IOException e) {
			LoginController.log(Level.ALL, AdminOptionsController.class.getName(), e);
		}
		Scene scene = new Scene(root);
		s.setScene(scene);
		s.setOnCloseRequest(ev -> {
		    MonitorController.end=true;
		  });
		s.show();
		s.setResizable(false);
    }


	public void jedisMain() {
		// configure our pool connection
		pool = new JedisPool(redisHost, redisPort);
	}

	@FXML
	void addUserAction(ActionEvent event) {
		sendAdminMessageBTN.getScene().getWindow().hide();
		AddUserController.addUser = true;
		openForm("AddUser");
	}

	private String getUsernameByName(String name) {
		jedisMain();
		Jedis jedis = pool.getResource();
		Set<String> keys = jedis.keys("*");
		int i = 0;
		for (String username : keys) {
			if (!username.equals("multimessages")&& !username.equals("online") && username.split("_").length < 2) {
				Map<String, String> map = jedis.hgetAll(username);
				if (map.get("ime").equals(name.split(" ")[0])) {
					return username;
				}
			}
		}
		return null;
	}
	
	@FXML
	void logoutAction(ActionEvent event) {
		sendAdminMessageBTN.getScene().getWindow().hide();
		openForm("Login");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setProperties();
		pullIntoCMB();
	}

	public void pullIntoCMB() {
		jedisMain();
		Jedis jedis = pool.getResource();
		ObservableList<String> items = FXCollections.observableArrayList();
		List<String> listToPop = new ArrayList<>();
		String tmp = "";
		while ((tmp = jedis.lpop("online")) != null) {
			listToPop.add(tmp);
		}
		if (!listToPop.isEmpty()) {
			for (String usernameKey : listToPop) {
				if (!usernameKey.equals(username))
					items.add(jedis.hget(usernameKey, "ime") + " " + jedis.hget(usernameKey, "prezime"));
			}
		}
		for (int i = listToPop.size() - 1; i >= 0; i--) {
			jedis.lpush("online", listToPop.get(i));
		}
		chooseUserToUpdateCMB.setItems(items);
	}
	
	private void openForm(String formName) {
		Stage s = new Stage();
		FXMLLoader loader = new FXMLLoader();
		Parent root = null;
		try {
			root = loader.load(getClass().getResource("/net/etfbl/view/"+formName+".fxml"));
		} catch (IOException e) {
			LoginController.log(Level.ALL, AdminOptionsController.class.getName(), e);
		}
		Scene scene = new Scene(root);
		s.setScene(scene);
		s.show();
		s.setResizable(false);
	}
	
	private void notSelectedUserAlert() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Upozorenje");
		alert.setHeaderText("Korisnik nije izabran ili se odjavio");
		alert.setContentText("Prvo izaberi korisnika!");

		alert.showAndWait();
	}
	
	private void setProperties(){
		FileReader reader;  
	      
	    Properties p=new Properties();  
	    try {
	    	reader=new FileReader(LoginController.PROPERTIES_PATH); 
			p.load(reader);
		} catch (IOException e) {
			LoginController.log(Level.ALL, AdminOptionsController.class.getName(), e);
		}  
	    String s=p.getProperty("MULTICAST_PORT");
	    MULTICAST_PORT=Integer.parseInt(p.getProperty("MULTICAST_PORT"));
	    MULTICAST_HOST=p.getProperty("MULTICAST_HOST");

		redisHost = p.getProperty("redisHost");
		redisPort = Integer.parseInt(p.getProperty("redisPort"));
	}
}
