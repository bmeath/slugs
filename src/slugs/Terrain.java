package slugs;

import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import processing.core.PApplet;
import processing.core.PConstants;
import shiffman.box2d.Box2DProcessing;

public class Terrain
{
	Slugs p;
	int maxVertices = 6000;
	BodyDef bd;
	FixtureDef fd;
	Area screenMap;
	Polygon screenMapPoly;
	ArrayList<Body> bodies;
	Box2DProcessing world;
	Area crater;
	private boolean old;
	
	/* steepness is a factor to control how severe the hills are
	 * where 1 is very rough
	 * and 0 is completely flat
	 */
	public Terrain(Slugs p, Box2DProcessing world, float steepness)
	{
		this.world = world;
		this.p = p;
		old = false;
		bd = new BodyDef();
		bd.type = BodyType.STATIC;
		Body b = world.createBody(bd);
		/* arraylist to hold all the bodys that combine to form the terrain,
		 * because the terrain sometimes gets split up from destruction
		 */
		bodies = new ArrayList<Body>();
		
		// generate terrain in pixel coordinates
		screenMapPoly = new Polygon();
		float seed = 0;
		for(int x = 0; x <= p.width; x ++)
		{
			screenMapPoly.addPoint(x, (int) (p.height/3f + p.noise(seed) * p.height * 0.66f));
			seed += 0.01 * steepness;
		}
		screenMapPoly.addPoint(p.width, p.height);
		screenMapPoly.addPoint(0, p.height);
		
		// convert pixel coords to box2d coords
		Vec2[] worldMap = new Vec2[screenMapPoly.npoints];
		for(int i = 0; i < worldMap.length; i ++)
		{
			worldMap[i] = world.coordPixelsToWorld(screenMapPoly.xpoints[i], screenMapPoly.ypoints[i]);
		}
		ChainShape shape = new ChainShape();
		shape.createChain(worldMap, worldMap.length);
		
		// define some physical properties the terrain will have
		fd = new FixtureDef();
		fd.restitution = 0;
		fd.friction = 10;
		fd.shape = shape;
		
		
		b.createFixture(fd);
		b.setUserData(this);
		bodies.add(b);
		
		// store and modify terrain in Area object
		screenMap = new Area(screenMapPoly);
		
		/* create a crater shape 
		 * which will be used when the terrain recieves damage
		 */
		Path2D.Float craterShape = new Path2D.Float();
		int numPoints = 32; // number of vertices in the polygon
		float angle = (2 * PConstants.PI)/numPoints;
		// begin the path using moveTo
		craterShape.moveTo(PApplet.sin(numPoints * angle), -PApplet.cos(numPoints * angle));
		while(numPoints-- >= 0)
		{
			craterShape.lineTo(PApplet.sin(numPoints * angle), -PApplet.cos(numPoints * angle));
		}
		crater = new Area(craterShape);
		
	}
	
	// create terrain with the default steepness factor 0.5
	public Terrain(Slugs p, Box2DProcessing world)
	{
		this(p, world, 0.5f);
	}

	public void display()
	{
		if(old)
		{
			update();
		}
		p.stroke(100, 170, 50);
		p.strokeWeight(5);
		p.fill(170, 115, 50);
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
	
	// make a crater of given diameter at a given location in the terrain
	public void damage(Vec2 loc, float diameter)
	{
		AffineTransform t = new AffineTransform(diameter, 0, 0, diameter, loc.x, loc.y);
		screenMap.subtract(crater.createTransformedArea(t));
		old = true;
	}
	
	protected void update()
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
					b.setUserData(this);
					bodies.add(b);
					break;
				default:
					points[index] = world.coordPixelsToWorld(point[0],point[1]);
					// skip vertices that are too close together
					if (MathUtils.distanceSquared(points[index - 1], points[index]) > 0.000025)
					{
						index++;
					}
					break;
			}
		}
	}
}
