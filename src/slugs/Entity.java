package slugs;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import processing.core.PConstants;
import shiffman.box2d.Box2DProcessing;

public abstract class Entity
{
	Slugs p;
	BodyDef bd;
	Body[] bodyList;
	FixtureDef fd;
	Box2DProcessing world;
	Vec2 pos;
	
	public Entity(Slugs p, Box2DProcessing world, Vec2 spawnPoint, BodyType bodyType, boolean fixedRotation, 
			float density, float friction, float restitution, int bodyCount)
	{
		bodyList = new Body[bodyCount];
		this.p = p;
		this.world = world;
		
		// define the physical properties
		bd = new BodyDef();
		bd.type = bodyType;
		bd.fixedRotation = fixedRotation;
		bd.position.set(world.coordPixelsToWorld(spawnPoint));
		
		// create body
		bodyList[0] = world.createBody(bd);
		
		/* declare new fixture definition,
		 * and define some physical properties
		 */
		fd = new FixtureDef();
		fd.restitution = restitution;
		fd.friction = friction;
		fd.density = density;
	}
	
	abstract protected void update();
	
	/* does the universal setup and tear down work,
	 * then lets the subclass decide the rest.
	 */
	public void display()
	{
		update();
		for(int i = 0; i < bodyList.length; i++)
		{
			pos = world.getBodyPixelCoord(bodyList[i]);
			p.pushMatrix();
			p.translate(pos.x, pos.y);
			p.rotate(-bodyList[i].getAngle());
			
			// draw to screen as per definition by subclass
			render(bodyList[i]);
			
			p.popMatrix();
		}
	}
	
	/* wrapper method to apply a force to the centre of the entity's body */
	public void applyForce(Vec2 force)
	{
		// implicitly choose the first body in the array
		bodyList[0].applyForce(force, bodyList[0].getWorldCenter());
	}
	
	/* applies to a body at given index i */
	public void applyForce(Vec2 force, int i)
	{
		bodyList[i].applyForce(force, bodyList[i].getWorldCenter());
	}
	
	// where the actual drawing code is placed.
	protected void render(Body b)
	{
		for (Fixture f = b.getFixtureList(); f != null; f = f.getNext())
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
}
