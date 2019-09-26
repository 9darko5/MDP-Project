package net.etfbl.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import net.etfbl.model.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class StatisticsController implements Initializable{

	public static String username="";
	public static String BASE_URL;
	private static String redisHost;
	private static Integer redisPort;
	private static JedisPool pool = null;

	public void jedisMain() {
		// configure our pool connection
		pool = new JedisPool(redisHost, redisPort);
	}
	
	@FXML
	private Label statisticsLabel;
	
	@FXML
	private TableView<Statistics> statisticsTV;

	@FXML
	private TableColumn<Statistics, Date> loginDateColumn;
	
	@FXML
	private TableColumn<Statistics, Date> logoutDateColumn;
	
	@FXML
	private TableColumn<Statistics, String> sessionTimeColumn;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setProperties();
		getStatistics();
	}
	
	private void getStatistics() {
		List<Statistics> listStats=new ArrayList<>();
		try {
			JSONArray jsonArray = readAllJSON();
			for (int i = 0; i < jsonArray.length(); i++) {
				String response=jsonArray.get(i).toString();
				Date loginDate=getDateFromString(response.split("_")[0]);
				Date logoutDate=getDateFromString(response.split("_")[2]);
				long sessionTime=Long.parseLong(response.split("_")[1]);
				int seconds = (int) (sessionTime / 1000) % 60 ;
				int minutes = (int) ((sessionTime / (1000*60)) % 60);
				int hours   = (int) ((sessionTime / (1000*60*60)) % 24);
				String sessTimeStr=hours+"h "+minutes+"min "+seconds+"s";
				listStats.add(new Statistics(loginDate, logoutDate, sessTimeStr));
			}
		} catch (Exception e) {
			LoginController.log(Level.ALL, StatisticsController.class.getName(), e);
		}
		ObservableList<Statistics> oListStats = FXCollections.observableArrayList(listStats);
		loginDateColumn.setCellValueFactory(new PropertyValueFactory<>("loginDate"));
		logoutDateColumn.setCellValueFactory(new PropertyValueFactory<>("logoutDate"));
		sessionTimeColumn.setCellValueFactory(new PropertyValueFactory<>("sessionTime"));
		statisticsTV.setItems(oListStats);
	}

	private String getNameByUsername(String username) {
		jedisMain();
		Jedis jedis = pool.getResource();
		Set<String> set = jedis.keys("*");
		for (String usernameKey : set) {
			if (usernameKey.equals(username))
				return jedis.hget(usernameKey, "ime")+" "+jedis.hget(usernameKey, "prezime");
		}
		return null;
	}
	private Date getDateFromString(String dateStr) {
		Date date=null;
		try {
			date = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse(dateStr);
		} catch (ParseException e) {
			LoginController.log(Level.ALL, StatisticsController.class.getName(), e);
		}
		return date;
	}
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	public static JSONArray readAllJSON() throws IOException, JSONException {
		InputStream is = new URL(BASE_URL+username+"/statistics").openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONArray json = new JSONArray(jsonText);
			return json;
		} finally {
			is.close();
		}
	}
	
	private void setProperties(){
		FileReader reader;  
	      
	    Properties p=new Properties();  
	    try {
	    	reader=new FileReader(LoginController.PROPERTIES_PATH); 
			p.load(reader);
		} catch (IOException e) {
			LoginController.log(Level.ALL, StatisticsController.class.getName(), e);
		}  
	    BASE_URL=p.getProperty("BASE_URL");
	    redisPort=Integer.parseInt(p.getProperty("redisPort"));
	    redisHost=p.getProperty("redisHost");
	    String s=getNameByUsername(username);
	    statisticsLabel.setText("Statistika korisnika "+getNameByUsername(username));
	}

}
