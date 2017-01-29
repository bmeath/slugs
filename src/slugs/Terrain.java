package slugs;

import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import processing.core.PConstants;
import shiffman.box2d.Box2DProcessing;

public class Terrain
{
	Slugs p;
	//ArrayList<Vec2> screenMap;
	Vec2[] worldMap;
	int maxVertices = 2048;
	BodyDef bd;
	FixtureDef fd;
	Area screenMap;
	Polygon screenMapPoly;
	ArrayList<Body> bodies;
	Box2DProcessing world;
	
	/* steepness is a factor to control how severe the hills are
	 * where 1 is very rough
	 * and 0 is completely flat
	 */
	public Terrain(Slugs p, Box2DProcessing world, float steepness)
	{
		this.world = world;
		this.p = p;
		bd = new BodyDef();
		bd.type = BodyType.STATIC;
		bodies = new ArrayList<Body>();
		Body b = world.createBody(bd);
		
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
			worldMap[i] = world.coordPixelsToWorld(screenMapPoly.xpoints[i], screenMapPoly.ypoints[i]);
		}
		
		screenMap = new Area(screenMapPoly);
		
		ChainShape shape = new ChainShape();
		shape.createChain(worldMap, worldMap.length);
		
		fd = new FixtureDef();
		fd.restitution = 0;
		fd.friction = 10;
		fd.shape = shape;
		b.createFixture(fd);
		bodies.add(b);
		
	}
	
	// create terrain with the default steepness factor 0.5
	public Terrain(Slugs p, Box2DProcessing world)
	{
		this(p, world, 0.5f);
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
	
	protected void update()
	{
		if (p.mousePressed)
		{
			p.lastClick = p.millis();
			//screenMap.subtract(new Area(new Ellipse2D.Float(p.mouseX, p.mouseY, 25, 25)));
			screenMap.subtract(new Area(new Rectangle2D.Float(p.mouseX, p.mouseY, 25, 25)));
			reCreate();
		}
	}
	
	protected void reCreate()
	{
		for(Body b: bodies)
		{
			world.destroyBody(b);
		}
		bodies.clear();
		
		Vec2[] points = new Vec2[maxVertices];
		ChainShape shape = new ChainShape();
		Body b = world.createBody(bd);
		int index = 0;
		for (PathIterator i = screenMap.getPathIterator(null); !i.isDone(); i.next())
		{
			float[] point = new float[6];
			switch (i.currentSegment(point))
			{
				case PathIterator.SEG_MOVETO:
					b = world.createBody(bd);
					shape = new ChainShape();
					points = new Vec2[maxVertices];
					index = 0;
					points[index] = world.coordPixelsToWorld(point[0],point[1]);
					index++;
					break;
				case PathIterator.SEG_CLOSE:
					shape.createChain(points, index);
					fd.shape = shape;
					b.createFixture(fd);
					bodies.add(b);
					break;
				default:
					points[index] = world.coordPixelsToWorld(point[0],point[1]);
					index++;
					break;
			}
		}
	}
}
