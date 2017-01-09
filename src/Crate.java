import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import shiffman.box2d.Box2DProcessing;

public class Crate extends Entity
{
	float w;
	float h;
	
	public Crate(SlugsGame p5, float x, float y, Box2DProcessing world)
	{
		super(p5, x, y, world, BodyType.DYNAMIC, 1.0f, 0.3f, 0.5f);
		
		w = 20;
		h = 20;
		
		// define the shape
		shape = new PolygonShape();
		float box2Dw = world.scalarPixelsToWorld(w/2);
		float box2Dh = world.scalarPixelsToWorld(h/2);
		shape.setAsBox(box2Dw, box2Dh);
		fd.shape = shape;
		
		// affix shape to body
		body.createFixture(fd);
		
		
	}
	
	public void display()
	{
		Vec2 pos = world.getBodyPixelCoord(body);
		p5.pushMatrix();
		p5.translate(pos.x, pos.y);
		p5.rotate(-body.getAngle());
		p5.stroke(0, 0, 0);
		p5.fill(247, 238, 158);
		p5.rect(0, 0, w, h);
		p5.popMatrix();
	}
}
