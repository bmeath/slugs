package slugs;

import java.awt.Rectangle;
import java.util.HashMap;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

import processing.core.*;
import processing.data.XML;

import shiffman.box2d.*;

public class Slugs extends PApplet
{
	class PauseMenu
	{
		private final String[] menuActions = {"Resume", "Quit"};
		private Vec2 dimensions;
		private Vec2 location;
		// memorise the width, as dimensions will be altered during open/close animation
		private float widthMem;
		private float heightMem;
		private boolean show;
		HashMap<Rectangle, String> menuButtons = new HashMap<Rectangle, String>();
		
		
		public PauseMenu(int w, int h)
		{
			dimensions = new Vec2(w, h);
			location = new Vec2(width/2, height/2);
			widthMem = dimensions.x;
			heightMem = dimensions.y;
			show = false;
			int dy = (int) (dimensions.y / menuActions.length);
			int i = 0;
			for (int y = (int) (location.y - dimensions.y/2) ; y < location.y + dimensions.y/2; y += dy)
			{
				menuButtons.put(new Rectangle((int) (location.x - dimensions.x/2), y, (int) dimensions.x, dy), menuActions[i++]);
			}
			
			// so menu doesn't appear shrinking away start of game
			dimensions.x = 0;
			dimensions.y = 0;
		}
		
		void display()
		{
			fill(0);
			if (show)
			{
				if (dimensions.x < widthMem)
				{
					dimensions.x += 30;
					dimensions.y += 30 * heightMem/widthMem;
				}				
			}
			else
			{
				if (dimensions.x > 0)
				{
					dimensions.x -= 30;
					dimensions.y -= 30 * heightMem/widthMem;
				}
				
				if (dimensions.x < 0)
				{
					dimensions.x = 0;
					dimensions.y = 0;
				}
			}
			rectMode(CENTER);
			stroke(255);
			strokeWeight(2);
			noStroke();
			// black background
			rect(location.x, location.y, dimensions.x, dimensions.y);
			
			stroke(255);
			strokeWeight(2);
			if(dimensions.x >= widthMem)
			{	
				// do this if menu dimensions get incremented beyond full size
				dimensions.x = widthMem;
				dimensions.y = heightMem;
				
				rectMode(CORNER);
				for (Rectangle r: menuButtons.keySet())
				{
					if(mouseX > r.x && mouseX < r.x + r.width && mouseY > r.y && mouseY < r.y + r.height)
					{
						fill(127, 176, 255);
					}
					else
					{
						
						noFill();
					}
					rect(r.x, r.y, r.width, r.height);
					textAlign(CENTER, CENTER);
					fill(255);
					text(menuButtons.get(r), r.x + r.width/2, r.y + r.height/2);
				}
			}
		}
		
		private void toggle()
		{
			show ^= true;
		}

		public void click() 
		{
			if (show)
			{
				for (Rectangle r: menuButtons.keySet())
				{
					if(r.contains(mouseX, mouseY))
					{
						switch (menuButtons.get(r))
						{
							case "Quit":
								setup();
								break;
							default:
								toggle();
								break;
						}
					}
				}
			}
		}

		public boolean isShown() 
		{
			return show;
		}
	}
	public static void main(String[] args)
	{
		PApplet.main("slugs.Slugs");
	}
	
	boolean[] keys = new boolean[1000];
	Box2DProcessing world;
	int gameState;
	public Terrain map;
	PauseMenu pauseMenu;
	
	HashMap<String, Player> players;
	
	// hashmap of inventory item instances
	HashMap<String, InventoryItem> itemStore;
	
	// hashmap of quantities of each item a player has
	HashMap<String, Integer> itemQuantities;
	
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
		players = new HashMap<String, Player>();
		
		pauseMenu = new PauseMenu(250, 150);
		itemQuantities = new HashMap<String, Integer>();
		itemStore = new HashMap<String, InventoryItem>();
		loadWeapons("weapons.xml");
		
		
		
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
		if (!pauseMenu.isShown())
		{
			world.step();
		}
		pauseMenu.display();
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
		if (keyCode == SHIFT)
		{
			pauseMenu.toggle();
		}
		keys[keyCode] = false;
	}
	
	public boolean checkKey(int k)
	{		
		if (pauseMenu.isShown())
		{
			return false;
		}
		if (keys.length >= k)
		{
			return keys[k] || keys[Character.toUpperCase(k)];
		}
		return false;
	}
	
	public void mouseClicked()
	{
		if (gameState == 1)
		{
			if (!pauseMenu.isShown())
			{
				players.get("Brendan").mouseClicked(mouseX, mouseY, mouseButton);
			}
			else
			{
				if (mouseButton == LEFT)
				{
					pauseMenu.click();
				}
			}
		}
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
			player.setGrounded();
			
		}
		
		if (b instanceof Player && a instanceof Terrain)
		{
			Player player = (Player) b;
			
			// check if player should take fall damage
			if(!player.isGrounded() && player.getFallDistance() < -90)
			{
				player.hurt(5);
			}
			player.setGrounded();
		}
		
		if (a instanceof Projectile)
		{
			Projectile p= (Projectile) a;
			p.explode();
		}
		
		if (b instanceof Projectile)
		{
			Projectile p = (Projectile) b;
			p.explode();
		}
	}
		
	public void endContact(Contact c)
	{
		
	}
}