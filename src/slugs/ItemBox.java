package slugs;

import java.util.Map;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import shiffman.box2d.Box2DProcessing;

public class ItemBox extends Entity
{
	PolygonShape shape;
	String itemName;
	public boolean used;
	
	public ItemBox(Slugs p, Box2DProcessing world, Vec2 spawnPoint, String itemName, Map<String, InventoryItem> items) 
	{
		super(p, world, spawnPoint, BodyType.DYNAMIC, true, 1, 0.3f, 0.5f);
		if(items.get(itemName) instanceof Weapon)
		{
			// weapon crates have colour of light wood
			colour = p.color(255, 244, 168); 
		}
		else
		{
			// tool boxes have blue colour
			colour = p.color(95, 152, 221); 
		}
		
		this.used = false; 
		this.itemName = itemName;
		
		// define the shape
		shape = new PolygonShape();
		
		/* divide by two.
		 * jbox2d expects width/height values as dist from shape's centre to respective edge
		 */
		shape.setAsBox(world.scalarPixelsToWorld(10), world.scalarPixelsToWorld(10));
		
		fd.shape = shape;
		
		// affix shape to body
		bodyList.get(0).createFixture(fd);
		bodyList.get(0).setUserData(this);
	}
	
	protected void update()
	{
		// draw black cross on box
		Vec2 centre = getPixelLocation();
		p.stroke(0);
		p.strokeWeight(1);
		p.noFill();
		p.line(centre.x - 9, centre.y - 9, centre.x + 9, centre.y + 9);
		p.line(centre.x - 9, centre.y + 9, centre.x + 9, centre.y - 9);
	}

}
