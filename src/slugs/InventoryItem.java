package slugs;

import processing.core.PImage;

public abstract class InventoryItem implements Cloneable
{
	
	Player owner;
	boolean used;
	public PImage img;
	
	public abstract void use();
	protected abstract void update();
	public abstract void display();
	
	/* define what the item does (if anything)
	 * when the up/down key is pressed
	 */
	public abstract void pressUp();
	public abstract void pressDown();
	
	public void setOwner(Player p)
	{
		this.owner = p;
	}
	
	public Object clone()
	{
		try 
		{
			return super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			e.printStackTrace();
		}
		return this;
	}
}
