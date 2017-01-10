package slugs;
import java.util.ArrayList;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import processing.core.PConstants;

public class Terrain
{
	Slugs p5;
	ChainShape shape;
	ArrayList<Vec2> screenMap;
	Vec2[] worldMap;
	BodyDef bd;
	Body body;
	
	public Terrain(Slugs p5)
	{
		this.p5 = p5;
		bd = new BodyDef();
		bd.type = BodyType.STATIC;
		body = p5.world.createBody(bd);
		
		// generate pixel coords for the terrain
		screenMap = new ArrayList<Vec2>();
		float seed = 0;
		for(float x = 0; x < p5.width; x += 10)
		{
			screenMap.add(new Vec2(x, p5.noise(seed) * p5.height));
			seed += 0.05;
		}
		
		// convert pixel coords to JBox2D world coords
		worldMap = new Vec2[screenMap.size()];
		for(int i = 0; i < worldMap.length; i ++)
		{
			worldMap[i] = p5.world.coordPixelsToWorld(screenMap.get(i));
		}
		shape = new ChainShape();
		shape.createChain(worldMap, worldMap.length);
		
		// affix shape to body using fixture with default properties
		body.createFixture(shape, 1.0f);
	}

	public void display()
	{
		p5.stroke(0);
		p5.fill(0);
		p5.beginShape();
		for(Vec2 coord: screenMap)
		{
			p5.point(coord.x, coord.y);
			p5.vertex(coord.x, coord.y);
		}
		p5.vertex(p5.width, p5.height);
		p5.vertex(0, p5.height);
		p5.endShape(PConstants.CLOSE);
	}

}
