/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import frc.robot.TriangleDrive;
//import com.kauailabs.navx.frc.AHRS;
import frc.robot.S_CurveMotionProfile;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
//import edu.wpi.first.wpilibj.GenericHID.RumbleType;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */



public class Robot extends TimedRobot {
  //private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private TriangleDrive TriDrive = new TriangleDrive();
  private XboxController Controller1 = new XboxController(0);
  ///private AHRS GyroscopeNavX = new AHRS();
  private ADXRS450_Gyro Gyroscope = new ADXRS450_Gyro();
  private S_CurveMotionProfile MotionCurve = new S_CurveMotionProfile();

  //Configurable Variables
  double DL=0.1,DeadzoneRightThirdPersonMode=0.9,DeadzoneRightFirstPersonMode=0.1;//Deadzone Values (DR should be at 0.9 (90%) otherwise the right joystick will become more sensitive as the joystick geets closer to the center)
  double RSpeedMultiplyer = .01;//A multiplyer of how fast the robot rotates when corecting itself using the gyroscope. 
  double DriverAngle=0;//The angle at which the driver is facing when starting and driving the robot.
  
  //Non Configurable Variables
  double TargetDirectionAngle=0,TargetRotation=0,RotationSpeed=0;//Rotation values
  double RX,RY,LX,LY;//Joystick values
  double ActualX,ActualY;//Robot X and Y speeds after adjustment for rotation
  boolean FirstPersonMode = false;


  @Override
  public void robotInit() {
    TriDrive.EnableSafety();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

/*
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }
*/
  /**
   * This function is called periodically during autonomous.
   */
  /*
   @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }
*/
  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    
    //The select button toggles the FirstPersonMode boolean
    if(Controller1.getBackButtonPressed()){
      if(FirstPersonMode==true){
        FirstPersonMode=false;
      }
      else{
        FirstPersonMode=true;
      }
    }
    
    //Set Joystick Values to Joysticks
    LX = Controller1.getX(Hand.kLeft);//Left Joystick, X axis 
    LY = Controller1.getY(Hand.kLeft);//Left Joystick, Y axis
    RX = Controller1.getX(Hand.kRight);//Right Joystick, X axis
    RY = Controller1.getY(Hand.kRight);//Right Joystick, Y axis

    //Dead Zone code for conrtoller joysticks (Circle Shaped)
    if (Math.sqrt(Math.pow(LX, 2)+Math.pow(LY, 2))<=DL){LX=0;LY=0;}//Left Joystick
    if(FirstPersonMode==false){
      if (Math.sqrt(Math.pow(RX, 2)+Math.pow(RY, 2))<=DeadzoneRightThirdPersonMode){RX=0;RY=0;}//Right Joystick
    }
    else{
      if (Math.sqrt(Math.pow(RX, 2)+Math.pow(RY, 2))<=DeadzoneRightFirstPersonMode){RX=0;RY=0;}//Right Joystick
    }
      
    //Figuring out the target rotation from the Right Joystick
    if(FirstPersonMode==false){
      if(RX!=0 && RY!=0){TargetRotation = Math.toDegrees(Math.atan2(RX*-1, RY*-1));}
      TargetDirectionAngle = Math.toDegrees(Math.atan2(LX*-1, LY*-1));
    }
    
    //Figure out ActualX and ActualY , these are the x and y variables after converting the joystick to a vector and back. 
    if(FirstPersonMode==false){
        //TargetDirectionAngle=TargetDirectionAngle-Gyroscope.roataion();
        //if(TargetDirectionAngle<-90 && TargetDirectionAngle<0){TargetDirectionAngle=180+TargetDirectionAngle;}//Finds the other side of the angle to the triangle can be solved.
        //if(TargetDirectionAngle>90 && TargetDirectionAngle>0){TargetDirectionAngle=180-TargetDirectionAngle;}//Remember to keep corect positive and negative for the correct quadrants.
      ActualX = (Math.sin(180-(90+Math.abs(TargetDirectionAngle)))*Math.sqrt(Math.pow(LX, 2)+Math.pow(LY, 2)));
        //ActualY = (Math.sqrt(Math.pow(LX, 2)+Math.pow(LY, 2)))/Math.sin(TargetDirectionAngle);
      //if(Gyroscope.getAngle()-TargetDirectionAngle>=-90 && Gyroscope.getAngle()-TargetDirectionAngle<=0){ActualX=-1*Math.abs(ActualX); ActualY=-1*Math.abs(ActualY);}
      //if(Gyroscope.getAngle()-TargetDirectionAngle<=90 && Gyroscope.getAngle()-TargetDirectionAngle>=0){ActualX=Math.abs(ActualX); ActualY=-1*Math.abs(ActualY);}
      //if(Gyroscope.getAngle()-TargetDirectionAngle>=90){ActualX=Math.abs(ActualX); ActualY=Math.abs(ActualY);}
      //if(Gyroscope.getAngle()-TargetDirectionAngle<=-90){ActualX=-1*Math.abs(ActualX); ActualY=Math.abs(ActualY);}
        //ActualX=LX;
      ActualY=LY;
    }
    else{
      ActualX=LX;
      ActualY=LY;
    }
    

    //Figure out how fast in what direction to rotate the robot to set and corect its rotation.
    if(FirstPersonMode==false){
      //if((-1*Gyroscope.getAngle())+TargetRotation<=180){RotationSpeed=MotionCurve.S_Curve((Gyroscope.getAngle()-TargetRotation)*RSpeedMultiplyer, 2);}
      if(TargetRotation<0){RotationSpeed=-1*MotionCurve.S_Curve(((TargetRotation-Gyroscope.getAngle())+360)*RSpeedMultiplyer, 2);}
    }
      else{RotationSpeed=RX;}

    //Limit Roataion speed from 1- to +1
    if(FirstPersonMode==false){
      if(RotationSpeed>1){RotationSpeed=1;} 
      else if(RotationSpeed<-1){RotationSpeed=-1;}
    }

    //Debug
      //Inputs
    SmartDashboard.putString("Inputs", "");
    SmartDashboard.putBoolean("FirstPersonMode", FirstPersonMode);
    SmartDashboard.putNumber("RightJoystickX", RX);
    SmartDashboard.putNumber("RightJoystickY", RY);
    SmartDashboard.putNumber("LeftJoystickX", LX);
    SmartDashboard.putNumber("LeftJoystickY", LY);
    SmartDashboard.putNumber("Gyroscope_Yaw", Gyroscope.getAngle());
      //Outputs
    SmartDashboard.putString("Outputs", "");
    SmartDashboard.putNumber("RightJoystickAngleDegrees", TargetRotation);
    SmartDashboard.putNumber("LeftJoystickAngleDegrees", (Math.toDegrees(Math.atan2(RY, RX))));
    SmartDashboard.putNumber("RotationSpeedBeforeCurve", RotationSpeed);
    SmartDashboard.putNumber("RotationAfterCurve", MotionCurve.S_Curve(RotationSpeed, 2));
    SmartDashboard.putNumber("X_Speed", ActualX);
    SmartDashboard.putNumber("Y_Speed", ActualY);

    //Move the Robot
    //TriDrive.MoveBot(ActualX, ActualY, RotationSpeed);
  }
}
