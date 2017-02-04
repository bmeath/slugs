package slugs;

import java.util.HashMap;

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
	
	HashMap<String, Player> players = new HashMap<String, Player>();
	
	// hashmap of inventory item instances
	HashMap<String, InventoryItem> itemStore = new HashMap<String, InventoryItem>();
	
	// hashmap of quantities of each item a player has
	HashMap<String, Integer> itemQuantities = new HashMap<String, Integer>();
	
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
		map = new Terrain(this, world, 0.5f);
		gameState = 0;
		loadWeapons("weapons.xml");
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
		
		textAlign(CENTER);
		text("SLUGS", width/2, height/3);
		text("Click to begin", width/2, height/2);
		if (mousePressed)
		{
			players.put("Brendan", new Player("Brendan", this, world, map.randomSpawn(), itemQuantities, itemStore));
			gameState = 1;
		}
	}
	
	public void gameScreen()
	{
		background(185, 225, 255);
		map.display();
		for(Player p: players.values())
		{
			p.display();
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
		world.step();
	}
	
	/* create weapons from XML data */
	public void loadWeapons(String path) 
	{
		XML weaponFile = loadXML(path);
		XML[] weapons = weaponFile.getChildren("weapon");
		for (int i = 0; i < weapons.length; i++)
		{
			int defaultAmount = weapons[i].getInt("default-amount");
			String type = weapons[i].getString("type");
			String name = weapons[i].getChild("name").getContent();
			if (type.equals("bomb"))
			{
				
				BombWeapon weapon;
				
				XML projectile = weapons[i].getChild("projectile");
				
				int projectileCount;
				int damage;
				float restitution;
				int clusterCount;
				int clusterDamage;
				float clusterVelocity;
				float clusterRestitution;
				boolean explodeOnImpact;
				int timeout;
				
				projectileCount = projectile.getInt("amount");
				if (projectile.getString("explode-on-impact").equals("true"))
				{
					explodeOnImpact = true;
					restitution = 0;
				}
				else
				{
					explodeOnImpact = false;
					restitution = projectile.getChild("bounciness").getFloatContent(0);
				}
				
				if(projectile.hasAttribute("timeout"))
				{
					timeout = projectile.getInt("timeout") * 1000;
				}
				else
				{
					timeout = 0;
				}
				damage = projectile.getChild("damage").getIntContent();
				
				XML cluster = projectile.getChild("cluster");
				clusterCount = cluster.getInt("amount");
				if( clusterCount > 0)
				{

					clusterVelocity = cluster.getChild("velocity").getFloatContent();
					clusterDamage = cluster.getChild("damage").getIntContent();
					clusterRestitution = cluster.getChild("bounciness").getFloatContent(0);
					weapon = new BombWeapon(this, world, players, map, projectileCount, damage, restitution,
							clusterCount, clusterDamage, clusterVelocity, clusterRestitution, 
							explodeOnImpact, timeout);
				}
				else
				{
					weapon = new BombWeapon(this, world, players, map, projectileCount, damage, restitution, explodeOnImpact, timeout);
				}
				itemStore.put(name, weapon);
				itemQuantities.put(name, defaultAmount);
			}
			if (type.equals("melee"))
			{
				
			}
			
			if (type.equals("bullet"))
			{
				
			}
			
			if (type.equals("airdrop"))
			{
				
			}
		}
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
	
	public void mouseClicked()
	{
		players.get("Brendan").mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	public void beginContact(Contact c)
	{
		// get object which body attached to fixture belongs to
		Object a = c.getFixtureA().getBody().getUserData();
		Object b = c.getFixtureB().getBody().getUserData();
		
		/* check if the player is standing on something.
		 * we don't really care what it is they're standing on.
		 */
		if (a instanceof Player && b instanceof Terrain)
		{
			Player player = (Player) a;
			
			// check if player should take fall damage
			if(!player.isGrounded() && player.getFallDistance() < -90)
			{
				player.hurt(5);
			}
			player.setGrounded(true);
			
		}
		if (b instanceof Player && a instanceof Terrain)
		{
			Player player = (Player) b;
			
			// check if player should take fall damage
			if(!player.isGrounded() && player.getFallDistance() < -90)
			{
				player.hurt(5);
			}
			player.setGrounded(true);
		}
		
		if (a instanceof Projectile)
		{
			Projectile proj = (Projectile) a;
			proj.explode();
		}
		
		if (b instanceof Projectile)
		{
			Projectile proj = (Projectile) b;
			proj.explode();
		}
	}
		
	public void endContact(Contact c)
	{

	}
}