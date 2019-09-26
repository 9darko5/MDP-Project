/**
 * AccountSettingsService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.etfbl.soapServer;

public interface AccountSettingsService extends javax.xml.rpc.Service {
    public java.lang.String getAccountSettingsAddress();

    public net.etfbl.soapServer.AccountSettings getAccountSettings() throws javax.xml.rpc.ServiceException;

    public net.etfbl.soapServer.AccountSettings getAccountSettings(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
