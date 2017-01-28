package slugs;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import processing.core.PConstants;

import shiffman.box2d.Box2DProcessing;

public class Player extends Entity
{
	String name;
	Vec2 dir;
	
	public Player(Slugs p, Box2DProcessing world, Vec2 spawnPoint, float scaleFactor)
	{
		super(p, world, spawnPoint, BodyType.DYNAMIC, true, 1, 1f, 0f, 2);
		PolygonShape shape = new PolygonShape();
		// define the shape
		shape = new PolygonShape();
		
		// create main body
		float w = world.scalarPixelsToWorld(7 * scaleFactor);
		float h = world.scalarPixelsToWorld(12 * scaleFactor);
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
	    
	    RevoluteJointDef RevoluteJD = new RevoluteJointDef();
	    
	}
	
	// create player with default size
	public Player(Slugs p, Box2DProcessing world, Vec2 spawnPoint)
	{
		this(p, world, spawnPoint, 1f);
	}
	
	protected void update()
	{
		if (p.checkKey(PConstants.LEFT))
		{
			applyForce(new Vec2(-200, 0));
		}
		if (p.checkKey(PConstants.RIGHT))
		{
			applyForce(new Vec2(200, 0));
		}
		if (p.checkKey(PConstants.ENTER))
		{
			applyForce(new Vec2(0, 300));
		}
	}

}
