package net.etfbl.rmi;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FileManagementInterface extends Remote {
	File getFileByName(String filePath, String name) throws RemoteException;
	void sendFile(String username, File file) throws RemoteException;
	List<String> getFilesNamesForUser(String username) throws RemoteException;
}
