package slugs;

import shiffman.box2d.Box2DProcessing;

public class RangedWeapon extends Weapon
{
	Projectile[] projectiles;
	Slugs p;
	Box2DProcessing world;
	float restitution;
	float maxVelocity;
	int clusterCount;
	Player owner;
	float initialVelocity;

	public RangedWeapon(Slugs p, Box2DProcessing world, int projectileCount, 
			int clusterCount, int maxDamage, float restitution, ExplosionTrigger trigger)
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
	
	public void use()
	{
		for(int i = 0; i < projectiles.length; i++)
		{
			projectiles[i] = new Projectile(p, world, owner.getPixelLocation(), maxDamage,  restitution, initialVelocity, clusterCount);
		}
	}
}
