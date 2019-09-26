package net.etfbl.rest.api;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

public class LoginService {
	public static final String PROPERTIES_PATH = "C:\\Users\\Darko\\eclipse-workspace\\RestServer\\config.properties";
	
	private static String redisHost;
    private static Integer redisPort;
 
    //the jedis connection pool..
    private static JedisPool pool = null;
 
    public void jedisMain() {
        //configure our pool connection
        pool = new JedisPool(redisHost, redisPort);
    }

	public static Handler handler;
    {
		try {
			handler = new FileHandler("C:\\Users\\Darko\\eclipse-workspace\\RestServer\\log\\error.log");
			Logger.getLogger(LoginService.class.getName()).addHandler(handler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void log(Level level, String name, Exception ex) {
		Logger logger = Logger.getLogger(name);
		logger.log(level, ex.fillInStackTrace().toString());
	}
	
    
	public LoginService(){
		setProperties();
	}
	
	 public boolean updateAccount(String username, String lozinka) {
	    	jedisMain();
	    	Jedis jedis = pool.getResource();
	    	jedis.hdel(username,  "lozinka");
	    	jedis.hset(username, "lozinka", lozinka);
	    	return true;
	    }
	
	public boolean setStatistics(String username, String loginDate_time_logoutDate) {
		jedisMain();
    	Jedis jedis = pool.getResource();
    	String loginDate=loginDate_time_logoutDate.split("_")[0];
    	String logoutDate=loginDate_time_logoutDate.split("_")[2];
    	String time=loginDate_time_logoutDate.split("_")[1];
    	jedis.hset("statistics_"+username, loginDate+"_"+time, logoutDate);
    	return true;
	}
	
	public ArrayList<String> getStatistics(String username) {
		jedisMain();
    	Jedis jedis = pool.getResource();
    	Set<String> keys=jedis.keys("*");
		Map<String, String> retrieveMap=jedis.hgetAll("statistics_"+username);
		Set<String> keySet=retrieveMap.keySet();
		ArrayList<String> listStats=new ArrayList<>();
		for(String s : keySet) {
			listStats.add(s+"_"+retrieveMap.get(s));
		}
		return listStats;
	}
	
	public String getPassword(String username) {
		jedisMain();
		Jedis jedis = pool.getResource();
        try {
        	//after saving the data, lets retrieve them to be sure that it has really added in redis
            Map<String, String> retrieveMap = jedis.hgetAll(username);
            if(retrieveMap.get("lozinka")!=null) {
            	return retrieveMap.get("lozinka");
            }
        } catch (JedisException e) {
        	log(Level.ALL, LoginService.class.getName(), e);
        } 
        return null;
	}
	private void setProperties(){
		FileReader reader;  
	      
	    Properties p=new Properties();  
	    try {
	    	reader=new FileReader(PROPERTIES_PATH); 
			p.load(reader);
		} catch (IOException e) {
			log(Level.ALL, LoginService.class.getName(), e);
		}  
		redisHost = p.getProperty("redisHost");
		redisPort = Integer.parseInt(p.getProperty("redisPort"));
	}
}
