package frc.robot;

import java.lang.Math;

public class S_CurveMotionProfile{
    public double S_Curve(double Input, double Scale){
        if(Input <= .5 && Input >= -.5){Input = Math.pow( Input, Scale ) * Math.pow( 2, (Scale-1) );}//From x = 0 to x = .5 the first porabola has a minimum at the orgin.
        if(Input > .5 && Input <= 1){Input = (( -1 * Math.pow( Math.abs(Input-1), Scale )) * (Math.pow( 2, (Scale-1) ))) +1;}//From x = .5 to x = 1 the second porabola is fliped updide down, and has its maximum at x = 1, y = 1.
        if(Input*-1 > .5 && Input*-1 <= 1){Input = (( -1 * Math.pow( Math.abs((Input*-1)-1), Scale )) * (Math.pow( 2, ((Scale*-1)-1) ))) +1;}//Same as above but mirrored in the x axis
        return Input;
    }
}