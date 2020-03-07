package frc.robot;

import edu.wpi.first.wpilibj.PWMVictorSPX;

public class TriangleDrive {

    private PWMVictorSPX Motor1 = new PWMVictorSPX(0);
    private PWMVictorSPX Motor2 = new PWMVictorSPX(1);
    private PWMVictorSPX Motor3 = new PWMVictorSPX(2);
    
    double LX=0,LY=0,RX=0; //Movement Variables
    double[] Average = new double[3];//Averaging numbers
    double Divide = 2;

    public void EnableSafety(){
        Motor1.setSafetyEnabled(true);
        Motor2.setSafetyEnabled(true);
        Motor3.setSafetyEnabled(true);
    }
    public void DisableSafety(){
        Motor1.setSafetyEnabled(false);
        Motor2.setSafetyEnabled(false);
        Motor3.setSafetyEnabled(false);
    }

    public void MoveBot(double X, double Y, double Rotation){
        //Sets X , Y and Rotation speed to X, Y and Yaw
        LX = X;
        LY = Y;
        RX = Rotation;

        //Figures out averaging stuff
        if(RX!=0){Average[0]++;Average[1]++;Average[2]++;}else if(LY==0 && LX==0){Average[0]=1;Average[1]=1;Average[2]=1;}
        if(LY!=0){Average[0]++;Average[1]++;}else {Average[0]=1;Average[1]=1;}
        if(LX!=0){Average[2]++;}else{Average[2]=1;}

        //Sets motor speeds
        Motor1.set(((LY*-1)+(LX/Divide)+RX)/Average[0]);//Invert motor when moving forward and average with half of X speed and Rotation speed
        Motor2.set((LY+(LX/Divide)+RX)/Average[1]);//Average Forward speed with half of X speed and rotation speed
        Motor3.set(((((LX)*-1)+RX)/Average[2]));//Average Inverted X speed with rotation speed 

        //Clears the average variables
            Average[0]=0;
            Average[1]=0;
            Average[2]=0;
    }    
}