/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.titaniumallies.frcsmartlibrary;

import com.titaniumallies.frcsmartlibrary.iismathwizardcomms.networking.Client;
import com.titaniumallies.frcsmartlibrary.iismathwizardcomms.networking.FailedToConnectException;
import com.titaniumallies.frcsmartlibrary.iismathwizardcomms.networking.NewSocketConnectionListener;
import com.titaniumallies.frcsmartlibrary.iismathwizardcomms.networking.Server;
import com.titaniumallies.frcsmartlibrary.iismathwizardcomms.networking.SocketHandler;
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
    
    public static void addJoystick(Joystick joy)
    {
        joys.add(joy);
    }
    
    public static void addMotorEncPair(SpeedController mtr, Encoder enc)
    {
        MotorEncoderPair pair = new MotorEncoderPair(mtr, enc);
        mtrEncPairs.add(pair);
    }
    
    public static void registerCamera(AxisCamera camera)
    {
        cam = camera;
    }
    
    public static void start(String ip)
    {
        try {
            client = new Client(ip);
        } catch (FailedToConnectException ex) {
            ex.printStackTrace();
            client = null;
        }
    }
    public static void start(String ip, int port)    
    {
        try {
            client = new Client(ip, port);
        } catch (FailedToConnectException ex) {
            ex.printStackTrace();
            client = null;
        }
    }
    public static void start(int port)
    {
        server = new Server(port);
        
    }
    public static void start(int[] ports)
    {
        server = new Server(ports);
    }
    
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
            message += "MOTORENC" + count + ":MTR=" + pair.mtr.get() + "ENC=" + pair.enc.getRate() + ";";
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