package slugs;

import org.jbox2d.common.Vec2;

public abstract class Weapon extends InventoryItem
{
	float damageRadius;
	int maxDamage;
	Vec2 loc;
	
	
	public Weapon(Slugs p, int maxDamage) 
	{
		this.maxDamage = maxDamage;
	}
	
	public void display() 
	{
		// TODO Auto-generated method stub

	}

	public void use() 
	{
		// TODO Auto-generated method stub

	}

}
