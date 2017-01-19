package slugs;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import shiffman.box2d.Box2DProcessing;

public abstract class Entity
{
	Slugs p5;
	
	BodyDef bd;
	Body body;
	FixtureDef fd;
	Box2DProcessing world;
	
	Vec2 pos;
	
	public Entity(Slugs p5, Box2DProcessing world, Vec2 spawnPoint, BodyType bodyType, boolean fixedRotation, 
			float density, float friction, float restitution)
	{
		this.p5 = p5;
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
	}
	
	/* does the universal setup and teardown work,
	 * then lets the subclass decide the rest.
	 */
	public void display()
	{
		pos = world.getBodyPixelCoord(body);
		System.out.println(pos.toString());
		p5.pushMatrix();
		
		// draw to screen as per definition by subclass
		render();
		
		p5.popMatrix();
	}
	
	// where the actual drawing code is placed.
	abstract protected void render();
}
