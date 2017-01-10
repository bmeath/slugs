package slugs;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public abstract class Entity
{
	Slugs p5;
	
	BodyDef bd;
	Body body;
	FixtureDef fd;
	
	public Entity(Slugs p5, float x, float y, BodyType type, boolean fixedRotation)
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
	
	/* call this if shape needs a translation and rotation
	 */
	public void display()
	{
		Vec2 pos = p5.world.getBodyPixelCoord(body);
		p5.pushMatrix();
		p5.translate(pos.x, pos.y);
		p5.rotate(-body.getAngle());
		
		// display shape as defined by subclass
		drawEntity();
		
		p5.popMatrix();
	}
	
	/* where the actual drawing code is placed.
	 * subclass could call this directly
	 * if no translation/rotation is needed, e.g the map
	 */
	abstract protected void drawEntity();
}
