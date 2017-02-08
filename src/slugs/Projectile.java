package slugs;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import shiffman.box2d.Box2DProcessing;

public class Projectile extends Entity
{
	int damage; // maximum damage a player can take from this projectile
	Vec2 spawnPoint; // where it emits from
	boolean affectsTerrain; // can it destroy the terrain?
	Projectile[] clusters;
	Vec2 angle;
	BombWeapon source;
	boolean hit; // wether projectile has hit something or exploded
	
	int timeStart; // time this was instantiated at
	int timeout; // time left before explosion
	
	ArrayList<Player> players; // used to deal damage as needed
	Terrain map;
	PImage img;
	int xScale; // used to flip image horizontally or not
	
	
	public Projectile(Slugs p, Box2DProcessing world, ArrayList<Player> players, Terrain map, BombWeapon source, Vec2 spawnPoint, 
			int maxDamage, float restitution, boolean explodeOnImpact, int timeout, int clusterCount, Vec2 force, boolean fixedRotation, PImage img, int xScale)
	{
		super(p, world, spawnPoint, BodyType.DYNAMIC, fixedRotation, 10, 1, restitution);
		
		this.players = players;
		this.map = map;
		
		this.colour = p.color(0, 0, 0);
		
		this.damage = maxDamage;
		
		clusters = new Projectile[clusterCount];
		
		CircleShape shape = new CircleShape();
		// define the shape
		shape = new CircleShape();
		
		float radius = world.scalarPixelsToWorld(4);
		shape.setRadius(radius);
		
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
		this.img = img;
		this.xScale = xScale;
	}
	
	public float getDamageRadius()
	{
		return damage * 0.75f;
	}
	
	public void explode()
	{
		this.hit = true;
		Vec2 loc = getPixelLocation();
		float radius = getDamageRadius();
		map.damage(loc, radius);
		
		for(Player player: players)
		{
			Vec2 playerLoc = player.getPixelLocation();
			float dist = PApplet.dist(loc.x, loc.y, playerLoc.x, playerLoc.y);
			if(dist < radius)
			{
				// damage dealt is proportional to proximity of player to explosion
				player.hurt((int) PApplet.map(dist, 10, radius, damage, 5));
				
				// apply force to player
				Vec2 force= new Vec2(playerLoc.x - loc.x, playerLoc.y - loc.y);
				force.normalize();
				player.applyForce(force.mul(10000), 1);
			}
		}
	}
	
	protected void update()
	{
		if(timeout > 0)
		{
			Vec2 loc = getPixelLocation();
			int t = p.millis() - timeStart;
			p.textSize(12);
			p.textAlign(PConstants.CENTER, PConstants.CENTER);
			p.text(((timeout - t) / 1000) + 1, loc.x, loc.y - 20);
			if(t >= timeout)
			{
				explode();
			}
		}
		
		// display picture of the projectile
		Vec2 loc = getPixelLocation();
		p.pushMatrix();
		p.translate(loc.x, loc.y);
		p.rotate(-bodyList.get(0).getAngle());
		p.imageMode(PConstants.CENTER);
		p.scale(xScale, 1);
		p.image(img, 0, 0, 24, 24);
		p.popMatrix();
	}

}
