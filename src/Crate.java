import java.awt.Rectangle;

import org.jbox2d.collision.shapes.PolygonShape;

import shiffman.box2d.Box2DProcessing;

public class Crate extends Entity
{
	float w = 10;
	float h = 10;
	
	public Crate(SlugsGame p5, float x, float y, Box2DProcessing world)
	{
		super(p5, x, y, world);
	}
	
	public boolean collisionCheck(Rectangle r)
	{
		// check if the shapes intersect
		return this.shape.getBounds().intersects(r);
	}
	
	public void display()
	{
		p5.stroke(0, 0, 0);
		p5.fill(247, 238, 158);
		p5.rect(centre.x, centre.y, 25, 25);
	}
	
	void createShape()
	{
		PolygonShape shape = new PolygonShape();
		float box2Dw = world.scalarPixelsToWorld(w);
		float box2dh = world.scalarPixelsToWorld(h);
	}
}
