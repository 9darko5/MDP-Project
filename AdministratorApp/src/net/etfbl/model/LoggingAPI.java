package net.etfbl.model;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.etfbl.controller.LoginController;

public class LoggingAPI {
	public static Handler handler;
	public LoggingAPI() {
		try {
			// logger ce upisivati logove u fajl banka.log
			// ime logger-a je naziv klase
			handler = new FileHandler("log"+File.separator+"error.log");
			Logger.getLogger(LoggingAPI.class.getName()).addHandler(handler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void log(Level level, String name, Exception ex) {
		Logger logger = Logger.getLogger(name);
		logger.log(level, ex.fillInStackTrace().toString());
	}
}
