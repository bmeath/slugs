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
	
	Vec2 left;
	Vec2 right;
	Vec2 jump;
	int lastJumped;
	boolean dir; // true if facing right, false otherwise
	
	RevoluteJoint motor;
	private boolean grounded;
	
	
	public Player(Slugs p, Box2DProcessing world, Vec2 spawnPoint, float scaleFactor)
	{
		super(p, world, spawnPoint, BodyType.DYNAMIC, true, 1, 1f, 0f, 2);
		left = new Vec2(-200 * scaleFactor, 0);
		right = new Vec2(200 * scaleFactor, 0);
		jump = new Vec2(0, 850 * scaleFactor);
		dir = true;
		lastJumped = p.millis();
		
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
		if (p.checkKey(PConstants.LEFT))
		{
			motor.setMotorSpeed(5);
			dir = false;
		}
		else if (p.checkKey(PConstants.RIGHT))
		{
			motor.setMotorSpeed(-5);
			dir = true;
		}
		else
		{
			motor.setMotorSpeed(0);
		}
		if (p.checkKey(PConstants.ENTER))
		{
			if(grounded == true)
			{
				if(p.millis() > lastJumped + 1000)
				{
					applyForce(jump.add(dir ? right : left));
					lastJumped = p.millis();
					grounded = false;
				}
			}
		}
	}

}
