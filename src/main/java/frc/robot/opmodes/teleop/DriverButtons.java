package frc.robot.opmodes.teleop;

import static org.wpilib.units.Units.Meters;

import org.wpilib.command3.Command;
import org.wpilib.command3.button.CommandXboxController;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;

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

        driverXbox.b().whileTrue(robot.swerve.driveToPose(new Pose2d(Meters.of(3), Meters.of(3),
                                                                 Rotation2d.fromDegrees(180))));

    }
}
