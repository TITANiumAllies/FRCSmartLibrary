/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.titaniumallies.frcsmartlibrary.comms;

import com.sun.squawk.io.BufferedReader;
import com.sun.squawk.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.microedition.io.SocketConnection;

/**
 *
 * @author Nathanial Lattimer <Nathanial.Lattimer at iismathwizard.com>
 */
public class SocketHandler {
    private SocketConnection _sock;
    private boolean _isConnected = false;
    
    public SocketHandler(SocketConnection sock)
    {
        this._sock = sock;
        _isConnected = true;
    }
    
    public String read()
    {
        BufferedReader input;
        String returnValue = null;
        try {
            input = new BufferedReader(new InputStreamReader(_sock.openInputStream()));
            returnValue = input.readLine();
            input.close();
        } catch (IOException ex) {
            
        }
        
        return returnValue;
    }
    
    public boolean write(String msg)
    {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(_sock.openOutputStream()));
            writer.write(msg);
            writer.flush();
            writer.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
        catch(Exception e)
        {
            return false;
        }
    }
    
    public boolean isConnected()
    {
        return this._isConnected;
    }
}
