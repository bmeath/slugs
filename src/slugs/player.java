package slugs;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;

import processing.core.PConstants;

import shiffman.box2d.Box2DProcessing;

public class Player extends Entity
{
	String name;
	Vec2 dir;
	Body wheelBody;
	
	
	public Player(Slugs p, Box2DProcessing world, Vec2 spawnPoint, float scaleFactor)
	{
		super(p, world, spawnPoint, BodyType.DYNAMIC, true, 1, 1f, 0f);
		PolygonShape shape = new PolygonShape();
		// define the shape
		shape = new PolygonShape();
		
		// create main body
		float w = world.scalarPixelsToWorld(7 * scaleFactor);
		float h = world.scalarPixelsToWorld(12 * scaleFactor);
		shape.setAsBox(w, h);
		
		fd.shape = shape;
		body.createFixture(fd);
		
		// create wheel
		BodyDef wheelBD = new BodyDef();
		wheelBD.position = new Vec2(0, -h);
		wheelBD.type = BodyType.DYNAMIC;
		wheelBody = world.createBody(wheelBD);
		
		CircleShape wheelShape = new CircleShape();
	    wheelShape.m_radius = w;
	    //wheelShape.m_p.set(0, -h);
	    fd.shape = wheelShape;
	    fd.density = 1;
	    fd.friction = 1;
	    fd.restitution = 0;
	    body.createFixture(fd);
	}
	
	// create player with default size
	public Player(Slugs p, Box2DProcessing world, Vec2 spawnPoint)
	{
		this(p, world, spawnPoint, 1f);
	}
	
	protected void render()
	{
		update();
		p.fill(0, 200, 150);
		for (Fixture f = body.getFixtureList(); f != null; f = f.getNext())
		{
			if (f.getShape() instanceof PolygonShape)
			{
				PolygonShape shape = (PolygonShape) f.getShape();
				p.beginShape();
				for(int i = 0; i < shape.getVertexCount(); i++)
				{
					Vec2 v = world.vectorWorldToPixels(shape.getVertex(i));
					p.vertex(v.x, v.y);
				}
				p.endShape(PConstants.CLOSE);
			}
			
			if (f.getShape() instanceof CircleShape)
			{
				CircleShape shape = (CircleShape) f.getShape();
				Vec2 centre = world.vectorWorldToPixels(shape.m_p);
				float diameter = 2 * world.scalarWorldToPixels(shape.m_radius);
				p.ellipse(centre.x, centre.y, diameter, diameter);
			}
		}
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
