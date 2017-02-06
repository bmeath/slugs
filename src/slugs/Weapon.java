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
	
	public abstract void pressUp();
	public abstract void pressDown();
	
	public abstract void display();
	protected abstract void update();
	public abstract void use();

}
