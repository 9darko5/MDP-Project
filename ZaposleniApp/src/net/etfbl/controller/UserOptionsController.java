package net.etfbl.controller;

import net.etfbl.model.ChatViewThread;
import net.etfbl.model.Data;
import net.etfbl.rmi.FileManagementInterface;
import net.etfbl.util.*;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.DatagramPacket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.net.ssl.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.google.gson.Gson;

import javafx.fxml.Initializable;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class UserOptionsController implements Initializable {

	@FXML
	private ImageView img;

	@FXML
	private Label choosenFileLabel;

	@FXML
	private ComboBox<String> chooseUsersInTableCMB;

	@FXML
	private TextArea chatWindowTA;

	@FXML
	private Button chooseFileBTN;

	@FXML
	private MenuItem changePasswordMenuItem;

	@FXML
	private ComboBox<String> chooseUserCMB;

	@FXML
	private Button sendMultiMessageBTN;

	@FXML
	private Button sendFileBTN;

	@FXML
	private Button refreshBTN;

	@FXML
	private TextArea adminMessageTA;

	@FXML
	private TextArea groupMessageTA;

	@FXML
	private MenuItem logoutMenuItem;

	@FXML
	private Label nameToChatLabel;

	@FXML
	private TextField writeMultiMessageTF;

	@FXML
	private MenuItem viewStatisticsMenuItem;

	@FXML
	private TextField writeSingleMessageTF;

	@FXML
	private Button sendSingleMessageBTN;

	@FXML
	private TableView<String> onlineUsersTW;

	@FXML
	private TableColumn<String, String> onlineUsersColumn;

	@FXML
	private TableView<String> receivedFilesTV;

	@FXML
	private TableColumn<String, String> nameOfReceivedFilesColumn;
	
	@FXML
	private Button refreshListOfFilesBTN;

	private static int serCount=0;
	public static String username = "";
	public static boolean logout = true;
	public static List<ChatViewThread> threads = new ArrayList<>();
	public File selectedFile = null;
	private static String LOCAL_PATH;
	private static String RESOURCE_PATH_LOCAL;

	private static int MULTICAST_PORT;
	private static String MULTICAST_HOST;
	
	private static String SERIALIZATION_PATH;

	private static String HOST;
	private static int PORT;
	private static String KEY_STORE_PATH;
	private static String KEY_STORE_PASSWORD;

	private static String redisHost;
	private static Integer redisPort;

	public static String BASE_URL;

	private Date loginDate;
	private Date logoutDate;
	private long sessionStartTime;
	private long sessionEndTime;

	// the jedis connection pool..
	private static JedisPool pool = null;

	public void jedisMain() {
		// configure our pool connection
		pool = new JedisPool(redisHost, redisPort);
	}

	@FXML
	void viewStatisticsAction(ActionEvent event) {
		StatisticsController.username = username;
		openForm("Statistics");
	}

	@FXML
	void changePasswordAction(ActionEvent event) {
		ChangePasswordController.username = username;
		openForm("ChangePassword");
	}
	
	@FXML
	void refreshListOfFilesAction(ActionEvent event) {
		fillTableViewReceivedFiles();
	}

	@FXML
	void chooseUsersInTableAction(ActionEvent event) {
		if (chooseUsersInTableCMB.getSelectionModel().getSelectedItem() != null) {
			String clicked = chooseUsersInTableCMB.getSelectionModel().getSelectedItem().toString();
			if (!("Svi".equals(clicked))) {
				fillTableViewOnline();
			} else {
				fillTableViewAll();
			}
		}
	}

	@FXML
	void logoutAction(ActionEvent event) {
		logoutDate = new Date();
		sessionEndTime = System.currentTimeMillis();
		logout = true;
		jedisMain();
		Jedis jedis = pool.getResource();
		List<String> listToPop = new ArrayList<>();
		String tmp = "";
		while ((tmp = jedis.lpop("online")) != null) {
			if (!tmp.equals(username))
				listToPop.add(tmp);
		}
		for (int i = listToPop.size() - 1; i >= 0; i--) {
			jedis.lpush("online", listToPop.get(i));
		}
		try {
			// priprema i otvaranje HTTP zahtjeva
			URL url = new URL(BASE_URL + username + "/statistics");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "text/plain");
			String logoutD = dateToString(logoutDate);
			String loginD = dateToString(loginDate);
			long sessionTime = sessionEndTime - sessionStartTime;
			String input = loginD + "_" + sessionTime + "_" + logoutD;
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
			if (!(output = br.readLine()).equals("false")) {
				//System.out.println("Dobro dodato vrijeme");
			}
			chooseFileBTN.getScene().getWindow().hide();
			openForm("Login");
		} catch (Exception e) {
			LoginController.log(Level.ALL, UserOptionsController.class.getName(), e);
		}
	}

	@FXML
	void sendSingleMessageAction(ActionEvent event) {
		System.setProperty("javax.net.ssl.trustStore", KEY_STORE_PATH);
		System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASSWORD);

		SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket s;
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			s = (SSLSocket) sf.createSocket(HOST, PORT);
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
		} catch (IOException e) {
			LoginController.log(Level.ALL, UserOptionsController.class.getName(), e);
			e.printStackTrace();
		}
		String message = ProtocolMessages.SINGLE_MESSAGE.getMessage() + ProtocolMessages.MESSAGE_SEPARATOR.getMessage()
				+ username + ProtocolMessages.MESSAGE_SEPARATOR.getMessage() + writeSingleMessageTF.getText()
				+ ProtocolMessages.MESSAGE_SEPARATOR.getMessage()
				+ getUsernameByName(onlineUsersTW.getSelectionModel().getSelectedItem());
		out.println(message);
		writeSingleMessageTF.clear();
	}

	@FXML
	void chooseFileAction(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File("." + File.separator + "resources"));
		selectedFile = fc.showOpenDialog(null);
		choosenFileLabel.setText(selectedFile.getName());
	}

	@FXML
	void refreshAction(ActionEvent event) {
		fillTableViewOnline();
	}

	@FXML
	void sendFileAction(ActionEvent event) {
		System.setProperty("java.security.policy", RESOURCE_PATH_LOCAL);
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		String name = "FileManagementServer";
		try {
			Registry registry = LocateRegistry.getRegistry(1099);
			FileManagementInterface fileManagement = (FileManagementInterface) registry.lookup(name);
			if (selectedFile!=null && selectedFile.isFile() && chooseUserCMB.getSelectionModel().getSelectedItem() != null) {
				fileManagement.sendFile(
						getUsernameByName(chooseUserCMB.getSelectionModel().getSelectedItem().toString()),
						selectedFile);
				succefullySendFileAlert();
			} else {
				notSelectedUserOrFileAlert();
				return;
			}
		} catch (RemoteException | NotBoundException re) {
			LoginController.log(Level.ALL, UserOptionsController.class.getName(), re);
		}
	}

	@FXML
	void sendMultiMessageAction(ActionEvent event) {
		System.setProperty("javax.net.ssl.trustStore", KEY_STORE_PATH);
		System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASSWORD);

		SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket s;
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			s = (SSLSocket) sf.createSocket(HOST, PORT);
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
		} catch (IOException e) {
			LoginController.log(Level.ALL, UserOptionsController.class.getName(), e);
		}
		String message = ProtocolMessages.MULTI_MESSAGE.getMessage() + ProtocolMessages.MESSAGE_SEPARATOR.getMessage()
				+ writeMultiMessageTF.getText() + ProtocolMessages.MESSAGE_SEPARATOR.getMessage() + username;
		out.println(message);
		writeMultiMessageTF.clear();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setProperties();
		setSessionStartInfo();
		fillTableViewOnline();
		fillCMBForChooseUsersInTable();
		fillChooseUserCMB();
		fillTableViewReceivedFiles();
		multicastCommunicationInit();
		setDoubleClickActionOnUser();
		setDoubleClickActionOnFile();
		logout = false;
		setMultiChat();
		takeScreenshot();
	}

	private void setSessionStartInfo() {
		loginDate = new Date();
		sessionStartTime = System.currentTimeMillis();
		groupMessageTA.setEditable(false);
		chatWindowTA.setEditable(false);
	}

	private void setMultiChat() {
		adminMessageTA.setEditable(false);
		jedisMain();
		Jedis jedis = pool.getResource();
		
		new Thread() {
			@Override
			public void run() {
				while (!logout) {
					System.setProperty("javax.net.ssl.trustStore", KEY_STORE_PATH);
					System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASSWORD);

					SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
					SSLSocket s;
					BufferedReader in = null;
					PrintWriter out = null;
					try {
						s = (SSLSocket) sf.createSocket(HOST, PORT);
						in = new BufferedReader(new InputStreamReader(s.getInputStream()));
						out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
					} catch (IOException e) {
						LoginController.log(Level.ALL, UserOptionsController.class.getName(), e);
					}
					String request = ProtocolMessages.MULTI_CHAT_HISTORY.getMessage()
							+ ProtocolMessages.MESSAGE_SEPARATOR.getMessage();
					out.println(request);
					String response = "";
					try {
						response = in.readLine();
					} catch (IOException e) {
						LoginController.log(Level.ALL, UserOptionsController.class.getName(), e);
					}
					String[] messages = response.split(ProtocolMessages.MESSAGE_SEPARATOR.getMessage());
					String message = "";
					for (int i = 0; i < messages.length; i++)
						message += messages[i] + "\n";
					groupMessageTA.setText(message);
				}
			}
		}.start();

	}

	private void fillTableViewAll() {
		refreshBTN.setVisible(false);
		jedisMain();
		Jedis jedis = pool.getResource();

		onlineUsersColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue()));

		ObservableList<String> items = FXCollections.observableArrayList();
		Set<String> set = jedis.keys("*");
		int i = 0;
		for (String usernameKey : set) {
			int a = usernameKey.split("_").length;
			if (!usernameKey.equals("multimessages") && !usernameKey.equals("online")
					&& usernameKey.split("_").length < 2 && !usernameKey.equals(username))
				items.add(jedis.hget(usernameKey, "ime") + " " + jedis.hget(usernameKey, "prezime"));
		}
		onlineUsersTW.setItems(items);
	}

	private void fillTableViewOnline() {
		refreshBTN.setVisible(true);
		jedisMain();
		Jedis jedis = pool.getResource();

		onlineUsersColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue()));

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
		onlineUsersTW.setItems(items);
	}

	private void fillTableViewReceivedFiles() {
		nameOfReceivedFilesColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue()));

		System.setProperty("java.security.policy", RESOURCE_PATH_LOCAL);
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		String name = "FileManagementServer";
		ObservableList<String> items = FXCollections.observableArrayList();
		try {
			Registry registry = LocateRegistry.getRegistry(1099);
			FileManagementInterface fileManagement = (FileManagementInterface) registry.lookup(name);
			List<String> listOfFileName = new ArrayList<String>();
			listOfFileName = fileManagement.getFilesNamesForUser(username);
			items.addAll(listOfFileName);
		} catch (RemoteException | NotBoundException re) {
			LoginController.log(Level.ALL, UserOptionsController.class.getName(), re);
		}
		receivedFilesTV.setItems(items);
	}

	private void fillCMBForChooseUsersInTable() {
		List<String> list = new ArrayList<>();
		list.add("Svi");
		list.add("Aktivni");
		ObservableList<String> observableList = FXCollections.observableList(list);
		chooseUsersInTableCMB.setItems(observableList);
	}

	private void fillChooseUserCMB() {
		jedisMain();
		Jedis jedis = pool.getResource();
		ObservableList<String> items = FXCollections.observableArrayList();
		Set<String> set = jedis.keys("*");
		int i = 0;
		for (String usernameKey : set) {
			int a = usernameKey.split("_").length;
			if (!usernameKey.equals("multimessages") && !usernameKey.equals("online")
					&& usernameKey.split("_").length < 2 && !usernameKey.equals(username))
				items.add(jedis.hget(usernameKey, "ime") + " " + jedis.hget(usernameKey, "prezime"));
		}
		chooseUserCMB.setItems(items);
	}

	private void multicastCommunicationInit() {
		new Thread() {
			@Override
			public void run() {
				MulticastSocket socket = null;
				byte[] buffer = new byte[256];
				try {
					socket = new MulticastSocket(MULTICAST_PORT);
					InetAddress address = InetAddress.getByName(MULTICAST_HOST);
					socket.joinGroup(address);
					while (true) {
						DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
						socket.receive(packet);
						String received = new String(packet.getData(), 0, packet.getLength());
						Data data=new Data("multicastMessage"+serCount, received);
						if(serCount%4==0)
							serializeWithGson(data);
						else if(serCount%4==1)
							serializeWithJava(data);
						else if(serCount%4==2)
							serializeWithXML(data);
						else if(serCount%4==3)
							serializeWithKryo(data);
						adminMessageTA.clear();
						adminMessageTA.setText(received);
					}
				} catch (IOException ioe) {
					LoginController.log(Level.ALL, UserOptionsController.class.getName(), ioe);
				}
			}
		}.start();
	}

	private void setDoubleClickActionOnUser() {
		onlineUsersTW.setRowFactory(tv -> {
			TableRow<String> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					threads.add(new ChatViewThread() {
						@Override
						public void run() {
							String rowData = row.getItem().toString();
							Platform.runLater(() -> {
								nameToChatLabel.setText(rowData);
							});
							chatWindowTA.clear();
							while (!this.isNewChat()) {
								System.setProperty("javax.net.ssl.trustStore", KEY_STORE_PATH);
								System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASSWORD);

								SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
								SSLSocket s;
								BufferedReader in = null;
								PrintWriter out = null;
								try {
									s = (SSLSocket) sf.createSocket(HOST, PORT);
									in = new BufferedReader(new InputStreamReader(s.getInputStream()));
									out = new PrintWriter(
											new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
								} catch (IOException e) {
									LoginController.log(Level.ALL, UserOptionsController.class.getName(), e);
								}
								String request = ProtocolMessages.SINGLE_CHAT_HISTORY.getMessage()
										+ ProtocolMessages.MESSAGE_SEPARATOR.getMessage()
										+ getFirstNameByUsername(username) + "_" + rowData.split(" ")[0];
								out.println(request);
								String response = "";
								try {
									response = in.readLine();
								} catch (IOException e) {
									LoginController.log(Level.ALL, UserOptionsController.class.getName(), e);
								}
								String[] messages = response.split(ProtocolMessages.MESSAGE_SEPARATOR.getMessage());
								String message = "";
								for (int i = 0; i < messages.length; i++)
									message += messages[i] + "\n";
								chatWindowTA.setText(message);
							}
						}
					});
					for (int i = 0; i < threads.size(); i++) {
						if (i == threads.size() - 1)
							threads.get(i).start();
						else {
							threads.get(i).setNewChat(true);
						}
					}
				}
			});
			return row;
		});
	}

	private void setDoubleClickActionOnFile() {
		receivedFilesTV.setRowFactory(tv -> {
			TableRow<String> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					String fileName = row.getItem().toString();
					System.setProperty("java.security.policy", RESOURCE_PATH_LOCAL);
					if (System.getSecurityManager() == null) {
						System.setSecurityManager(new SecurityManager());
					}
					String name = "FileManagementServer";
					try {
						Registry registry = LocateRegistry.getRegistry(1099);
						FileManagementInterface fileManagement = (FileManagementInterface) registry.lookup(name);
						File downloadedFile = fileManagement.getFileByName(username, fileName);
						File directory = new File(LOCAL_PATH + File.separator + username);
						if (!directory.exists()) {
							directory.mkdir();
						}
						File localFile = new File(
								LOCAL_PATH + File.separator + username + File.separator + downloadedFile.getName());
						if (localFile.exists()) {
							alreadyDownloadedFile();
							return;
						}
						FileChannel src;
						FileChannel dest;
						try {
							src = new FileInputStream(downloadedFile).getChannel();
							dest = new FileOutputStream(localFile).getChannel();
							dest.transferFrom(src, 0, src.size());
							succefullyDownloadedFileAlert();
							src.close();
							dest.close();
						} catch (IOException e) {
							LoginController.log(Level.ALL, UserOptionsController.class.getName(), e);
						}
					} catch (RemoteException | NotBoundException re) {
						LoginController.log(Level.ALL, UserOptionsController.class.getName(), re);
					}
				}
			});
			return row;
		});
	}

	private String getPortByUserame(String username) {
		jedisMain();
		Jedis jedis = pool.getResource();
		Set<String> keys = jedis.keys("*");
		int i = 0;
		for (String usernameK : keys) {
			if (username.equals(usernameK)) {
				Map<String, String> map = jedis.hgetAll(username);
				return map.get("port");
			}
		}
		return null;
	}

	private String getUsernameByName(String name) {
		jedisMain();
		Jedis jedis = pool.getResource();
		Set<String> keys = jedis.keys("*");
		int i = 0;
		for (String username : keys) {
			if (!username.equals("online") && username.split("_").length < 2) {
				Map<String, String> map = jedis.hgetAll(username);
				if (map.get("ime").equals(name.split(" ")[0])) {
					return username;
				}
			}
		}
		return null;
	}

	private String getFirstNameByUsername(String username) {
		jedisMain();
		Jedis jedis = pool.getResource();
		Set<String> set = jedis.keys("*");
		for (String usernameKey : set) {
			if (usernameKey.equals(username))
				return jedis.hget(usernameKey, "ime");
		}
		return null;
	}

	private void takeScreenshot() {
		new Thread() {
			@Override
			public void run() {
				System.setProperty("javax.net.ssl.trustStore", KEY_STORE_PATH);
				System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASSWORD);

				SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
				SSLSocket s = null;
				BufferedReader in = null;
				PrintWriter out = null;
				while (!logout) {
					try {
						s = (SSLSocket) sf.createSocket(HOST, PORT);
						in = new BufferedReader(new InputStreamReader(s.getInputStream()));
						out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
					} catch (IOException e) {
						LoginController.log(Level.ALL, UserOptionsController.class.getName(), e);
					}
					String message = ProtocolMessages.MONITOR_RECEIVE.getMessage()
							+ ProtocolMessages.MESSAGE_SEPARATOR.getMessage() + username;

					out.println(message);
					try {
						Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
						BufferedImage capture = new Robot().createScreenCapture(screenRect);
						OutputStream outputStream = s.getOutputStream();

						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						ImageIO.write(capture, "jpg", byteArrayOutputStream);

						byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
						outputStream.write(size);
						outputStream.write(byteArrayOutputStream.toByteArray());
						outputStream.flush();
						sleep(100);
					} catch (AWTException | IOException | InterruptedException e) {
						LoginController.log(Level.ALL, UserOptionsController.class.getName(), e);
					}

				}
			}
		}.start();
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
		InputStream is = new URL(BASE_URL + username + "/statistics").openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONArray json = new JSONArray(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	private String dateToString(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		String strDate = dateFormat.format(date);
		return strDate;
	}

	private void openForm(String formName) {
		Stage s = new Stage();
		FXMLLoader loader = new FXMLLoader();
		Parent root = null;
		try {
			root = loader.load(getClass().getResource("/net/etfbl/view/" + formName + ".fxml"));
		} catch (IOException e) {
			LoginController.log(Level.ALL, UserOptionsController.class.getName(), e);
		}
		Scene scene = new Scene(root);
		s.setScene(scene);
		s.show();
		s.setResizable(false);
	}

	private void succefullyDownloadedFileAlert() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Bravo");
		alert.setHeaderText("Fajl je uspjesno preuzet!");

		alert.showAndWait();
	}

	private void notSelectedUserOrFileAlert() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Upozorenje");
		alert.setHeaderText("Korisnik ili fajl nije izabran!");
		alert.setContentText("Prvo izaberi korisnika i fajl!");

		alert.showAndWait();
	}

	private void alreadyDownloadedFile() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Upozorenje");
		alert.setHeaderText("Fajl već postoji!");
		alert.setContentText("Već postoji fajl sa tim nazvom!");

		alert.showAndWait();
	}
	
	private void succefullySendFileAlert() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Bravo");
		alert.setHeaderText("Fajl je uspjesno poslat!");

		alert.showAndWait();
	}

	// GSON
	public static void serializeWithGson(Data data) {
		Gson gson = new Gson();

		try {
			File dir=new File(SERIALIZATION_PATH+File.separator+username);
			if(!dir.exists()) {
				dir.mkdir();
			}
			FileWriter out = new FileWriter(new File(SERIALIZATION_PATH+File.separator+username+"\\gson"+(serCount++)+".out"));
			out.write(gson.toJson(data));
			out.close();
		} catch (IOException e) {
			LoginController.log(Level.ALL, UserOptionsController.class.getName(), e);
		}
	}

	// Kryo
	public static void serializeWithKryo(Data data) {
		Kryo kryo = new Kryo();
		kryo.register(Data.class);
		try {
			File dir=new File(SERIALIZATION_PATH+File.separator+username);
			if(!dir.exists()) {
				dir.mkdir();
			}
			Output out = new Output(new FileOutputStream(new File(SERIALIZATION_PATH+File.separator+username+"\\kryo"+(serCount++)+".out")));
			kryo.writeClassAndObject(out, data);
			out.close();
		} catch (Exception e) {
			LoginController.log(Level.ALL, UserOptionsController.class.getName(), e);
		}
	}

	// Java
	public static void serializeWithJava(Data data) {
		try {
			File dir=new File(SERIALIZATION_PATH+File.separator+username);
			if(!dir.exists()) {
				dir.mkdir();
			}
			FileOutputStream fileOut = new FileOutputStream(SERIALIZATION_PATH+File.separator+username+"\\java"+(serCount++)+".out");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(data);
			out.close();
		} catch (Exception e) {
			LoginController.log(Level.ALL, UserOptionsController.class.getName(), e);
		}
	}

	// XML
	public static void serializeWithXML(Data data) {
		try {
			File dir=new File(SERIALIZATION_PATH+File.separator+username);
			if(!dir.exists()) {
				dir.mkdir();
			}
			XMLEncoder encoder = new XMLEncoder(new FileOutputStream(new File(SERIALIZATION_PATH+File.separator+username+"\\xml"+(serCount++)+".out")));
			encoder.writeObject(data);
			encoder.close();
		} catch (Exception e) {
			LoginController.log(Level.ALL, UserOptionsController.class.getName(), e);
		}
	}

	private void setProperties() {
		FileReader reader;

		Properties p = new Properties();
		try {
			reader = new FileReader(LoginController.PROPERTIES_PATH);
			p.load(reader);
		} catch (IOException e) {
			LoginController.log(Level.ALL, UserOptionsController.class.getName(), e);
		}
		MULTICAST_PORT = Integer.parseInt(p.getProperty("MULTICAST_PORT"));
		MULTICAST_HOST = p.getProperty("MULTICAST_HOST");

		redisHost = p.getProperty("redisHost");
		redisPort = Integer.parseInt(p.getProperty("redisPort"));
		BASE_URL = p.getProperty("BASE_URL");
		PORT = Integer.parseInt(p.getProperty("PORT"));
		HOST = p.getProperty("HOST");
		RESOURCE_PATH_LOCAL = p.getProperty("RESOURCE_PATH_LOCAL");
		LOCAL_PATH = p.getProperty("LOCAL_PATH");
		KEY_STORE_PATH = p.getProperty("KEY_STORE_PATH");
		KEY_STORE_PASSWORD = p.getProperty("KEY_STORE_PASSWORD");
		SERIALIZATION_PATH = p.getProperty("SERIALIZATION_PATH");
	}
}
