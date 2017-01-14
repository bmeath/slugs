package slugs;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public abstract class Entity
{
	Slugs p5;
	
	BodyDef bd;
	Body body;
	FixtureDef fd;
	
	public Entity(Slugs p5, float x, float y, BodyType type, boolean fixedRotation, 
			float density, float friction, float restitution)
	{
		this.p5 = p5;
		
		// define the physical properties
		bd = new BodyDef();
		bd.type = type;
		bd.fixedRotation = fixedRotation;
		bd.position.set(p5.world.coordPixelsToWorld(x, y));
		
		// create body
		body = p5.world.createBody(bd);
		
		/* declare new fixture definition,
		 * and define some physical properties
		 */
		fd = new FixtureDef();
	}
	
	/* repetitive setup/teardown process is contained here,
	 * subclass needs not worry about it.
	 */
	public void display()
	{
		Vec2 pos = p5.world.getBodyPixelCoord(body);
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
