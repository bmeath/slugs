package slugs;

import org.jbox2d.common.Vec2;

import processing.core.PApplet;
import processing.core.PConstants;
import shiffman.box2d.Box2DProcessing;

public class BombWeapon extends Weapon
{
	Projectile[] projectiles;
	Slugs p;
	Box2DProcessing world;
	float restitution;
	float maxVelocity;
	int clusterCount;
	float aimAngle;
	Vec2 projectileForce;
	int projectileCount;
	float power;

	public BombWeapon(Slugs p, Box2DProcessing world, int projectileCount, int maxDamage, float restitution, int clusterCount, 
			int clusterDamage, float clusterVelocity, float clusterRestitution, boolean explodeOnImpact, int timeout)
	{
		super(p, maxDamage);
		this.p = p;
		this.world = world;
		this.clusterCount = clusterCount;
		this.projectileCount = projectileCount;
		projectiles = new Projectile[projectileCount];
		aimAngle = PConstants.PI;
		projectileForce = new Vec2();
		power = 0;
	}
	
	public BombWeapon(Slugs p, Box2DProcessing world, int projectileCount, int maxDamage, float restitution, boolean explodeOnImpact, int timeout)
	{
		this(p, world, projectileCount, maxDamage, restitution, 0, 0, 0, 0, explodeOnImpact, timeout);
	}
	
	public void display()
	{
		update();
		for(Projectile p: projectiles)
		{
			if(p != null)
			{
				p.display();
			}
		}
		Vec2 lineStart = owner.getPixelLocation();
		Vec2 lineEnd = new Vec2(lineStart.x + 40 * PApplet.cos(owner.dir ? PConstants.PI - aimAngle : aimAngle), lineStart.y - 40 * PApplet.sin(owner.dir ? PConstants.PI - aimAngle : aimAngle));
		p.stroke(255, 0, 0);
		p.strokeWeight(1);
		p.line(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y);
	}
	
	protected void update()
	{
		if (p.checkKey(PConstants.UP))
		{
			// prevent aiming behind view of player
			if(aimAngle > PConstants.PI / 2)
			{
				aimAngle -= 0.025;
			}
		}
		
		if (p.checkKey(PConstants.DOWN))
		{
			// prevent aiming behind view of player
			if(aimAngle < (3 * PConstants.PI) / 2)
			{
				aimAngle += 0.025;
			}
		}
		
		// flip angle horizontally if player is facing to the right
		projectileForce.x = PApplet.cos(owner.dir ? PConstants.PI - aimAngle : aimAngle);
		projectileForce.y = PApplet.sin(owner.dir ? PConstants.PI - aimAngle : aimAngle);
		
		if((power > 0 && p.keys[' '] == false) || power == 5000)
		{
			projectileForce.mulLocal(power);
			Vec2 loc = owner.getPixelLocation();
			loc.x += 10 * ((owner.dir) ? 1 : -1);
			projectiles[--projectileCount] = new Projectile(p, world, loc, maxDamage, restitution, clusterCount, projectileForce);
			power = 0;
		}
	}
	
	public void use()
	{
		if(projectileCount > 0)
		{
			power += 40;
		}
	}
}
