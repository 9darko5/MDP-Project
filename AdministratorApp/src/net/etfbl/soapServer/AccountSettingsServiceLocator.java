/**
 * AccountSettingsServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.etfbl.soapServer;

public class AccountSettingsServiceLocator extends org.apache.axis.client.Service implements net.etfbl.soapServer.AccountSettingsService {

    public AccountSettingsServiceLocator() {
    }


    public AccountSettingsServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AccountSettingsServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AccountSettings
    private java.lang.String AccountSettings_address = "http://localhost:8080/AccountSettings/services/AccountSettings";

    public java.lang.String getAccountSettingsAddress() {
        return AccountSettings_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AccountSettingsWSDDServiceName = "AccountSettings";

    public java.lang.String getAccountSettingsWSDDServiceName() {
        return AccountSettingsWSDDServiceName;
    }

    public void setAccountSettingsWSDDServiceName(java.lang.String name) {
        AccountSettingsWSDDServiceName = name;
    }

    public net.etfbl.soapServer.AccountSettings getAccountSettings() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AccountSettings_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAccountSettings(endpoint);
    }

    public net.etfbl.soapServer.AccountSettings getAccountSettings(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            net.etfbl.soapServer.AccountSettingsSoapBindingStub _stub = new net.etfbl.soapServer.AccountSettingsSoapBindingStub(portAddress, this);
            _stub.setPortName(getAccountSettingsWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAccountSettingsEndpointAddress(java.lang.String address) {
        AccountSettings_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (net.etfbl.soapServer.AccountSettings.class.isAssignableFrom(serviceEndpointInterface)) {
                net.etfbl.soapServer.AccountSettingsSoapBindingStub _stub = new net.etfbl.soapServer.AccountSettingsSoapBindingStub(new java.net.URL(AccountSettings_address), this);
                _stub.setPortName(getAccountSettingsWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("AccountSettings".equals(inputPortName)) {
            return getAccountSettings();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://soapServer.etfbl.net", "AccountSettingsService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://soapServer.etfbl.net", "AccountSettings"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AccountSettings".equals(portName)) {
            setAccountSettingsEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
