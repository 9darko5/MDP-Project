package net.etfbl.main;


import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.*;
import net.etfbl.controller.AddUserController;
import net.etfbl.controller.LoginController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
import javafx.fxml.*;
import javafx.scene.*;


public class Main extends Application {
	private static final String redisHost = "localhost";
    private static final Integer redisPort = 6379;
 
    //the jedis connection pool..
    private static JedisPool pool = null;
 
    public void jedisMain() {
        //configure our pool connection
        pool = new JedisPool(redisHost, redisPort);
    }
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		jedisMain();
		Jedis jedis = pool.getResource();
		//add some values in Redis HASH
        for(int i=0; i<5; i++) {
        	 String keyUsername = "korisnik"+i;
             Map<String, String> map = new HashMap<>();
             map.put("ime", "Korisnik"+i);
             map.put("prezime", "Korisnikovic"+i);
             map.put("lozinka", "123456");
	        try {
	            //save to redis
	            jedis.hmset(keyUsername, map);
	        } catch (JedisException e) {
	            //if something wrong happen, return it back to the pool
	        } 
        }
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
			//LoginController.log(Level.ALL, Main.class.getName(), e);
			e.printStackTrace();
		}
	}

}
