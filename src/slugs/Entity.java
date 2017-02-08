package slugs;

import java.util.ArrayList;

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
	ArrayList<Body> bodyList;
	FixtureDef fd;
	Box2DProcessing world;
	int colour;
	
	public Entity(Slugs p, Box2DProcessing world, Vec2 spawnPoint, BodyType bodyType, boolean fixedRotation, 
			float density, float friction, float restitution)
	{
		bodyList = new ArrayList<Body>();
		this.p = p;
		this.world = world;
		
		this.colour = p.color(255, 255, 255);
		
		// define the physical properties
		bd = new BodyDef();
		bd.type = bodyType;
		bd.fixedRotation = fixedRotation;
		bd.position.set(world.coordPixelsToWorld(spawnPoint));
		
		// create body
		bodyList.add(world.createBody(bd));
		
		/* declare new fixture definition,
		 * and define some physical properties
		 */
		fd = new FixtureDef();
		fd.restitution = restitution;
		fd.friction = friction;
		fd.density = density;
	}
	
	abstract protected void update();
	
	// get screen location of player
	public Vec2 getPixelLocation()
	{
		/* give location of first body in the list.
		 */
		return world.getBodyPixelCoord(bodyList.get(0));
	}
	
	// get box2d location of player
	public Vec2 getWorldLocation()
	{
		return world.coordPixelsToWorld(getPixelLocation());
	}
	
	/* does the universal setup and tear down work,
	 * then lets the subclass decide the rest.
	 */
	public void display()
	{
		for(int i = 0; i < bodyList.size(); i++)
		{
			Vec2 pos = world.getBodyPixelCoord(bodyList.get(i));
			p.pushMatrix();
			p.translate(pos.x, pos.y);
			p.rotate(-bodyList.get(i).getAngle());
			
			// draw to screen as per definition by subclass
			render(bodyList.get(i));
			
			p.popMatrix();
		}
		update();
	}
	
	/* wrapper method to apply a force to an entity's body */
	public void applyForce(Vec2 force)
	{
		// implicitly choose the first body in the list
		bodyList.get(0).applyForce(force, bodyList.get(0).getWorldCenter());
	}
	
	/* applies to body number i in the list*/
	public void applyForce(Vec2 force, int i)
	{
		bodyList.get(i).applyForce(force, bodyList.get(i).getWorldCenter());
	}
	
	// where the actual drawing code is placed.
	protected void render(Body b)
	{
		p.fill(colour);
		p.stroke(0);
		p.strokeWeight(1);
		
		for (Fixture f = b.getFixtureList(); f != null; f = f.getNext())
		{
			Object o = f.getShape();
			if (o instanceof PolygonShape)
			{
				PolygonShape shape = (PolygonShape) o;
				p.beginShape();
				for(int i = 0; i < shape.getVertexCount(); i++)
				{
					Vec2 v = world.vectorWorldToPixels(shape.getVertex(i));
					p.vertex(v.x, v.y);
				}
				p.endShape(PConstants.CLOSE);
			}
			
			if (o instanceof CircleShape)
			{
				CircleShape shape = (CircleShape) o;
				Vec2 centre = world.vectorWorldToPixels(shape.m_p);
				float diameter = 2 * world.scalarWorldToPixels(shape.m_radius);
				p.ellipse(centre.x, centre.y, diameter, diameter);
			}
		}
	}
}
