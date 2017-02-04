package slugs;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import shiffman.box2d.Box2DProcessing;

public class Projectile extends Entity
{
	int damage; // maximum damage a player can take from this projectile
	Vec2 spawnPoint; // where it emits from
	boolean affectsTerrain; // can it destroy the terrain?
	Projectile[] clusters;
	Vec2 angle;
	BombWeapon source;
	boolean hit;
	
	public Projectile(Slugs p, Box2DProcessing world, BombWeapon source, Vec2 spawnPoint, int maxDamage, float restitution, int clusterCount, Vec2 force)
	{
		super(p, world, spawnPoint, BodyType.DYNAMIC, false, 1, 1, restitution);
		
		this.colour = p.color(255, 0, 0);
		
		this.damage = maxDamage;
		
		clusters = new Projectile[clusterCount];
		
		PolygonShape shape = new PolygonShape();
		// define the shape
		shape = new PolygonShape();
		
		// create main body
		float w = world.scalarPixelsToWorld(3);
		float h = world.scalarPixelsToWorld(8);
		shape.setAsBox(w, h);
		
		fd.shape = shape;
		bodyList.get(0).createFixture(fd);
		bodyList.get(0).setUserData(this);
		
		bodyList.get(0).applyForceToCenter(force);
		
		this.source = source;
		hit = false;
	}
	
	public float getDamageRadius()
	{
		return damage * 0.75f;
	}
	
	protected void update()
	{
		
	}

}
