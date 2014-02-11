/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.titaniumallies.frcsmartlibrary.comms;

import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;



/**
 *
 * @author Nathanial Lattimer <Nathanial.Lattimer at iismathwizard.com>
 */
public class Client {
    private SocketHandler _handle;
    
    public Client(String ip) throws FailedToConnectException
    {
        this(ip, 1248);
    }
    public Client(String ip, int port) throws FailedToConnectException
    {
        try {
            SocketConnection conn = (SocketConnection) Connector.open("socket://" + ip + ":" + port);
            conn.setSocketOption(SocketConnection.KEEPALIVE, 0);
            _handle = new SocketHandler(conn);
        } catch (Exception e) {
            throw new FailedToConnectException("The client at IP: " + ip + " --- and PORT: " + port + " --- failed to connect");
        }
        
    }
    
    public String read()
    {
        if(_handle == null) throw new RuntimeException("A connection wasn't properly made");
        return _handle.read();
    }
    
    public void write(String message)
    {
        if(_handle == null) throw new RuntimeException("A connection wasn't properly made");
        _handle.write(message);
    }
}
