package net.etfbl.model;

import java.util.Date;

public class Statistics {
	Date loginDate;
	Date logoutDate;
	String sessionTime;
	
	public Statistics() {
		super();
	}

	public Statistics(Date loginDate, Date logoutDate, String sessionTime) {
		super();
		this.loginDate = loginDate;
		this.logoutDate = logoutDate;
		this.sessionTime = sessionTime;
	}

	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public Date getLogoutDate() {
		return logoutDate;
	}

	public void setLogoutDate(Date logoutDate) {
		this.logoutDate = logoutDate;
	}

	public String getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime(String sessionTime) {
		this.sessionTime = sessionTime;
	}
}
