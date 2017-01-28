package slugs;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import shiffman.box2d.Box2DProcessing;

public abstract class ItemBox extends Entity
{
	float w;
	float h;
	PolygonShape shape;
	int itemID; 
	
	public ItemBox(Slugs p, Box2DProcessing world, Vec2 spawnPoint, float w, float h, int itemID) 
	{
		super(p, world, spawnPoint, BodyType.DYNAMIC, true, 1, 0.3f, 0.5f, 1);
		
		this.itemID = itemID;
		this.w = w;
		this.h = h;
		
		// define the shape
		shape = new PolygonShape();
		
		/* divide by two.
		 * jbox2d expects width/height values as dist from shape's centre to respective edge
		 */
		shape.setAsBox(world.scalarPixelsToWorld(w/2), world.scalarPixelsToWorld(h/2));
		
		fd.shape = shape;
		
		// affix shape to body
		bodyList[0].createFixture(fd);
	}

}
