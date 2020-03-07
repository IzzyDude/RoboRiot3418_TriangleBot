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
import com.kauailabs.navx.frc.AHRS;
import frc.robot.S_CurveMotionProfile;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;


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
  private AHRS GyroscopeNavX = new AHRS();
  private S_CurveMotionProfile MotionCurve = new S_CurveMotionProfile();

  //Configurable Variables
  double DL=0.1,DeadzoneRightThirdPersonMode=0.9,DeadzoneRightFirstPersonMode=0.1;//Deadzone Values (DR should be at 0.9 (90%) otherwise the right joystick will become more sensitive as the joystick geets closer to the center)
  double RSpeedMultiplyer = .01;//A multiplyer of how fast the robot rotates when corecting itself using the gyroscope. 
  double DriverAngle=0;//The angle at which the driver is facing when starting and driving the robot.
  
  //Non Configurable Variables
  double TargetDirectionRotation=0,TargetRotation=0,RotationSpeed=0;//Rotation values
  double RX,RY,LX,LY;//Joystick values
  double ActualX=0,ActualY=0;//Robot X and Y speeds after acomidation for ratation
  int GyroSubtract=0;//The value to subtract from the gyroscop everytime it passes 360 degrees to make it match the behavior of the joystick.
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
    if(Controller1.getBackButton() && FirstPersonMode==true){Controller1.setRumble(RumbleType.kLeftRumble, .5);}
    else if(Controller1.getBackButton() && FirstPersonMode==false){Controller1.setRumble(RumbleType.kLeftRumble, 1);Controller1.setRumble(RumbleType.kRightRumble, 1);}
    
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
    
    //Figure out ActualX and ActualY , these are the x and y variables after converting the joystick to a vector and back. 
    //Not yet used
    ActualX = LX;
    ActualY = LY;
      
    //Figuring out the target rotation from the Right Joystick
    if(FirstPersonMode==false){TargetRotation = Math.toDegrees(Math.atan2(RY, RX));}//The target rotation angle of the whole robot is the inverse tangent on Right Joystick Y over Right Joystick X converted from radient to degrees as a double
    
    //Figure out how fast in what direction to rotate the robot to set and corect its rotation.
    if(FirstPersonMode==false){
      if((-1*GyroscopeNavX.getYaw())+TargetRotation<=180){RotationSpeed=MotionCurve.S_Curve((GyroscopeNavX.getYaw()-TargetRotation)*RSpeedMultiplyer, 2);}
      if((-1*GyroscopeNavX.getYaw())+TargetRotation>180){RotationSpeed=MotionCurve.S_Curve((-1*(TargetRotation-GyroscopeNavX.getYaw())+360)*RSpeedMultiplyer, 2);}
    }
      else{RotationSpeed=RX;}

    //Limit from 1- to +1
    if(FirstPersonMode==false){
      if(RotationSpeed>1){RotationSpeed=1;} 
      else if(RotationSpeed<-1){RotationSpeed=-1;}
    }

    //Debug
    SmartDashboard.putBoolean("FirstPersonMode", FirstPersonMode);
    SmartDashboard.putNumber("RightJoystickAnglRadians", Math.atan2(RY, RX));
    SmartDashboard.putNumber("RightJoystickAngleAfterDegreesConversion", (Math.toDegrees(Math.atan2(RY, RX))));
    SmartDashboard.putNumber("RightJoystickX", RX);
    SmartDashboard.putNumber("RightJoystickY", RY);
    SmartDashboard.putNumber("RotationSpeedBeforeCurve", RotationSpeed);
    SmartDashboard.putNumber("RotationAfterCurve", MotionCurve.S_Curve(RotationSpeed, 2));
    SmartDashboard.putNumber("X_Speed", ActualX);
    SmartDashboard.putNumber("Y_Speed", ActualY);
    SmartDashboard.putNumber("Gyroscope_Yaw", GyroscopeNavX.getYaw());

    //Move the Robot
    TriDrive.MoveBot(ActualX, ActualY, RotationSpeed);
  }
}
