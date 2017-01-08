import java.awt.Polygon;
import processing.core.PVector;
import java.awt.Rectangle;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import shiffman.box2d.*;

public abstract class Entity
{
	SlugsGame p5;
	PVector centre;
	BodyDef bd;
	Polygon shape;
	Body body;
	
	public Entity(SlugsGame p5, float x, float y, Box2DProcessing world)
	{
		this.p5 = p5;
		
		this.centre = new PVector(x, y);
		
		// define the physical properties
		bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.fixedRotation = true;
		Vec2 centre = world.coordPixelsToWorld(x, y);
		bd.position.set(centre);
		
		// create body
		body = world.createBody(bd);
		
		// create shape
		createShape();
	}
	
	public boolean collisionCheck(Rectangle r)
	{
		// check if the shapes intersect
		return this.shape.getBounds().intersects(r);
	}
	
	public abstract void display();
	abstract void createShape();
}
