package slugs;

public abstract class InventoryItem{
	
	Player owner;
	
	abstract void use();
	abstract void update();
	abstract void display();
	
	public void setOwner(Player p)
	{
		this.owner = p;
	}
}
