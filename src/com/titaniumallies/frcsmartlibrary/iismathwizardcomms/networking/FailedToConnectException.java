/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.titaniumallies.frcsmartlibrary.iismathwizardcomms.networking;

/**
 *
 * @author Nathanial Lattimer <Nathanial.Lattimer at iismathwizard.com>
 */
public class FailedToConnectException extends Exception{
    public FailedToConnectException(String message)
    {
        super(message);
    }
    
    public FailedToConnectException()
    {
        super();
    }
}
