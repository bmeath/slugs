package slugs;

import org.jbox2d.common.Vec2;

import shiffman.box2d.Box2DProcessing;

public class ToolBox extends ItemBox {

	public ToolBox(Slugs p, Box2DProcessing world, Vec2 spawnPoint, int itemID) {
		super(p, world, spawnPoint, 20, 12, itemID);
	}
	
	protected void update()
	{
		
	}
}
