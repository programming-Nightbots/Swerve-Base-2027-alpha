package frc.robot;

import org.wpilib.framework.TimedRobot;
import org.wpilib.command2.Command;
import org.wpilib.command2.CommandScheduler;
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  private final RobotContainer m_robotContainer;

  

  public Robot() {
    m_robotContainer = new RobotContainer();
     
  }
  @Override
  public void robotInit() {

  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
    m_robotContainer.updateDashboard();
  }

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      CommandScheduler.getInstance().schedule(m_autonomousCommand);
    }
  }

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  // Other periodic methods remain empty or unchanged as per standard WPILib templates
  @Override 
  public void disabledInit() 
  {

  }
  @Override public void disabledPeriodic() 
  {

  }
  @Override public void autonomousPeriodic() 
  {

  }
  @Override public void teleopPeriodic() 
  {

  }
  @Override public void utilityInit() 
  { 
    CommandScheduler.getInstance().cancelAll(); 
  }
  @Override public void utilityPeriodic() 
  {
    
  }
}
