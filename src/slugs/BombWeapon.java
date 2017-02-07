package slugs;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import processing.core.PApplet;
import processing.core.PConstants;
import shiffman.box2d.Box2DProcessing;

public class BombWeapon extends Weapon
{
	ArrayList<Projectile> projectiles;
	Slugs p;
	Box2DProcessing world;
	float restitution;
	float maxVelocity;
	int clusterCount;
	float aimAngle;
	Vec2 projectileForce;
	int projectileCount;
	float power;
	boolean explodeOnImpact;
	int timeout;
	Player[] players;
	Terrain map;

	public BombWeapon(Slugs p, Box2DProcessing world, Player[] players, Terrain map, int projectileCount, 
			int maxDamage, float restitution, int clusterCount, int clusterDamage, float clusterVelocity, 
			float clusterRestitution, boolean explodeOnImpact, int timeout)
	{
		super(p, maxDamage);
		this.p = p;
		this.world = world;
		this.players = players;
		this.map = map;
		this.clusterCount = clusterCount;
		this.projectileCount = projectileCount;
		projectiles = new ArrayList<Projectile>();
		aimAngle = PConstants.PI;
		projectileForce = new Vec2();
		power = 0;
		this.explodeOnImpact = explodeOnImpact;
		this.timeout = timeout;
	}
	
	public BombWeapon(Slugs p, Box2DProcessing world, Player[] players, Terrain map, int projectileCount, int maxDamage, float restitution, boolean explodeOnImpact, int timeout)
	{
		this(p, world, players, map, projectileCount, maxDamage, restitution, 0, 0, 0, 0, explodeOnImpact, timeout);
	}
	
	public void display()
	{
		update();
		for(int i = 0; i < projectiles.size(); i++)
		{
			projectiles.get(i).display();
		}
		Vec2 lineStart = owner.getPixelLocation();
		Vec2 lineEnd = new Vec2(lineStart.x + 40 * PApplet.cos(owner.dir ? PConstants.PI - aimAngle : aimAngle), lineStart.y - 40 * PApplet.sin(owner.dir ? PConstants.PI - aimAngle : aimAngle));
		p.stroke(255, 0, 0);
		p.strokeWeight(1);
		p.line(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y);
	}
	
	protected void update()
	{
		for(int i = 0; i < projectiles.size(); i++)
		{
			if(map.contains(projectiles.get(i).getPixelLocation()))
			{
				projectiles.get(i).explode();
			}
			if (projectiles.get(i).hit)
			{
				destroyProjectile(projectiles.get(i));
			}
		}
		
		// flip angle horizontally if player is facing to the right
		projectileForce.x = PApplet.cos(owner.dir ? PConstants.PI - aimAngle : aimAngle);
		projectileForce.y = PApplet.sin(owner.dir ? PConstants.PI - aimAngle : aimAngle);
		
		if((power > 0 && p.keys[' '] == false) || power == 12000)
		{
			projectileForce.mulLocal(power);
			Vec2 loc = owner.getPixelLocation();
			loc.x += 15 * PApplet.cos(owner.dir ? PConstants.PI - aimAngle : aimAngle);
			loc.y -= 20 * PApplet.sin(owner.dir ? PConstants.PI - aimAngle : aimAngle);
			
			// check if projectile was shot inside the ground or outside the screen
			if (map.contains(loc) || loc.x < 0 || loc.y < 0 ||loc.x > p.width || loc.y > p.height)
			{
				if (!explodeOnImpact)
				{
					loc = owner.getPixelLocation().clone();
				}
			}
			
			projectiles.add(new Projectile(p, world, players, map, this, loc, maxDamage, restitution, explodeOnImpact, timeout, clusterCount, projectileForce));
			projectileCount--;
			power = 0;
		}
	}
	
	public void use()
	{
		if(projectileCount > 0)
		{
			power += 100;
		}
	}
	
	public void destroyProjectile(Projectile proj)
	{
		for(Body b: proj.bodyList)
		{
			world.destroyBody(b);
		}
		proj.bodyList.clear();
		projectiles.remove(proj);
		if (projectiles.isEmpty())
		{
			used = true;
		}
	}
	
	public void pressUp()
	{
		// prevent aiming behind view of player
		if(aimAngle > PConstants.PI / 2)
		{
			aimAngle -= 0.025;
		}
	}
	
	public void pressDown()
	{
		// prevent aiming behind view of player
		if(aimAngle < (3 * PConstants.PI) / 2)
		{
			aimAngle += 0.025;
		}
	}
}
