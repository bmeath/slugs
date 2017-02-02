package slugs;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import shiffman.box2d.Box2DProcessing;

public class Projectile extends Entity
{
	int damage; // maximum damage a player can take from this projectile
	float damageRadius; // how widespread is the explosion?
	Vec2 spawnPoint; // where it emits from
	boolean affectsTerrain; // can it destroy the terrain?
	Projectile[] clusters;
	Vec2 angle;
	
	public Projectile(Slugs p, Box2DProcessing world, Vec2 spawnPoint, int maxDamage, float restitution, float initialVelocity, int clusterCount, Vec2 force)
	{
		super(p, world, spawnPoint, BodyType.DYNAMIC, false, 1, 1, restitution, 1);
		clusters = new Projectile[clusterCount];
		
		PolygonShape shape = new PolygonShape();
		// define the shape
		shape = new PolygonShape();
		
		// create main body
		float w = world.scalarPixelsToWorld(4);
		float h = world.scalarPixelsToWorld(10);
		shape.setAsBox(w, h);
		
		fd.shape = shape;
		bodyList[0].createFixture(fd);
		bodyList[0].setUserData(this);
		
		bodyList[0].applyForceToCenter(force);
	}
	
	
	
	protected void update()
	{
		
	}

}
