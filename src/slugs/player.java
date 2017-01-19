package slugs;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;

import processing.core.PConstants;
import processing.data.XML;
import shiffman.box2d.Box2DProcessing;

public class Player extends Entity
{
	String name;
	
	public Player(Slugs p5, Box2DProcessing world, Vec2 spawnPoint)
	{
		super(p5, world, spawnPoint, BodyType.KINEMATIC, true, 1, 0.3f, 0.5f);
		
		//XML xmlShapeFile = p5.loadXML("player.xml");
		XML[] xmlShape = p5.loadXML("player.xml").getChildren("shape");
		XML[] xmlPoint;
		
		PolygonShape[] shapes = new PolygonShape[xmlShape.length];
		Vec2[] vertices;
		//Vec2 offset = new Vec2();
		for(int i = 0; i < xmlShape.length; i++)
		{
			xmlPoint = xmlShape[i].getChildren("point");
			vertices = new Vec2[xmlPoint.length];
			for(int j = 0; j < xmlPoint.length; j++)
			{
				vertices[j] = world.coordPixelsToWorld(xmlPoint[j].getChild("x").getFloatContent(), xmlPoint[j].getChild("y").getFloatContent());
			}
			shapes[i] = new PolygonShape();
			
			shapes[i].set(vertices, vertices.length);
			body.createFixture(shapes[i], 1.0f);
			
		}
	}
	
	protected void render()
	{
		p5.translate(p5.width/2 + pos.x, p5.height/2 + pos.y);
		p5.fill(0, 200, 150);
		for(Fixture f = body.getFixtureList(); f != null; f = f.getNext())
		{
			p5.beginShape();
			PolygonShape shape = (PolygonShape) f.getShape();
			for(int i = 0; i < shape.getVertexCount(); i++)
			{
				Vec2 v = world.vectorWorldToPixels(shape.getVertex(i));
				p5.vertex(v.x, v.y);
			}
			p5.endShape(PConstants.CLOSE);
		}
	}

}
