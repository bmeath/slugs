import java.util.ArrayList;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import processing.core.PApplet;
import processing.core.PConstants;
import shiffman.box2d.Box2DProcessing;

public class Terrain extends Entity
{
	ChainShape shape;
	ArrayList<Vec2> screenMap;
	Vec2[] worldMap;
	
	public Terrain(SlugsGame p5, Box2DProcessing world)
	{
		super(p5, 0, 0, world, BodyType.STATIC, true, 1, 1, 0);
		
		// generate pixel coords for the terrain
		screenMap = new ArrayList<Vec2>();
		// begin at a random height to the left of the screen
		Vec2 mapGenCoord = new Vec2(-1, p5.random(p5.height, 0));
		while(mapGenCoord.x < p5.width)
		{
			mapGenCoord.x ++;
			mapGenCoord.y = PApplet.map(p5.noise(mapGenCoord.x, mapGenCoord.y), 0, 1, p5.height/2, -(p5.height/2));
			screenMap.add(mapGenCoord);
		}
		
		// convert pixel coords to JBox2D world coords
		worldMap = new Vec2[screenMap.size()];
		for(int i = 0; i < worldMap.length; i ++)
		{
			worldMap[i] = world.coordPixelsToWorld(screenMap.get(i));
		}
		shape = new ChainShape();
		shape.createChain(worldMap, worldMap.length);
		
		fd.shape = shape;
		
		// affix shape to body
		body.createFixture(fd);
	}

	public void display() {
		p5.noStroke();
		p5.fill(0);
		p5.beginShape();
		for(Vec2 coord: screenMap)
		{
			p5.vertex(coord.x, coord.y);
		}
		p5.vertex(p5.width, p5.height);
		p5.vertex(0, p5.height);
		p5.endShape(PConstants.CLOSE);
	}

}
