package slugs;

import org.jbox2d.common.Vec2;

import processing.core.PConstants;
import shiffman.box2d.Box2DProcessing;

public class ToolBox extends ItemBox {

	public ToolBox(Slugs p5, Box2DProcessing world, Vec2 spawnPoint, int itemID) {
		super(p5, world, spawnPoint, 20, 12, itemID);
	}

	protected void render() {
		p5.translate(pos.x, pos.y);
		p5.rotate(-body.getAngle());
		p5.stroke(0, 0, 0);
		p5.fill(2, 92, 147);
		p5.rectMode(PConstants.CENTER);
		p5.rect(0, 0, w, h);
	}

}
