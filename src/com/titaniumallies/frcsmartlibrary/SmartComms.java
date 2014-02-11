/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.titaniumallies.frcsmartlibrary;

import com.titaniumallies.frcsmartlibrary.comms.Client;
import com.titaniumallies.frcsmartlibrary.comms.FailedToConnectException;
import com.titaniumallies.frcsmartlibrary.comms.NewSocketConnectionListener;
import com.titaniumallies.frcsmartlibrary.comms.Server;
import com.titaniumallies.frcsmartlibrary.comms.SocketHandler;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.networktables2.util.List;
import java.util.Vector;

/**
 * This class allows for communicating vital information to other machines
 * @author Nathanial Lattimer <Nathanial.Lattimer at iismathwizard.com>
 */
public class SmartComms {
    /*
     * Vital information would be:
     *      Motor outputs (set value/ encoder feedback)
     *          Perceived speed
     *      Joystick inputs
     *      Camera
     *      
     */
    private static List joys = new List();
    private static List mtrEncPairs = new List();
    private static AxisCamera cam = null;
    private static Server server = null;
    private static Client client = null;
    private static Vector connections = new Vector();
    
    /**
     * Add a joystick to the server for live updating
     * @param joy Joystick to add
     */
    public static void addJoystick(Joystick joy)
    {
        joys.add(joy);
    }
    
    /**
     * Add a motor/encoder pair to the server for live updating
     * @param mtr The motor to add (can be null)
     * @param enc The encoder to add (can be null)
     */
    public static void addMotorEncPair(SpeedController mtr, Encoder enc)
    {
        MotorEncoderPair pair = new MotorEncoderPair(mtr, enc);
        mtrEncPairs.add(pair);
    }
    
    /**
     * Set the server's camera
     * @param camera AxisCamera to add
     */
    public static void registerCamera(AxisCamera camera)
    {
        cam = camera;
    }
    
    /**
     * Start the client on an ip address. The client, upon successful connection, can now communicate. Defaults to port 1248
     * @param ip The ip address of another server
     */
    public static void start(String ip)
    {
        try {
            client = new Client(ip);
        } catch (FailedToConnectException ex) {
            ex.printStackTrace();
            client = null;
        }
    }
    
    /**
     * Start the client on an ip address with specified port. The client, upon successful connection, can now communicate
     * @param ip the ip address of another server
     * @param port the port to communicate on
     */
    public static void start(String ip, int port)    
    {
        try {
            client = new Client(ip, port);
        } catch (FailedToConnectException ex) {
            ex.printStackTrace();
            client = null;
        }
    }
    
    /**
     * start the server on a specified port
     * @param port the port to start the server with
     */
    public static void start(int port)
    {
        server = new Server(port);
        
    }
    
    /**
     * start the server on the specified ports
     * @param ports the ports to listen to
     */
    public static void start(int[] ports)
    {
        server = new Server(ports);
    }
    
    /**
     * get the string that is being sent out to all of the clients
     */
    public static String getCurrentCompiledInfo()
    {
        String message = "";
        
        for(int count = 0; count < joys.size(); count ++)
        {
            Joystick joy = ((Joystick)joys.get(count));
            message += "JOYSTICK" + count + ":X=" + joy.getX() + "Y=" + joy.getY() + ";";
        }
        
        for(int count = 0; count < mtrEncPairs.size(); count ++)
        {
            MotorEncoderPair pair = ((MotorEncoderPair)mtrEncPairs.get(count));
            message += "MOTORENC" + count + ":MTR=" + (pair.mtr != null ? pair.mtr.get() + "" : "null") + "ENC=" + (pair.enc != null ? pair.enc.getRate() + "" : "null") + ";";
        }
        String sub = "";
        
        try{
            int num = DriverStation.getInstance().getTeamNumber();
            sub = ((num - (num % 100))/100) + "." + (num % 100);
            sub = "10." + sub + ".12";
            sub = "ADDR=" + sub;
        }
        catch(Exception e)
        {
            //oh well, no address
        }
        
        message += "AXIS:FPS=" + cam.getMaxFPS() + sub + ";";
        return message;
    }
    
    private static class MotorEncoderPair
    {
        SpeedController mtr;
        Encoder enc;
        
        public MotorEncoderPair(SpeedController mtr, Encoder enc)
        {
            this.mtr = mtr;
            this.enc = enc;
        }
    }
    
    private static class listUpdator implements NewSocketConnectionListener
    {

        public void newConnection(SocketHandler handle) {
            connections.addElement(handle);
        }

        public void subscribed(Server server) {
            //nothing needed here
        }

        public void unsubscribed(Server server) {
            //nothing needed here
        }
    }
    
    private static class pureUpdator extends Thread
    {
        public void run()
        {
            while(this.isAlive())
            {
                for(int count = 0; count < connections.size(); count ++)
                {
                    SocketHandler handle = (SocketHandler) connections.elementAt(count);
                    
                    if(!handle.write(getCurrentCompiledInfo())){ //did the write fail? if so it might be disconnected
                        connections.removeElementAt(count);
                        count --; //we remove this element so we should re-check this location
                    }
                }
                Timer.delay(0.2);
            }
        }
    }
    
}