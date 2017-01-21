package slugs;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import shiffman.box2d.Box2DProcessing;

public abstract class Entity
{
	Slugs p;
	
	BodyDef bd;
	Body body;
	FixtureDef fd;
	Box2DProcessing world;
	
	Vec2 pos;
	
	public Entity(Slugs p, Box2DProcessing world, Vec2 spawnPoint, BodyType bodyType, boolean fixedRotation, 
			float density, float friction, float restitution)
	{
		this.p = p;
		this.world = world;
		
		// define the physical properties
		bd = new BodyDef();
		bd.type = bodyType;
		bd.fixedRotation = fixedRotation;
		bd.position.set(world.coordPixelsToWorld(spawnPoint));
		
		// create body
		body = world.createBody(bd);
		
		/* declare new fixture definition,
		 * and define some physical properties
		 */
		fd = new FixtureDef();
		fd.restitution = restitution;
		fd.friction = friction;
		fd.density = density;
	}
	
	/* does the universal setup and tear down work,
	 * then lets the subclass decide the rest.
	 */
	public void display()
	{
		pos = world.getBodyPixelCoord(body);
		p.pushMatrix();
		p.translate(pos.x, pos.y);
		p.rotate(-body.getAngle());
		
		// draw to screen as per definition by subclass
		render();
		
		p.popMatrix();
	}
	
	/* wrapper method to apply a force to the centre of the entity's body */
	public void applyForce(Vec2 force)
	{
		body.applyForce(force, body.getWorldCenter());
	}
	
	// where the actual drawing code is placed.
	abstract protected void render();
}
