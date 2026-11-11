package frc.robot;

import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.Distance;
import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.Inches;

public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
    public static final double DEADBAND = 0.15;
  }

  public static class SwerveConstants {
    public static final Distance POSE_TRANSLATION_TOLERANCE =  Inches.of(2);
    public static final Angle POSE_ROTATION_TOLERANCE =  Degrees.of(5);
  }
}
