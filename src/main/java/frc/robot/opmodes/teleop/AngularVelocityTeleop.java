package frc.robot.opmodes.teleop;

import frc.robot.Robot;
import org.wpilib.command3.Command;
import org.wpilib.command3.button.CommandXboxController;
import org.wpilib.command3.button.RobotModeTriggers;
import org.wpilib.driverstation.XboxController;
import org.wpilib.opmode.OpMode;
import org.wpilib.opmode.Teleop;
import yams.commands3.swerve.SwerveInputStream;

@Teleop(name = "Angular Velocity Control")
public class AngularVelocityTeleop implements OpMode
{

  private final CommandXboxController driverXbox = new CommandXboxController(0);
  private final SwerveInputStream     driveStream;

  public AngularVelocityTeleop(Robot robot)
  {
    // Xbox sticks read negative when pushed forward/left, so every axis is negated.
    XboxController hid = driverXbox.getController();
    driveStream = SwerveInputStream.of(robot.swerve.getSwerveDrive(), () -> -hid.getLeftY(), () -> -hid.getLeftX())
        .withControllerRotationAxis(() -> -hid.getRightX())
        .withDeadband(0.05)
        .withAllianceRelativeControl();

    Command driveCommand = robot.swerve.run(coroutine -> {
      while (true)
      {
        robot.swerve.drive(driveStream.get());
        coroutine.yield();
      }
    }).named("Drive Angular Velocity");

    RobotModeTriggers.teleop().whileTrue(driveCommand);

    // Resets gyro using no requirments so it wont crash, only needed irl sim cant use
    driverXbox.y().onTrue(
      Command.noRequirements(coroutine -> {
          robot.swerve.getSwerveDrive().zeroGyro();
        }).named("Zero Gyro")
);
  }
}