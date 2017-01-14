package slugs;

import org.jbox2d.common.Vec2;

public abstract class Item
{
	int id;
	String name;
	
	/* where the item will appear,
	 * relative to player's centre,
	 * when the player is holding it
	 */
	Vec2 centre;
	
	public Item(int id, String name, Vec2 centre)
	{
		this.id = id;
		this.name = name;
		this.centre = centre;
	}

}
