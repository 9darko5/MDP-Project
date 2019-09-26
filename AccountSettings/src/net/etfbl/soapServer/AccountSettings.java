package net.etfbl.soapServer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

public class AccountSettings {
	private static String redisHost;
    private static Integer redisPort;
    public static final String PROPERTIES_PATH="C:\\Users\\Darko\\eclipse-workspace\\AccountSettings\\config.properties";
    //the jedis connection pool..
    private static JedisPool pool = null;
 
    public void jedisMain() {
    	setProperties();
        pool = new JedisPool(redisHost, redisPort);
    }
    
    public static Handler handler;

	{
		try {
			handler = new FileHandler("C:\\Users\\Darko\\eclipse-workspace\\AccountSettings\\log\\error.log");
			Logger.getLogger(AccountSettings.class.getName()).addHandler(handler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void log(Level level, String name, Exception ex) {
		Logger logger = Logger.getLogger(name);
		logger.log(level, ex.fillInStackTrace().toString());
	}
    
    
    public boolean addAccount(String ime, String prezime, String username, String lozinka) {
    	String keyUsername = username;
        Map<String, String> map = new HashMap<>();
        map.put("ime", ime);
        map.put("prezime", prezime);
        map.put("lozinka", lozinka);
  
       jedisMain();
       Jedis jedis = pool.getResource();
       try {
           //save to redis
           jedis.hmset(keyUsername, map);
           return true;
       } catch (JedisException e) {
    	   log(Level.ALL, AccountSettings.class.getName(), e);
    	   e.printStackTrace();
       } 
       return false;
    }

    public void removeAccount(String username) {
    	jedisMain();
    	Jedis jedis = pool.getResource();
    	jedis.hdel(username, "ime", "prezime", "lozinka");
    }

    public boolean updateAccount(String ime, String prezime, String username, String lozinka) {
    	jedisMain();
    	Jedis jedis = pool.getResource();
    	jedis.hdel(username, "ime", "prezime", "lozinka");
    	addAccount(ime, prezime, username, lozinka);
    	return true;
    }
    
    private void setProperties(){
		FileReader reader;  
	      
	    Properties p=new Properties();  
	    try {
	    	reader=new FileReader(PROPERTIES_PATH); 
			p.load(reader);
		} catch (IOException e) {
			log(Level.ALL, AccountSettings.class.getName(), e);
			e.printStackTrace();
		}  

		redisHost = p.getProperty("redisHost");
		redisPort = Integer.parseInt(p.getProperty("redisPort"));
	}
}
