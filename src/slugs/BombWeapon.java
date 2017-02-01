package slugs;

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

	public BombWeapon(Slugs p, Box2DProcessing world, int projectileCount, 
			int clusterCount, float clusterVelocity, int maxDamage, float restitution, float clusterRestitution, ExplosionTrigger trigger, boolean explodeOnImpact, int timeout)
	{
		super(p);
		this.p = p;
		this.world = world;
		this.clusterCount = clusterCount;
		projectiles = new Projectile[projectileCount];
	}
	
	public void display()
	{
		
	}
	
	public void update()
	{
		
	}
	
	public void use(Player user)
	{
		for(int i = 0; i < projectiles.length; i++)
		{
			projectiles[i] = new Projectile(p, world, user.getPixelLocation(), maxDamage,  restitution, initialVelocity, clusterCount);
		}
	}
}
