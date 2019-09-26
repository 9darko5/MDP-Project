package net.etfbl.rmi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import net.etfbl.controller.LoginController;
import net.etfbl.controller.UserOptionsController;

public class FileManagementServer implements FileManagementInterface {
	
	public static String SERVER_PATH;
	public static String RESOURCE_PATH_SERVER;
	
	public static final String PROPERTIES_PATH = "C:\\Users\\Darko\\eclipse-workspace\\ZaposleniApp\\config.properties";
	
	@Override
	public File getFileByName(String username, String name) throws RemoteException {
		File directory=new File(SERVER_PATH+File.separator+username);
		File[] listOfFiles=directory.listFiles();
		for(int i=0; i<listOfFiles.length; i++) {
			if(listOfFiles[i].isFile()) {
				if(name.equalsIgnoreCase(listOfFiles[i].getName())) {
					return listOfFiles[i];
				}
			}
		}
		return null;
	}

	@Override
	public void sendFile(String username, File file) throws RemoteException {
		File directory=new File(SERVER_PATH+File.separator+username);
		if (!directory.exists()){
	        directory.mkdir();
	    }
		File localFile=new File(directory+File.separator+file.getName());
		FileChannel src;
		FileChannel dest;
		try {
			src = new FileInputStream(file).getChannel();
			dest = new FileOutputStream(localFile).getChannel();
			dest.transferFrom(src, 0, src.size());
			src.close();
			dest.close();
		} catch (IOException e) {
			LoginController.log(Level.ALL, FileManagementServer.class.getName(), e);
		}
	}

	@Override
	public List<String> getFilesNamesForUser(String username) throws RemoteException {
		List<String> listOfNames=new ArrayList<>();
		File directory=new File(SERVER_PATH+File.separator+username);
		if(!directory.exists()) {
			return listOfNames;
		}
		File[] listOfFiles=directory.listFiles();
		for(int i=0; i<listOfFiles.length; i++) {
			if(listOfFiles[i].isFile()) {
				listOfNames.add(listOfFiles[i].getName());
			}
		}
		return listOfNames;
	}
	
	public static void main(String Args[]) {
		setProperties();
		System.setProperty("java.security.policy", RESOURCE_PATH_SERVER);
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			FileManagementServer server = new FileManagementServer();
			FileManagementInterface stub = (FileManagementInterface) UnicastRemoteObject.exportObject(server, 0);
			Registry registry = LocateRegistry.createRegistry(1099);
			registry.rebind("FileManagementServer", stub);
			System.out.println("FileManagement Server started.");
		} catch (Exception ex) {
			LoginController.log(Level.ALL, FileManagementServer.class.getName(), ex);
		}
	}
	
	private static void setProperties(){
		FileReader reader;  
	      
	    Properties p=new Properties();  
	    try {
	    	reader=new FileReader(PROPERTIES_PATH); 
			p.load(reader);
		} catch (IOException e) {
			LoginController.log(Level.ALL, FileManagementServer.class.getName(), e);
		}  
	    RESOURCE_PATH_SERVER=p.getProperty("RESOURCE_PATH_SERVER");
	    SERVER_PATH=p.getProperty("SERVER_PATH");
	}
}
