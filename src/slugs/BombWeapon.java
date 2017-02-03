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

	public BombWeapon(Slugs p, Box2DProcessing world, int projectileCount, int maxDamage, float restitution,
			int clusterCount, int clusterDamage, float clusterVelocity, float clusterRestitution, boolean explodeOnImpact, int timeout)
	{
		super(p, maxDamage);
		this.p = p;
		this.world = world;
		this.clusterCount = clusterCount;
		this.projectileCount = projectileCount;
		projectiles = new Projectile[projectileCount];
		aimAngle = 90;
		projectileForce = new Vec2();
	}
	
	public BombWeapon(Slugs p, Box2DProcessing world, int projectileCount, int maxDamage, float restitution, boolean explodeOnImpact, int timeout)
	{
		this(p, world, projectileCount, maxDamage, restitution, 0, 0, 0, 0, explodeOnImpact, timeout);
	}
	
	public void update()
	{
		if (p.checkKey(PConstants.UP))
		{
			aimAngle += 0.25;
		}
		
		if (p.checkKey(PConstants.DOWN))
		{
			aimAngle -= 0.25;
		}
		
		projectileForce.x = PApplet.cos(PApplet.radians(aimAngle));
		projectileForce.y = PApplet.sin(PApplet.radians(aimAngle));
		projectileForce.mulLocal(1500);
	}
	
	public void use()
	{
		System.out.println("use");
		if(projectileCount > 0)
		{
			Vec2 loc = owner.getPixelLocation();
			loc.x += 8 * ((owner.dir) ? 1 : -1);
			projectiles[projectileCount - 1] = new Projectile(p, world, loc, maxDamage, restitution, clusterCount, projectileForce);
			projectileCount--;
		}
	}
}
