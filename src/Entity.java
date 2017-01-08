import java.awt.Polygon;
import processing.core.PVector;
import java.awt.Rectangle;
import org.jbox2d.dynamics.BodyDef;

public abstract class Entity
{
	PVector centre;
	BodyDef bd = new BodyDef();
	Polygon shape;
	SlugsGame p5;
	
	
	public Entity(SlugsGame p5, float x, float y)
	{
		this.p5 = p5;
		this.centre = new PVector(x, y);
	}
	
	public boolean collisionCheck(Rectangle r)
	{
		// check if the shapes intersect
		return this.shape.getBounds().intersects(r);
	}
	
	public abstract void display();
}
