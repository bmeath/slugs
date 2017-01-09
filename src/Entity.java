import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.*;
import shiffman.box2d.*;

public abstract class Entity
{
	SlugsGame p5;
	Box2DProcessing world;
	
	BodyDef bd;
	Body body;
	PolygonShape shape;
	FixtureDef fd;
	
	public Entity(SlugsGame p5, float x, float y, Box2DProcessing world, BodyType type, float density, float friction, float restitution)
	{
		this.p5 = p5;
		this.world = world;
		
		// define the physical properties
		bd = new BodyDef();
		bd.type = type;
		bd.fixedRotation = true;
		bd.position.set(world.coordPixelsToWorld(x, y));
		
		// create body
		body = world.createBody(bd);
		
		/* declare new fixture definition,
		 * and define some physical properties
		 */
		fd = new FixtureDef();
		fd.density = density;
		fd.friction = friction;
		fd.restitution = restitution;
	}
	
	public abstract void display();
}
