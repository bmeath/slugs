package slugs;

public abstract class InventoryItem implements Cloneable
{
	
	Player owner;
	
	public abstract void use();
	protected abstract void update();
	public abstract void display();
	
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
