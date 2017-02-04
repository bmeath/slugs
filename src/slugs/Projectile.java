package slugs;

import java.util.HashMap;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import processing.core.PApplet;
import processing.core.PConstants;
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
	int timeStart;
	int timeout;
	HashMap<String, Player> players;
	Terrain map;
	
	public Projectile(Slugs p, Box2DProcessing world, HashMap<String, Player> players, Terrain map, BombWeapon source, Vec2 spawnPoint, int maxDamage, float restitution, boolean explodeOnImpact, int timeout, int clusterCount, Vec2 force)
	{
		super(p, world, spawnPoint, BodyType.DYNAMIC, false, 1, 1, restitution);
		this.players = players;
		this.map = map;
		
		this.colour = p.color(255, 0, 0);
		
		this.damage = maxDamage;
		
		clusters = new Projectile[clusterCount];
		
		PolygonShape shape = new PolygonShape();
		// define the shape
		shape = new PolygonShape();
		
		// create main body
		float w = world.scalarPixelsToWorld(1);
		float h = world.scalarPixelsToWorld(7);
		shape.setAsBox(w, h);
		
		fd.shape = shape;
		bodyList.get(0).createFixture(fd);
		if (explodeOnImpact)
		{
			bodyList.get(0).setUserData(this);
		}
		
		timeStart = p.millis();
		
		bodyList.get(0).applyForceToCenter(force);
		
		this.source = source;
		hit = false;
		this.timeout = timeout;
	}
	
	public float getDamageRadius()
	{
		return damage * 0.75f;
	}
	
	public void explode()
	{
		Vec2 loc = getPixelLocation();
		float radius = getDamageRadius();
		map.damage(loc, radius);
		for(Player player: players.values())
		{
			Vec2 playerLoc = player.getPixelLocation();
			float dist = PApplet.dist(loc.x, loc.y, playerLoc.x, playerLoc.y);
			if(dist < radius)
			{
				// damage dealt is proportional to proximity of player to explosion
				player.hurt((int) PApplet.map(dist, 10, radius, damage, 5));
			}
		}
		hit = true;
	}
	
	protected void update()
	{
		// check if projectile was shot inside the chainshape by aiming into the ground
		if (map.contains(getPixelLocation()))
		{
			explode();
		}
		if(timeout > 0)
		{
			Vec2 loc = getPixelLocation();
			int t = p.millis() - timeStart;
			p.textAlign(PConstants.CENTER, PConstants.CENTER);
			p.text(((timeout - t) / 1000) + 1, loc.x, loc.y - 20);
			if(t >= timeout)
			{
				explode();
			}
		}
	}

}
