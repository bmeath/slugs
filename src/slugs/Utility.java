package slugs;

public abstract class Utility implements InventoryItem
{

	public Utility(Slugs p, String name, int quantity)
	{
	}
	
	public abstract void display();
	public abstract void use();
}