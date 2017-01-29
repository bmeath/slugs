package slugs;

import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
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
	//ArrayList<Vec2> screenMap;
	Vec2[] worldMap;
	BodyDef bd;
	Body body;
	FixtureDef fd;
	Area screenMap;
	Polygon screenMapPoly;
	
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
		
		screenMapPoly = new Polygon();
		float seed = 0;
		for(int x = 0; x <= p.width; x ++)
		{
			screenMapPoly.addPoint(x, (int) (p.height/3f + p.noise(seed) * p.height * 0.66f));
			seed += 0.01 * steepness;
		}
		screenMapPoly.addPoint(p.width, p.height);
		screenMapPoly.addPoint(0, p.height);
		
		worldMap = new Vec2[screenMapPoly.npoints];
		for(int i = 0; i < worldMap.length; i ++)
		{
			worldMap[i] = p.world.coordPixelsToWorld(screenMapPoly.xpoints[i], screenMapPoly.ypoints[i]);
		}
		
		screenMap = new Area(screenMapPoly);
		
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
		update();
		p.stroke(0);
		for (PathIterator i = screenMap.getPathIterator(null); !i.isDone(); i.next())
		{
			float[] point = new float[6];
			switch (i.currentSegment(point))
			{
				case PathIterator.SEG_MOVETO:
					p.beginShape();
					p.vertex(point[0], point[1]);
					break;
				case PathIterator.SEG_CLOSE:
					p.endShape(PConstants.CLOSE);
					break;
				default:
					p.vertex(point[0],point[1]);
					break;
			}
		}
		p.vertex(p.width, p.height);
		p.vertex(0, p.height);
	}
	
	// picks a random position along the terrain at h pixels above surface
	public Vec2 randomSpawn(float h)
	{
		Vec2 spawn = new Vec2();
		int i = (int) p.random(screenMapPoly.npoints);
		spawn.x = screenMapPoly.xpoints[i];
		spawn.y = screenMapPoly.ypoints[i] - h;
		return spawn;
	}
	
	public Vec2 randomSpawn()
	{
		// 40 pixels default height above ground
		return randomSpawn(40);
	}
	
	void update()
	{
		if (p.mousePressed)
		{
			screenMap.subtract(new Area(new Ellipse2D.Float(p.mouseX, p.mouseY, 25, 25)));
		}
	}
}
