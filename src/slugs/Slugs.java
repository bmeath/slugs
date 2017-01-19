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
		loadWeapons("weapons.xml");
		
		textAlign(CENTER);
		text("SLUGS", width/2, height/3);
		text("Click to begin", width/2, height/2);
		if (mousePressed)
		{
			player1 = new Player(this, world, randomSpawn(map, 10));
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
	
	/* generate a random location on the terrain at the given height above the surface */
	Vec2 randomSpawn(Terrain map, float h)
	{
		Vec2 spawn = new Vec2();
		spawn.x = (int) random(0, map.screenMap.size() - 1);
		spawn.y = map.screenMap.get((int) spawn.x).y + h;
		if (spawn.y > height)
		{
			spawn.y = height;
		}
		return spawn;
	}
	
	/*public static void itemParser(String path) 
	{
		try 
		{
			File itemFile = new File(path);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document itemsDoc = docBuilder.parse(itemFile);
			itemsDoc.getDocumentElement().normalize();
			NodeList itemNodeList = itemsDoc.getElementsByTagName("item");
			for(int i = 0; i < itemNodeList.getLength(); i++)
			{
				Node itemNode = itemNodeList.item(i);
				
			}
		} 
		catch (ParserConfigurationException | SAXException | IOException e) 
		{
			e.printStackTrace();
		}		
	}*/
}