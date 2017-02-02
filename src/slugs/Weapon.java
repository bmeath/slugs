package slugs;

import org.jbox2d.common.Vec2;

public abstract class Weapon implements InventoryItem
{
	float damageRadius;
	int maxDamage;
	Vec2 loc;
	Player owner;
	
	public Weapon(Slugs p) 
	{
		
	}
	
	public void setOwner(Player p)
	{
		this.owner = p;
	}

	@Override
	public void display() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void use() 
	{
		// TODO Auto-generated method stub

	}

}
