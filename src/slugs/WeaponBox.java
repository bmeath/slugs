package slugs;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.BodyType;
import processing.core.PConstants;

public class WeaponBox extends Entity
{
	float w;
	float h;
	PolygonShape shape;
	int weapon; 
	
	public WeaponBox(Slugs p5, float x, float y)
	{
		super(p5, x, y, BodyType.DYNAMIC, true);
		
		w = 20;
		h = 20;
		
		// define the shape
		shape = new PolygonShape();
		/* divide by two.
		 * jbox2d expects width/height values as dist from shape's centre to respective edge
		 */
		
		shape.setAsBox(p5.world.scalarPixelsToWorld(w/2), p5.world.scalarPixelsToWorld(h/2));
		
		fd.shape = shape;
		fd.density = 1;
		fd.friction = 0.3f;
		fd.restitution = 0.5f;
		
		// affix shape to body
		body.createFixture(fd);
	}
	
	protected void drawEntity()
	{
		p5.stroke(0, 0, 0);
		p5.fill(247, 238, 158);
		p5.rectMode(PConstants.CENTER);
		p5.rect(0, 0, w, h);
	}
}
