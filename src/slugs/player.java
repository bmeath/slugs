package slugs;
import java.util.ArrayList;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import processing.core.PConstants;
import processing.data.XML;
import shiffman.box2d.Box2DProcessing;

public class Player extends Entity
{
	float w;
	float h;
	String name;
	
	public Player(Slugs p5, Box2DProcessing world, Vec2 spawnPoint)
	{
		super(p5, spawnPoint, BodyType.KINEMATIC, true, 1, 0.3f, 0.5f);
		
		w = 40;
		h = 30;
		
		XML shapeFile = p5.loadXML("player.xml");
		XML[] shape = shapeFile.getChildren("shape");
		
		PolygonShape[] shapes = new PolygonShape[shape.length];
		Vec2[] vertices;
		
		for(int i = 0; i < shape.length; i++)
		{
			XML[] point = shape[i].getChildren("point");
			
			vertices = new Vec2[point.length];
			for(int j = 0; j < point.length; i++)
			{
				vertices[j] = world.coordPixelsToWorld(point[i].getChild("x").getFloatContent(), point[i].getChild("y").getFloatContent());
			}
		}	
	}
	
	protected void render()
	{
		p5.fill(0, 200, 150);
		
	}

}
