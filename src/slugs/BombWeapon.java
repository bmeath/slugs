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
	float initialVelocity;
	float aimAngle;
	Vec2 projectileForce;
	int projectileCount;

	public BombWeapon(Slugs p, Box2DProcessing world, int projectileCount, int maxDamage, float restitution,
			int clusterCount, int clusterDamage, float clusterVelocity, float clusterRestitution, boolean explodeOnImpact, int timeout)
	{
		super(p);
		this.p = p;
		this.world = world;
		this.clusterCount = clusterCount;
		this.projectileCount = projectileCount;
		projectiles = new Projectile[projectileCount];
	}
	
	public BombWeapon(Slugs p, Box2DProcessing world, int projectileCount, int maxDamage, float restitution, boolean explodeOnImpact, int timeout)
	{
		super(p);
		this.p = p;
		this.world = world;
		this.clusterCount = 0;
		this.projectileCount = projectileCount;
		projectiles = new Projectile[projectileCount];
	}
	
	public void display()
	{
		
	}
	
	public void update()
	{
		if (p.checkKey(PConstants.UP))
		{
			aimAngle++;
		}
		
		if (p.checkKey(PConstants.DOWN))
		{
			aimAngle--;
		}
		
		projectileForce.x = PApplet.cos(aimAngle);
		projectileForce.y = PApplet.sin(aimAngle);
		projectileForce.mulLocal(50);
		
	}
	
	public void use(Player user)
	{
		if(projectileCount > 0)
		{
			Vec2 loc = user.getPixelLocation();
			loc.x += 8 * ((user.dir) ? 1 : -1);
			
			projectiles[projectileCount - 1] = new Projectile(p, world, loc, maxDamage, restitution, initialVelocity, clusterCount, projectileForce);
			projectileCount--;
		}
	}
}
