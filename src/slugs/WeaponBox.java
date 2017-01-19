package slugs;
import org.jbox2d.common.Vec2;

import processing.core.PConstants;
import shiffman.box2d.Box2DProcessing;

public class WeaponBox extends ItemBox
{
	
	public WeaponBox(Slugs p5, Box2DProcessing world, Vec2 spawnPoint, int itemID)
	{
		super(p5, world, spawnPoint, 20, 20, itemID);
	}
	
	protected void render()
	{
		p5.stroke(0, 0, 0);
		p5.fill(247, 238, 158);
		p5.rectMode(PConstants.CENTER);
		p5.rect(0, 0, w, h);
	}
}
