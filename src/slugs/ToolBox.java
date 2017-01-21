package slugs;

import org.jbox2d.common.Vec2;

import processing.core.PConstants;
import shiffman.box2d.Box2DProcessing;

public class ToolBox extends ItemBox {

	public ToolBox(Slugs p, Box2DProcessing world, Vec2 spawnPoint, int itemID) {
		super(p, world, spawnPoint, 20, 12, itemID);
	}

	protected void render() {
		p.stroke(0, 0, 0);
		p.fill(2, 92, 147);
		p.rectMode(PConstants.CENTER);
		p.rect(0, 0, w, h);
	}

}
