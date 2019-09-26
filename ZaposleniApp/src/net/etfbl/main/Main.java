package net.etfbl.main;


import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.*;
import javafx.fxml.*;
import javafx.scene.*;


public class Main extends Application {
	
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try
		{
			Parent root=FXMLLoader.load(getClass().getResource("/net/etfbl/view/Login.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);
		}
		catch(Exception e)
		{
			//Logger.getLogger(AdminOptionsController.class.getName()).log(Level.WARNING, null, e);
			e.printStackTrace();
		}
	}

}
