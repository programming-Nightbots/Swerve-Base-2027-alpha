package frc.robot.opmodes.auto;

import com.pathplanner.lib.command3.AutoBuilder;
import com.pathplanner.lib.command3.NamedCommands;
import frc.robot.Robot;
import org.wpilib.command3.Command;
import org.wpilib.command3.button.RobotModeTriggers;
import org.wpilib.opmode.Autonomous;
import org.wpilib.opmode.OpMode;
import org.wpilib.tunable.Selectable;
import org.wpilib.tunable.Tunables;


@Autonomous(name = "PathPlanner Auto")
public class AutoOpMode implements OpMode {
  private final Selectable<Command> autoChooser;

  public AutoOpMode(Robot robot) {
    // 1. Register any NamedCommands used in your PathPlanner .auto files BEFORE building the chooser
    // NamedCommands.registerCommand("Intake", robot.intakeMechanism.intakeCommand());

    // 2. Build the Selectable<Command> dropdown and publish to Tunables
    autoChooser = AutoBuilder.buildAutoChooser("Default Auto");
    Tunables.publish("Auto Selector", autoChooser);

    // 3. Schedule whichever auto is selected in the dropdown when Autonomous enables
    RobotModeTriggers.autonomous().whileTrue(
    Command.noRequirements(coroutine -> {
      Command selectedAuto = autoChooser.getSelected();
        if (selectedAuto != null) {
          coroutine.await(selectedAuto);
        }
      }).named("Run Selected Auto")
    );  
  }

  @Override
  public void close() {
    Tunables.remove("Auto Selector");
  }
}
