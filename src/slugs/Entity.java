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
	
	public Entity(Slugs p5, Box2DProcessing world, Vec2 spawnPoint, BodyType bodyType, boolean fixedRotation, 
			float density, float friction, float restitution)
	{
		this.p5 = p5;
		this.world = world;
		
		// define the physical properties
		bd = new BodyDef();
		bd.type = bodyType;
		bd.fixedRotation = fixedRotation;
		bd.position.set(world.coordPixelsToWorld(spawnPoint.x, spawnPoint.y));
		
		// create body
		body = world.createBody(bd);
		
		/* declare new fixture definition,
		 * and define some physical properties
		 */
		fd = new FixtureDef();
	}
	
	/* repetitive setup/teardown process is contained here,
	 */
	public void display()
	{
		Vec2 pos = world.getBodyPixelCoord(body);
		p5.pushMatrix();
		p5.translate(pos.x, pos.y);
		p5.rotate(-body.getAngle());
		
		// draw to screen as per definition by subclass
		render();
		
		p5.popMatrix();
	}
	
	// where the actual drawing code is placed.
	abstract protected void render();
}
