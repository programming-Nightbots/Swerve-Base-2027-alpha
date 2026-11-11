package frc.robot.opmodes.teleop;

import org.wpilib.command3.Command;
import org.wpilib.command3.button.CommandXboxController;

import frc.robot.Robot;

final public class DriverButtons {
    public DriverButtons() {
    }

    static void bind(Robot robot, CommandXboxController driverXbox){
        
        driverXbox.x().whileTrue(robot.swerve.lockPose());
            
        driverXbox.y().onTrue(
            Command.noRequirements(coroutine -> {
            robot.swerve.getSwerveDrive().zeroGyro();
        }).named("Zero Gyro"));

    }
}
