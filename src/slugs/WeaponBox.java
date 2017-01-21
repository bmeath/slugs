package slugs;
import org.jbox2d.common.Vec2;

import processing.core.PConstants;
import shiffman.box2d.Box2DProcessing;

public class WeaponBox extends ItemBox
{
	
	public WeaponBox(Slugs p, Box2DProcessing world, Vec2 spawnPoint, int itemID)
	{
		super(p, world, spawnPoint, 20, 20, itemID);
	}
	
	protected void render()
	{
		p.stroke(0, 0, 0);
		p.fill(247, 238, 158);
		p.rectMode(PConstants.CENTER);
		p.rect(0, 0, w, h);
	}
}
