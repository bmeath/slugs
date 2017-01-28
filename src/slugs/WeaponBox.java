package slugs;
import org.jbox2d.common.Vec2;

import shiffman.box2d.Box2DProcessing;

public class WeaponBox extends ItemBox
{
	
	public WeaponBox(Slugs p, Box2DProcessing world, Vec2 spawnPoint, int itemID)
	{
		super(p, world, spawnPoint, 20, 20, itemID);
	}
	
	protected void update()
	{
		
	}
}
