package slugs;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;

import processing.core.*;
import processing.data.XML;
import shiffman.box2d.*;

public class Slugs extends PApplet
{
	public static void main(String[] args)
	{
		PApplet.main("slugs.Slugs");
	}
	
	public Box2DProcessing world;
	public int gameState;
	public Terrain map;
	ArrayList<WeaponBox> crates;
	Player player1;
	
	public void settings()
	{
		size(1280, 720);
	}
	
	public void setup()
	{
		world = new Box2DProcessing(this);
		world.createWorld();
		map = new Terrain(this);
		crates = new ArrayList<WeaponBox>();
		gameState = 0;
	}
	
	public void draw()
	{
		// current state of game
		switch (gameState)
		{
			case 0:
				initScreen();
				break;
			case 1:
				gameScreen();
				break;
			case 2:
				endScreen();
				break;
			case 3:
				testScreen();
				break;
		}
	}
	
	public void initScreen()
	{
		background(0);
		
		// load all weapons and tools
		//loadWeapons("weapons.xml");
		
		textAlign(CENTER);
		text("SLUGS", width/2, height/3);
		text("Click to begin", width/2, height/2);
		if (mousePressed)
		{
			player1 = new Player(this, world, randomSpawn(map, 15));
			gameState = 1;
		}
	}
	
	public void gameScreen()
	{
		
		background(255);
		map.display();
		if (mousePressed)
		{
			WeaponBox c = new WeaponBox(this, world, new Vec2(mouseX, mouseY), 0);
			crates.add(c);
		}
		for(WeaponBox c: crates)
		{
			c.display();
		}
		player1.display();
		world.step();
	}
	
	public void endScreen()
	{
	}
	
	public void testScreen()
	{
		background(255);
		map.display();
		if (mousePressed)
		{
			WeaponBox c = new WeaponBox(this, world, new Vec2(mouseX, mouseY), 0);
			crates.add(c);
		}
		for(WeaponBox c: crates)
		{
			c.display();
		}
		world.step();
	}
	
	/* create weapons from XML data */
	public void loadWeapons(String path) 
	{
		XML weaponFile = loadXML(path);
		XML[] weapons = weaponFile.getChildren("item");
		for(int i = 0; i < weapons.length; i++)
		{
			
		}
	}
	
	/* picks a random coordinate h pixels above the surface of the terrain */
	Vec2 randomSpawn(Terrain map, float h)
	{
		Vec2 spawn = new Vec2();
		int i = (int) random(map.screenMap.size());
		spawn.x = map.screenMap.get(i).x;
		spawn.y = map.screenMap.get(i).y - h;
		System.out.println(spawn.toString());
		return spawn;
	}
}