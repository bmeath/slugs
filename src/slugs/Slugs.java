package slugs;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

import processing.core.*;
import processing.data.XML;

import shiffman.box2d.*;

public class Slugs extends PApplet
{
	public static void main(String[] args)
	{
		PApplet.main("slugs.Slugs");
	}
	
	boolean[] keys = new boolean[1000];
	Box2DProcessing world;
	int gameState;
	Terrain map;
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
		world.listenForCollisions();
		world.setGravity(0f, -20f);
		map = new Terrain(this, 0.5f);
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
			player1 = new Player(this, world, randomSpawn());
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
		for (WeaponBox c: crates)
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
		for (WeaponBox c: crates)
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
		for (int i = 0; i < weapons.length; i++)
		{
			
		}
	}
	
	/* picks a random position along the terrain at h pixels above surface*/
	public Vec2 randomSpawn(Terrain map, float h)
	{
		Vec2 spawn = new Vec2();
		int i = (int) random(map.screenMap.size());
		spawn.x = map.screenMap.get(i).x;
		spawn.y = map.screenMap.get(i).y - h;
		return spawn;
	}
	
	public Vec2 randomSpawn()
	{
		return randomSpawn(map, 40);
	}
	
	public void keyPressed()
	{ 
	  keys[keyCode] = true;
	}
	 
	public void keyReleased()
	{
	  keys[keyCode] = false; 
	}
	
	public boolean checkKey(int k)
	{
	  if (keys.length >= k) 
	  {
	    return keys[k] || keys[Character.toUpperCase(k)];
	  }
	  return false;
	}
	
	public void beginContact(Contact c)
	{
		// get object which body attached to fixture belongs to
		Object a = c.getFixtureA().getBody().getUserData();
		Object b = c.getFixtureB().getBody().getUserData();
		
		/* check if the player is standing on something.
		 * we don't really care what it is they're standing on.
		 */
		if(a instanceof Player)
		{
			Player player = (Player) a;
			player.setGrounded(true);
		}
		if(b instanceof Player)
		{
			Player player = (Player) b;
			player.setGrounded(true);
		}
	}
	
	public void endContact(Contact c)
	{

	}
}