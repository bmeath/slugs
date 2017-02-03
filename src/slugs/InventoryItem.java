package slugs;

public abstract class InventoryItem{
	
	Player owner;
	
	public abstract void use();
	protected abstract void update();
	public abstract void display();
	
	public void setOwner(Player p)
	{
		this.owner = p;
	}
}
