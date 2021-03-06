/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1799.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team1799.robot.commands.AutoCenterToLeft;
import org.usfirst.frc.team1799.robot.commands.AutoCenterToRight;
import org.usfirst.frc.team1799.robot.commands.AutoStraight;
import org.usfirst.frc.team1799.robot.subsystems.MecanumDriveTrain;
import org.usfirst.frc.team1799.robot.subsystems.ShooterSystem;

import org.usfirst.frc.team1799.robot.subsystems.ArmPWMsystem;
import org.usfirst.frc.team1799.robot.subsystems.CompressorSubsystem;
import org.usfirst.frc.team1799.robot.subsystems.GrabberSystem;
import org.usfirst.frc.team1799.robot.Robot;

/**
 *  This is the main class for running the WireUpV1 code.
 *  
 *  
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
	public static OI m_oi;

	// Initialize the our WireUpV1 subsystems
	public static final MecanumDriveTrain kDrivetrain = new MecanumDriveTrain();
	public static final CompressorSubsystem kcompressor = new CompressorSubsystem();
	public static final ShooterSystem kShooterSystem = new ShooterSystem();
	public static final GrabberSystem kGrabberSystem = new GrabberSystem();
	public static final ArmPWMsystem kArm = new ArmPWMsystem();
	
	public Command m_autonomousCommand;
	SendableChooser<String> m_chooser = new SendableChooser<>();
    public static String startingPosition;
    public static boolean switchOnLeft, scaleOnLeft;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_oi = new OI();
		
		// start the compressor
		kcompressor.start();
		
		
		SmartDashboard.putData("Auto mode", m_chooser);
		// SmartDashboard Buttons for robot subsystems initialized above
//		SmartDashboard.putData("Mecanum DriveTrain", kDrivetrain);
//		SmartDashboard.putData("Compressor Subsystem", kcompressor);
//		SmartDashboard.putData("Shooter System", kShooterSystem);
//		SmartDashboard.putData("Grabber System", kGrabberSystem);
//		SmartDashboard.putData("Arm PWM System", kArm);
		
		m_chooser.addDefault("Center", "center");
		m_chooser.addObject("Left", "left");
		m_chooser.addObject("Right", "right");
		
		new Thread(() -> {
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			camera.setResolution(640, 480);
		}).start();
		
		
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
//		m_autonomousCommand = m_chooser.getSelected();

//		Scheduler.getInstance().add(new ResetArmEncoder());
//		Scheduler.getInstance().add(new ResetWristEncoder());

		String msg = DriverStation.getInstance().getGameSpecificMessage();
		while (msg == null || msg.length() < 2) {
		    msg = DriverStation.getInstance().getGameSpecificMessage();
		}

		switchOnLeft = msg.substring(0, 1).equals("L");
		scaleOnLeft = msg.substring(1, 2).equals("L");
		startingPosition = m_chooser.getSelected();

		if (switchOnLeft) {
		    SmartDashboard.putString("Switch Position", "Left");
		} else {
		    SmartDashboard.putString("Switch Position", "Right");
		}

		if (scaleOnLeft) {
		    SmartDashboard.putString("Scale Position", "Left");
		} else {
		    SmartDashboard.putString("Scale Position", "Right");
		}

		if (startingPosition == "center" && switchOnLeft) {
		    m_autonomousCommand = new AutoCenterToLeft();
		} else if (startingPosition == "center" && !switchOnLeft) {
		    m_autonomousCommand = new AutoCenterToRight();
		} else if (startingPosition == "left" && switchOnLeft) {
		    m_autonomousCommand = new AutoStraight();
		} else if (startingPosition == "left" && !switchOnLeft) {
		    m_autonomousCommand = new AutoStraight();
		} else if (startingPosition == "right" && switchOnLeft) {
		    m_autonomousCommand = new AutoStraight();
		} else if (startingPosition == "right" && !switchOnLeft) {
		    m_autonomousCommand = new AutoStraight();
		} else {
		    m_autonomousCommand = null;
		}
		
		// schedule the autonomous command (example)
		if (m_autonomousCommand != null) {
			m_autonomousCommand.start();
		}
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		log();
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (m_autonomousCommand != null) {
			m_autonomousCommand.cancel();
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		log();
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
	/**
	 * This function is called periodically for all modes.
	 */
	@Override
	  public void robotPeriodic() {
		SmartDashboard.putData(Scheduler.getInstance());
//		kcompressor.sendInfo();
//		kShooterSystem.sendInfo();
//		kDrivetrain.sendInfo();
//		kGrabberSystem.sendInfo();
//		kArm.sendInfo();
		Scheduler.getInstance().run();
		log();
	  }
	
	/**
	 * Log interesting values to the SmartDashboard.
	 */
	private void log() {
//		Robot.pneumatics.writePressure();
//		SmartDashboard.putNumber("Pivot Pot Value", Robot.pivot.getAngle());
//		SmartDashboard.putNumber("Left Distance",
//				drivetrain.getLeftEncoder().getDistance());
//		SmartDashboard.putNumber("Right Distance",
//				drivetrain.getRightEncoder().getDistance());
		SmartDashboard.putString("Test Value to disply", "need to log more data");
		
	}
}
