package slugs;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import processing.core.PConstants;

import shiffman.box2d.Box2DProcessing;

public class Player extends Entity
{
	String name;
	Vec2 dir;
	Vec2 jump;
	RevoluteJoint motor;
	private boolean grounded;
	
	public Player(Slugs p, Box2DProcessing world, Vec2 spawnPoint, float scaleFactor)
	{
		super(p, world, spawnPoint, BodyType.DYNAMIC, true, 1, 1f, 0f, 2);
		
		dir = new Vec2(1000, 0);
		jump = new Vec2(0, 850 * scaleFactor);
		
		PolygonShape shape = new PolygonShape();
		// define the shape
		shape = new PolygonShape();
		
		// create main body
		float w = world.scalarPixelsToWorld(4 * scaleFactor);
		float h = world.scalarPixelsToWorld(7 * scaleFactor);
		shape.setAsBox(w, h);
		
		fd.shape = shape;
		bodyList[0].createFixture(fd);
		
		// create wheel
		BodyDef wheelBD = new BodyDef();
		wheelBD.type = BodyType.DYNAMIC;
		
		Vec2 wheelPos = world.coordPixelsToWorld(spawnPoint);
		wheelBD.position.set(new Vec2(wheelPos.x, wheelPos.y - h));
		
		bodyList[1] = world.createBody(wheelBD);
		
		CircleShape wheelShape = new CircleShape();
	    wheelShape.m_radius = w;
	    fd.shape = wheelShape;
	    bodyList[1].createFixture(fd);
	    bodyList[1].setUserData(this);
	    
	    RevoluteJointDef revJD = new RevoluteJointDef();
	    revJD.initialize(bodyList[0], bodyList[1], bodyList[1].getWorldCenter());
	    revJD.motorSpeed = -PConstants.PI*2;
	    revJD.maxMotorTorque = 300f;
	    revJD.enableMotor = true;
	    motor = (RevoluteJoint) world.createJoint(revJD);
	}
	
	// create player with default size
	public Player(Slugs p, Box2DProcessing world, Vec2 spawnPoint)
	{
		this(p, world, spawnPoint, 1f);
	}
	
	public boolean isGrounded()
	{
		return grounded;
	}
	
	public void setGrounded(boolean isGrounded)
	{
		this.grounded = isGrounded;
	}
	
	protected void update()
	{
		System.out.println(grounded);
		if (p.checkKey(PConstants.LEFT))
		{
			motor.setMotorSpeed(5);
		}
		else if (p.checkKey(PConstants.RIGHT))
		{
			motor.setMotorSpeed(-5);
		}
		else
		{
			motor.setMotorSpeed(0);
		}
		if (p.checkKey(PConstants.ENTER))
		{
			if(grounded == true)
			{
				applyForce(jump);
				grounded = false;
			}
		}
	}

}
