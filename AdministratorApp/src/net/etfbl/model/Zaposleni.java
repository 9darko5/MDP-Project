package net.etfbl.model;

public class Zaposleni {
	public String ime;
	public String prezime;
	private String username;
	private String password;
	
	public Zaposleni(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public Zaposleni() {
		super();
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
