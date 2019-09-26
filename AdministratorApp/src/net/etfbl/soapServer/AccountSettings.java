/**
 * AccountSettings.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.etfbl.soapServer;

public interface AccountSettings extends java.rmi.Remote {
    public void jedisMain() throws java.rmi.RemoteException;
    public boolean addAccount(java.lang.String ime, java.lang.String prezime, java.lang.String username, java.lang.String lozinka) throws java.rmi.RemoteException;
    public void removeAccount(java.lang.String username) throws java.rmi.RemoteException;
    public boolean updateAccount(java.lang.String ime, java.lang.String prezime, java.lang.String username, java.lang.String lozinka) throws java.rmi.RemoteException;
}
