package frc.robot;

import org.wpilib.framework.OpModeRobot;
import frc.robot.mechanisms.SwerveMechanism;
import org.wpilib.command3.*;
public class Robot extends OpModeRobot {

  public final SwerveMechanism swerve = new SwerveMechanism();

  private final Scheduler scheduler = Scheduler.getDefault();

  @Override
  public void robotPeriodic()
  {
    swerve.periodic();
    scheduler.run();
  }

  @Override
  public void simulationPeriodic()
  {
    swerve.simulationPeriodic();
  }
}
