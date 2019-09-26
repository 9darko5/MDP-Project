package net.etfbl.soapServer;

public class AccountSettingsProxy implements net.etfbl.soapServer.AccountSettings {
  private String _endpoint = null;
  private net.etfbl.soapServer.AccountSettings accountSettings = null;
  
  public AccountSettingsProxy() {
    _initAccountSettingsProxy();
  }
  
  public AccountSettingsProxy(String endpoint) {
    _endpoint = endpoint;
    _initAccountSettingsProxy();
  }
  
  private void _initAccountSettingsProxy() {
    try {
      accountSettings = (new net.etfbl.soapServer.AccountSettingsServiceLocator()).getAccountSettings();
      if (accountSettings != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)accountSettings)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)accountSettings)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (accountSettings != null)
      ((javax.xml.rpc.Stub)accountSettings)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public net.etfbl.soapServer.AccountSettings getAccountSettings() {
    if (accountSettings == null)
      _initAccountSettingsProxy();
    return accountSettings;
  }
  
  public void jedisMain() throws java.rmi.RemoteException{
    if (accountSettings == null)
      _initAccountSettingsProxy();
    accountSettings.jedisMain();
  }
  
  public boolean addAccount(java.lang.String ime, java.lang.String prezime, java.lang.String username, java.lang.String lozinka) throws java.rmi.RemoteException{
    if (accountSettings == null)
      _initAccountSettingsProxy();
    return accountSettings.addAccount(ime, prezime, username, lozinka);
  }
  
  public void removeAccount(java.lang.String username) throws java.rmi.RemoteException{
    if (accountSettings == null)
      _initAccountSettingsProxy();
    accountSettings.removeAccount(username);
  }
  
  public boolean updateAccount(java.lang.String ime, java.lang.String prezime, java.lang.String username, java.lang.String lozinka) throws java.rmi.RemoteException{
    if (accountSettings == null)
      _initAccountSettingsProxy();
    return accountSettings.updateAccount(ime, prezime, username, lozinka);
  }
  
  
}