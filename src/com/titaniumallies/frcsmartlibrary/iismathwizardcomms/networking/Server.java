/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.titaniumallies.frcsmartlibrary.iismathwizardcomms.networking;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;

/**
 *
 * @author Nathanial Lattimer <Nathanial.Lattimer at iismathwizard.com>
 */
public class Server implements SocketHost{
    Vector listeners = new Vector();
    Vector portscanners = new Vector();
    
    public Server(int port)
    {
        portscanners.addElement(new PortScanner(port, this));
    }
    public Server(int[] ports)
    {
        for(int count = 0; count < ports.length; count ++)
        {
            portscanners.addElement(new PortScanner(ports[count], this));
        }
    }    
    
    public void stop()
    {
        for(int count = 0; count < portscanners.size(); count ++)
        {
            ((PortScanner)portscanners.elementAt(count)).kill();
        }
    }
    
    public void add(SocketConnection sock) {        
        SocketHandler handle = new SocketHandler(sock);
        sckHandlers.addElement(handle);
        
        for(int count = 0; count < listeners.size(); count ++)
        {
            ((NewSocketConnectionListener)listeners.elementAt(count)).newConnection(handle);
        }
    }
    
    public void subscribe(NewSocketConnectionListener listener)
    {
        listeners.addElement(listener);
        listener.subscribed(this);
    }
    
    public void unsubscribe(NewSocketConnectionListener listener)
    {
        listeners.addElement(listener);
        listener.unsubscribed(this);
    }

    private static class PortScanner extends Thread{
        private ServerSocketConnection _sckt;
        private SocketHost _host;
        private boolean _valid = false;
        
        public PortScanner(int port, SocketHost host) {
            this._host = host;
            
            try {
                _sckt = (ServerSocketConnection) Connector.open("socket://:" + port);
                
                this._valid = true;
                start();
            } catch (IOException ex) {
                System.out.println("PORT NUMBER: " + port + "\n\n" + ex.getMessage());
            }
            
        }
        public int getPort()
        {
            try {
                return _sckt.getLocalPort();
            } catch (IOException ex) {
                return -1;
            }
        }
        public void kill()
        {
            this._valid = false;
        }
        public void run()
        {
            while(this.isAlive())
            {
                check();
            }
        }
        public void check()
        {
            while(_valid)
            {
                try {
                    SocketConnection newSocket = (SocketConnection) _sckt.acceptAndOpen();
                    
                    if(newSocket == null) throw new IOException("Failed to create socket");
                    
                    _host.add(newSocket);
                } catch (IOException ex) {}
            }
        } 
    }
}
