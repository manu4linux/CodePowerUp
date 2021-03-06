package org.usfirst.frc.team1799.robot.subsystems;

import org.usfirst.frc.team1799.robot.RobotMap;
import org.usfirst.frc.team1799.robot.commands.ArmStop;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class ArmPWMsystem extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

	private Victor m_leftMotor = new Victor(RobotMap.PWM.pwm0);
	private Victor m_rightMotor = new Victor(RobotMap.PWM.pwm0);
	
//	private SpeedControllerGroup m_ArmMotors = new SpeedControllerGroup(m_leftMotor,m_rightMotor);
	
	public ArmPWMsystem() {
//		addChild("Arm Motors",m_ArmMotors)
		addChild("Arm LEFT Motors",(Victor)m_leftMotor);
		addChild("Arm RIGHT Motors",(Victor)m_rightMotor);

		m_leftMotor.setInverted(RobotMap.leftmotorInverted);
		m_rightMotor.setInverted(RobotMap.rightmotorInverted);
	}
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new ArmStop());
    }
    

	public void stop() {
		m_leftMotor.set(0);
		m_rightMotor.set(0);
	}
	

	public void moveUp() {
		m_leftMotor.set(RobotMap.kArmSpeedUp);
		m_rightMotor.set(RobotMap.kArmSpeedUp);
	}
	

	public void moveDown() {
		m_leftMotor.set(RobotMap.kArmSpeedDown);
		m_rightMotor.set(RobotMap.kArmSpeedDown);
	}
	
	public void sendInfo() {
		SmartDashboard.putData(this);
		
	}
}

