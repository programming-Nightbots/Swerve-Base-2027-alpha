package frc.robot.mechanisms;

import java.io.File;
// import java.io.IOException;
// import java.util.function.Supplier;

import org.wpilib.math.controller.PIDController;

// import org.json.simple.parser.ParseException;

// import com.pathplanner.lib.auto.AutoBuilder; UN COMMENT PATH PLANNER STUFF WHEN PATH PLANNER BEING SETUP
// import com.pathplanner.lib.config.PIDConstants;
// import com.pathplanner.lib.config.RobotConfig;
// import com.pathplanner.lib.controllers.PPHolonomicDriveController;

import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.kinematics.ChassisVelocities;
import org.wpilib.driverstation.MatchState;
import org.wpilib.driverstation.RobotState;
import org.wpilib.driverstation.Alliance;
// import org.wpilib.driverstation.MatchType;
// import org.wpilib.driverstation.DriverStationErrors;
import org.wpilib.system.Filesystem;
import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.Distance;
import org.wpilib.smartdashboard.Field2d;
import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
// import frc.robot.Constants;
import swervelib.commands3.SwerveParser;
import yams.commands3.swerve.*;
import yams.commands3.config.SwerveDriveConfig;
import yams.core.telemetry.SwerveDriveTelemetryConfig;
import yams.core.telemetry.enums.TelemetryVerbosity;

import static frc.robot.Constants.SwerveConstants.*;
/**
 * SwerveSubsystem manages the 4-wheel independent drive modules.
 * In FRC, a "Subsystem" is like a body part of the robot. This one handles movement.
 * It uses YAGSL (Yet Another Generic Swerve Library) to handle the complex 
 * trigonometry required to make 4 wheels move together as one drivetrain.
 */
public class SwerveMechanism implements Mechanism {
  
  // The 'swerveDrive' object is the brain of the drivetrain provided by YAGSL.
  private final SwerveDrive swerveDrive;
  
  // Field2d is a utility that lets us see a virtual robot icon on our 
  // laptop dashboard (Glass/Shuffleboard) to verify our position on the field.
  private final Field2d m_field = new Field2d();
  
  // A safety check to ensure PathPlanner (auto-navigation) is ready to use.
  private boolean pathPlannerConfigured = false;

  // At power-on the gyro reads 0 whichever way the robot faces. Same convention as the Y button:
  // the robot starts facing AWAY from its own driver station, which is field heading 0 on Blue
  // and 180 on Red. Until the alliance is known (or Y / an auto sets the pose), the heading is
  // 180 deg wrong on Red, which flips field-oriented driving.
  private boolean headingSeeded = false;

  /**
   * CONSTRUCTOR: This runs once when the robot code starts up.
   */
  public SwerveMechanism() {
    // 1. Build your YAMS configuration profile
    var cfg = new SwerveDriveConfig()
        .withStartingPose(new Pose2d(3, 3, new Rotation2d()))
        .withMechanism(this) // Registers the subsystem framework directly to YAMS
         .withTranslationController(new PIDController(4, 0, 0))
        // YAMS stops any module asked for less than 0.1 m/s (its default module velocity deadband), so a weak
        // rotation gain leaves a heading error it can never close. At kP = 3 that dead zone is under 4.5 degrees,
        // inside driveToPose's 5 degree tolerance.
        .withRotationController(new PIDController(3, 0, 0))
        .withTelemetry("Swerve", new SwerveDriveTelemetryConfig(TelemetryVerbosity.HIGH));

    // 2. Parse the JSON config directory statically into YAGSL memory
    SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve"));

    // 3. Statically instantiate your YAMS-powered SwerveDrive instance 
    this.swerveDrive = SwerveParser.createSwerveDrive(cfg);
    
    //_______________________________________________OLD____________________
    // YAGSL looks for JSON configuration files in the 'deploy/swerve' folder.
    // These files define motor IDs, gear ratios, and wheel placement.
    // File directory = new File(Filesystem.getDeployDirectory(), "swerve");
    // try {
    //   // Initialize the drivetrain using the files in that directory and the max speed from Constants.
    //   swerveDrive = new SwerveParser(directory).createSwerveDrive(Constants.maxSpeed);
    // } catch (IOException e) {
    //   // If the config files are missing or broken, the code crashes here so we know immediately.
    //   throw new RuntimeException(e);
    // }

    // // Pushes the virtual field map to the dashboard so drivers can see robot positioning.
    // SmartDashboard.putData("Field", m_field);

    // // Initialize the autonomous navigation settings.
    // setupPathPlanner();
  }

//   /**
//    * Configures PathPlanner, which is the software used to follow pre-drawn paths.
//    */
//   public final void setupPathPlanner() {
//     try {
//       // RobotConfig reads the physical robot data (mass, MOI, etc.) from GUI settings.
//       RobotConfig config = RobotConfig.fromGUISettings();

//       // Configure the AutoBuilder: This maps PathPlanner's "brain" to our robot's "muscles."
//       AutoBuilder.configure(
//           this::getPose,                // Function to get current robot position (X, Y, Rotation)
//           swerveDrive::resetOdometry,   // Function to 'teleport' the robot's internal map position
//           swerveDrive::getRobotVelocity,// Function to see how fast the robot is currently moving
//           (speeds, feedforwards) -> swerveDrive.drive(speeds), // Function to physically move the motors
//           new PPHolonomicDriveController(
//               // PIDConstants: These control how hard the robot fights to stay on a path.
//               // If the robot wobbles, these numbers are too high. If it's lazy, they are too low.
//               // Starting point (PathPlanner/YAGSL example values). If autos oscillate, lower them;
//               // if the robot drifts off the path, raise them.
//               new PIDConstants(5.0, 0.0, 0.0), // Translation (Moving X and Y): m/s per meter of error
//               new PIDConstants(5.0, 0.0, 0.0)  // Rotation (Turning): rad/s per radian of error
//           ),
//           config,
// () -> {
//     // 1. Check if we are on Red Alliance
//     var alliance = MatchState.getAlliance();
//     boolean isRed = alliance.isPresent() && alliance.get() == Alliance.RED;
    
//     // 2. Read a True/False toggle from your Dashboard for starting side
//     // (True = Right side of alliance wall, False = Left side)
//     // 3. XOR Logic: If BOTH are true or BOTH are false, don't flip. 
//     // If only ONE is true, PathPlanner flips the path upside down!
//     return isRed ^ false; // Replace 'false' with your dashboard toggle if you have one !!!
// },
// this);

//       pathPlannerConfigured = true;
//     } catch (IOException | ParseException e) {
//       // If PathPlanner fails, we report an error to the Driver Station log.
//       pathPlannerConfigured = false;
//       DriverStationErrors.reportError("PathPlanner Config Failed: " + e.getMessage(), e.getStackTrace());
//     }
//   }

  /**
   * Drive command for the joysticks.
   * "Field Oriented" means pushing the stick forward always moves the robot away from the driver,
   * no matter which way the robot's front is pointing.
   */
  public void drive(ChassisVelocities velocity)
  {
    swerveDrive.setFieldRelativeChassisSpeeds(velocity);
  }

  /**
 * Resets the Gyro (Compass). NOT ALLIANCE RELATIVE ONLY FOR IF MESSED UP MID MATCH FACE AWAY FROM RED ALLIANCE WALL for it.
   */
  public Command zeroGyro() {
    return Command.requiring(this)
        .executing(coroutine -> {
            swerveDrive.zeroGyro();
            // This runs once and exits cleanly without a loop or yield
        })
        .named("Zero Gyro Fixed");
  }

  public Command lockPose() {
     return runRepeatedly( () ->
      {
        swerveDrive.lockPose();
        System.out.println("Lock Pose Running");
      }
    ).named("Lock Pose");
  }

  /**
   * Locks the wheels in an 'X' pattern.
   * This makes it very difficult for other robots to push us.
   */

  // public Command lockPoseCommand() {
  //   return run(() -> swerveDrive.lockPose());
  // }

  /**
   * Returns where the robot is on the field (X, Y in meters, and Rotation).
   */
  public Pose2d getPose() {
    return swerveDrive.getPose();
  }

  /**
   * Accessor method to get the raw SwerveDrive object if needed.
   */
  public SwerveDrive getSwerveDrive() {
    return swerveDrive;
  }

  /** The dashboard field ("Field"), so other code can draw extra objects on it. */
  public Field2d getField() {
    return m_field;
  }

  /**
   * Raw yaw straight from the Pigeon IMU (NOT fused odometry), as a Rotation2d,
   * CCW-positive. The turret-aim command differentiates this between loops to learn
   * how fast the chassis is spinning, so it can counter-rotate and stay field-locked.
   */
  public Rotation2d getGyroYaw() {
    return swerveDrive.getGyroRotation3d().toRotation2d();
  }

  /**
   * This runs every 20ms (50 times per second).
   * It is the "Heartbeat" of the subsystem.
   */
  public void periodic() {
    // Once, while disabled, as soon as the Driver Station reports the alliance: flip the
    // heading on Red (see headingSeeded). Keeps any turning done since power-on.
    if (!headingSeeded && RobotState.isDisabled()) {
      var alliance = MatchState.getAlliance();
      if (alliance.isPresent()) {
        if (alliance.get() == Alliance.RED) {
          Pose2d pose = swerveDrive.getPose();
          swerveDrive.resetOdometry(new Pose2d(pose.getTranslation(), pose.getRotation().plus(Rotation2d.k180deg)));
        }
        headingSeeded = true;
      }
    }

    // Vision corrections are now applied by VisionSubsystem, which feeds AprilTag
    // pose measurements straight into this drive's pose estimator.
    swerveDrive.updateTelemetry(); // Calculate new position based on motor rotations

    // Update the virtual robot icon on the dashboard so we can debug movement.
    m_field.setRobotPose(swerveDrive.getPose());
  }

  /** Call every loop in simulation. */
  public void simulationPeriodic()
  {
    swerveDrive.simIterate();
  }

  /**
   * Drive straight to the given pose with YAMS' {@link SwerveDrive#driveToPose(Pose2d, Distance, Angle)}, using the
   * translation and rotation PID controllers from the {@link SwerveDriveConfig}. There is no path planning (Commands
   * v3 has no PathPlanner support), so nothing steers it around obstacles.
   *
   * @param pose {@link Pose2d} to drive to. Field relative, blue-origin where 0deg is facing towards RED alliance.
   * @return {@link Command} that drives to the pose, ending once it is within 2in and 5deg of it.
   */
  public Command driveToPose(Pose2d pose)
  {
    return swerveDrive.driveToPose(pose, POSE_TRANSLATION_TOLERANCE, POSE_ROTATION_TOLERANCE);
  }
}