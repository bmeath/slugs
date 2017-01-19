package slugs;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import shiffman.box2d.Box2DProcessing;

public class Projectile extends Entity
{
	int damage; // maximum damage a player can take from this projectile
	float radius; // how widespread is the explosion?
	float force; // how strong is the explosion?
	Vec2 spawnPoint; // where it emits from
	boolean affectsTerrain; // can it destroy the terrain?
	
	
	public Projectile(Slugs p5, Box2DProcessing world, Vec2 spawnPoint, BodyType type, boolean fixedRotation, float density, float friction,
			float restitution)
	{
		super(p5, world, spawnPoint, type, fixedRotation, density, friction, restitution);
		// TODO Auto-generated constructor stub
	}

	protected void render()
	{
		// TODO Auto-generated method stub

	}

}
