package slugs;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import shiffman.box2d.Box2DProcessing;

public class HealthCrate extends Entity
{

	public int health;
	PolygonShape shape;
	public boolean used;

	public HealthCrate(Slugs p, Box2DProcessing world, Vec2 spawnPoint) 
	{
		super(p, world, spawnPoint, BodyType.DYNAMIC, true, 1, 0.3f, 0.5f);
		this.health = 25;
		colour = p.color(255, 255, 255);
		// define the shape
		shape = new PolygonShape();
		
		shape.setAsBox(world.scalarPixelsToWorld(10), world.scalarPixelsToWorld(10));
		
		fd.shape = shape;
		
		// affix shape to body
		bodyList.get(0).createFixture(fd);
		bodyList.get(0).setUserData(this);
		used = false;
	}
	
	protected void update()
	{
		Vec2 centre = getPixelLocation();
		p.stroke(255, 0, 0);
		p.strokeWeight(3);
		p.noFill();
		p.line(centre.x - 5, centre.y, centre.x + 5, centre.y);
		p.line(centre.x, centre.y - 5, centre.x, centre.y + 5);
	}
}
