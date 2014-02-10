package com.titaniumallies.frcsmartlibrary;

import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author Nathanial Lattimer <Nathanial.Lattimer at iismathwizard.com>
 */
public class SmartJoystick{
    private Joystick _joy;
    private boolean _isRising = false;
    private boolean _toggleState = false;
    private int _invertScalarY = 1;
    private int _invertScalarX = 1;
            
    public SmartJoystick(int port)
    {
        this(port, false, false);
    }
    public SmartJoystick(int port, boolean invertY)
    {
        this(port, false, invertY);
    }
    public SmartJoystick(int port, boolean invertX, boolean invertY)
    {
        _joy = new Joystick(port);
        
        _invertScalarX = invertX ? -1 : 1; // ?:D
        _invertScalarY = invertY ? -1 : 1;
    }
    
    public Joystick getJoystick()
    {
        return _joy;
    }
    public boolean isInvertedY()
    {
        return _invertScalarY == -1;
    }
    public boolean isInvertedX()
    {
        return _invertScalarX == -1;
    }
    
    public boolean getToggleButton(int buttonnum)
    {
        if(_isRising && !_joy.getRawButton(buttonnum)) //button was already true and we just let go (toggle effect)
        {
            _isRising = false;
            _toggleState = _toggleState ^ true;
            return _toggleState;
        }
        _isRising = _joy.getRawButton(buttonnum);
        _toggleState = _toggleState ^ false;
        return _toggleState;
    }
    public boolean getRawButton(int buttonnum)
    {
        return _joy.getRawButton(buttonnum);
    }
    
    public double getX()
    {
        return _joy.getX() * _invertScalarX;
    }
    public double getY()
    {
        return _joy.getY() * _invertScalarY;
    }    
}