/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.titaniumallies.frcsmartlibrary.comms;


/**
 *
 * @author Nathanial Lattimer <Nathanial.Lattimer at iismathwizard.com>
 */
public interface NewSocketConnectionListener {
    public void newConnection(SocketHandler handle);
    public void subscribed(Server server);
    public void unsubscribed(Server server);
}
