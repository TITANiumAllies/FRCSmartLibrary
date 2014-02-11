/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.titaniumallies.frcsmartlibrary.comms;

import java.util.Vector;
import javax.microedition.io.SocketConnection;

/**
 *
 * @author Nathanial Lattimer <Nathanial.Lattimer at iismathwizard.com>
 */
public interface SocketHost {
    Vector sckHandlers = new Vector();
    public void add(SocketConnection sock);
}
