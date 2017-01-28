package slugs;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import processing.core.PConstants;

public class Terrain
{
	Slugs p;
	ChainShape shape;
	ArrayList<Vec2> screenMap;
	Vec2[] worldMap;
	BodyDef bd;
	Body body;
	FixtureDef fd;
	
	/* steepness is a factor to control how severe the hills are
	 * where 1 is very rough
	 * and 0 is completely flat
	 */
	public Terrain(Slugs p, float steepness)
	{
		this.p = p;
		bd = new BodyDef();
		bd.type = BodyType.STATIC;
		body = p.world.createBody(bd);
		
		// generate pixel coords for the terrain
		screenMap = new ArrayList<Vec2>();
		float seed = 0;
		for(float x = 0; x <= p.width; x ++)
		{
			screenMap.add(new Vec2(x, p.height/3 + p.noise(seed) * p.height * 0.66f));
			seed += 0.01 * steepness;
		}
		
		// convert pixel coords to JBox2D world coords
		worldMap = new Vec2[screenMap.size()];
		for(int i = 0; i < worldMap.length; i ++)
		{
			worldMap[i] = p.world.coordPixelsToWorld(screenMap.get(i));
		}
		shape = new ChainShape();
		shape.createChain(worldMap, worldMap.length);
		
		fd = new FixtureDef();
		fd.restitution = 0;
		fd.friction = 10;
		fd.shape = shape;
		body.createFixture(fd);
		
		body.setUserData(this);
	}
	
	// create terrain with the default steepness factor 0.5
	public Terrain(Slugs p)
	{
		this(p, 0.5f);
	}

	public void display()
	{
		p.stroke(0);
		p.fill(0);
		p.beginShape();
		for(Vec2 coord: screenMap)
		{
			p.point(coord.x, coord.y);
			p.vertex(coord.x, coord.y);
		}
		p.vertex(p.width, p.height);
		p.vertex(0, p.height);
		p.endShape(PConstants.CLOSE);
	}

}
