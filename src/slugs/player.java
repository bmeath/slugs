package slugs;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import shiffman.box2d.Box2DProcessing;

public class Player extends Entity
{
	String name;
	
	Vec2 left;
	Vec2 right;
	Vec2 farLeft;
	Vec2 farRight;
	Vec2 highJump;
	Vec2 jump;
	int lastLanded;
	int jumpCooldown;
	boolean dir; // true if facing right, false otherwise
	boolean showInventory;
	InventoryItem currentItem;
	HashMap<String, Integer> inventory;
	HashMap<String, InventoryItem> itemStore;
	
	PImage leftSlug, rightSlug;
	RevoluteJoint motor;
	private boolean grounded;
	int health;
	private final int maxHealth = 250;
	float fallY;
	ItemMenu itemMenu;
	String currentItemName;
	
	// used when showing how much damage the slug took
	int showDamageTaken;
	int damageTaken;
	
	class ItemMenu
	{
		private Vec2 dimensions;
		// memorise the width, as dimensions will be altered during open/close animation
		private float widthMem;
		private boolean show;
		private final int rows = 4;
		private final int cols = 8;
		
		HashMap<Rectangle, String> itemButtons = new HashMap<Rectangle, String>();
		
		
		
		public ItemMenu()
		{
			dimensions = new Vec2(150, 250);
			widthMem = dimensions.x;
			show = false;
			int dx = (int) (dimensions.x / rows);
			int dy = (int) (dimensions.y / cols);
			Iterator<String> i = inventory.keySet().iterator();
			for (int y = (int) ( p.height - dimensions.y) ; y < p.height; y += dy)
			{
				for (int x = (int) ( p.width - dimensions.x) ; x < p.width; x += dx)
				{
					if (i.hasNext())
					{
						itemButtons.put(new Rectangle(x, y, dx, dy), i.next());
					}
					else
					{
						itemButtons.put(new Rectangle(x, y, dx, dy), "");
					}
				}
			}
			
			// so menu doesn't appear sliding away at start of game
			dimensions.x = 0;
			
			
		}
		
		public void toggle()
		{
			show ^= true;
		}
		
		void display()
		{
			p.fill(0);
			if (show)
			{
				if(dimensions.x < widthMem)
				{
					dimensions.x += 10;
				}				
			}
			else
			{
				if(dimensions.x > 0)
				{
					dimensions.x -= 10;
				}
			}
			p.rectMode(PConstants.CORNER);
			p.stroke(255);
			p.strokeWeight(2);
			// item name field
			p.rect(p.width - dimensions.x, p.height - dimensions.y - 21, dimensions.x, 20);
			p.noStroke();
			// black background
			p.rect(p.width - dimensions.x, p.height - dimensions.y - 1, dimensions.x, dimensions.y);
			
			p.noFill();
			p.stroke(127);
			p.strokeWeight(1);
			if(dimensions.x == widthMem)
			{	
				for (Rectangle r: itemButtons.keySet())
				{
					p.textSize(12);
					if (p.mouseX > r.x && p.mouseX < r.x + r.width && p.mouseY > r.y && p.mouseY < r.y + r.height)
					{
						p.strokeWeight(3);
						p.stroke(127, 176, 255);
						p.textAlign(PConstants.LEFT, PConstants.CENTER);
						p.fill(255);
						// item name
						p.text(itemButtons.get(r), p.width - dimensions.x + 5, p.height - dimensions.y - 12);
					}
					else
					{
						p.strokeWeight(1);
						p.stroke(127);
					}
					
					if (!itemButtons.get(r).equals("")) // if there is an item associated with that button
					{
						p.textAlign(PConstants.RIGHT, PConstants.TOP);
						p.fill(255);
						
						p.text(inventory.get(itemButtons.get(r)), r.x + r.width, r.y + r.height/2);
					}
					p.noFill();
					p.rect(r.x, r.y, r.width, r.height);
				}
			}
			
			p.strokeWeight(2);
			p.stroke(255);
			p.noFill();
			// outer rectangle
			p.rect(p.width - dimensions.x, p.height - dimensions.y - 1, dimensions.x, dimensions.y);
		}

		public void click() {
			if (show)
			{
				for (Rectangle r: itemButtons.keySet())
				{
					if(r.contains(p.mouseX, p.mouseY))
					{
						toggle();
						if (!itemButtons.get(r).equals(""))
						{
							selectItem(itemButtons.get(r));
						}
					}
				}
			}
		}
	}
	
	public Player(String name, Slugs p, Box2DProcessing world, Vec2 spawnPoint, HashMap<String, Integer> inventory, 
			HashMap<String, InventoryItem> itemStore, int health, float scaleFactor)
	{
		super(p, world, spawnPoint, BodyType.DYNAMIC, true, 1, 1f, 0f);
		left = new Vec2(-400 * scaleFactor, 0);
		right = new Vec2(400 * scaleFactor, 0);
		farLeft = new Vec2(-800 * scaleFactor, 0);
		farRight = new Vec2(800 * scaleFactor, 0);
		jump = new Vec2(0, 1000 * scaleFactor);
		highJump = new Vec2(0, 1500 * scaleFactor);
		dir = true;
		lastLanded = p.millis();
		jumpCooldown = 750;
		
		colour = p.color(125, 255, 125);
		leftSlug = p.loadImage("leftslug.png");
		rightSlug = p.loadImage("rightslug.png");
		
		PolygonShape shape = new PolygonShape();
		// define the shape
		shape = new PolygonShape();
		
		// create main body
		float w = world.scalarPixelsToWorld(4 * scaleFactor);
		float h = world.scalarPixelsToWorld(7 * scaleFactor);
		shape.setAsBox(w, h);
		
		fd.shape = shape;
		bodyList.get(0).createFixture(fd);
		bodyList.get(0).setUserData(this);
		
		// create wheel
		BodyDef wheelBD = new BodyDef();
		wheelBD.type = BodyType.DYNAMIC;
		
		Vec2 wheelPos = world.coordPixelsToWorld(spawnPoint);
		wheelBD.position.set(new Vec2(wheelPos.x, wheelPos.y - h));
		
		bodyList.add(world.createBody(wheelBD));
		
		CircleShape wheelShape = new CircleShape();
	    wheelShape.m_radius = w;
	    fd.shape = wheelShape;
	    bodyList.get(1).createFixture(fd);
	    bodyList.get(1).setUserData(this);
	    
	    RevoluteJointDef revJD = new RevoluteJointDef();
	    revJD.initialize(bodyList.get(0), bodyList.get(1), bodyList.get(1).getWorldCenter());
	    revJD.motorSpeed = 0;
	    revJD.maxMotorTorque = 1000f;
	    revJD.enableMotor = true;
	    motor = (RevoluteJoint) world.createJoint(revJD);
	    
	    this.inventory = new HashMap<String, Integer>(inventory);
	    this.itemStore = itemStore;
	    this.health = health;
	    this.fallY = getPixelLocation().y;
	    
	    itemMenu = new ItemMenu();
	    this.name = name;
	    
	    /* set to large negative number at start,
	     * otherwise a zero damage text will appear above players head
	     */
	    showDamageTaken = - 2001;
	}
	
	// create player with default size
	public Player(String name, Slugs p, Box2DProcessing world, Vec2 spawnPoint, HashMap<String, Integer> inventory, 
			HashMap<String, InventoryItem> itemList)
	{
		this(name, p, world, spawnPoint, inventory, itemList, 100, 1f);
	}
	
	// select the item for use
	public void selectItem(String name)
	{
		if (inventory.get(name) > 0)
		{
			currentItem = (InventoryItem) itemStore.get(name).clone();
			currentItem.setOwner(this);
			currentItemName = name;
		}
	}
	
	// select the first item in the inventory by default
	public void selectFirstItem()
	{
		for (String s: inventory.keySet())
		{
			selectItem(s);
			break;
		}
	}
	
	// give player exactly one of the item
	public void giveItem(String name)
	{
		giveItem(name, 1);
	}
	
	// give player a given quantity of the item
	public void giveItem(String name, int quantity)
	{
		if(inventory.containsKey(name))
		{
			inventory.replace(name, inventory.get(name) + quantity);
		}
		else
		{
			inventory.put(name, quantity);
		}
	}
	
	// replace the players inventory with a copy of the argument
	public void setInventory(HashMap<String, Integer> inventory)
	{
		this.inventory = new HashMap<String, Integer>(inventory);
	}
	
	// take exactly one of an item from the player
	public void removeItem(String name)
	{
		removeItem(name, 1);	
	}
	
	// take a given amount of an item from the player
	public void removeItem(String name, int quantity)
	{
		int newQuantity = inventory.get(name) - quantity;
		if(newQuantity < 0)
		{
			inventory.replace(name, 0);
		}
		else
		{
			inventory.replace(name, newQuantity);
		}
	}
	
	public boolean isGrounded()
	{
		return grounded;
	}
	
	public void setGrounded()
	{
		if (!grounded)
		{
			lastLanded = p.millis();
		}
		grounded = true;
	}
	
	protected void update()
	{
		if(currentItem != null)
		{
			currentItem.display();
		}
		
		p.pushMatrix();
		p.imageMode(PConstants.CENTER);
		Vec2 loc = getPixelLocation();
		p.image(dir ? rightSlug : leftSlug, loc.x, loc.y, 24, 24);
		
		// name and health above head
		p.textAlign(PConstants.CENTER, PConstants.CENTER);
		p.fill(0);
		p.textSize(12);
		p.text(name, loc.x, loc.y - 23);
		p.text(health, loc.x, loc.y - 35);
		
		if (showDamageTaken + 2000 > p.millis())
		{
			p.fill(255, 0, 0);
			p.textSize(16);
			int h = (int) PApplet.map(p.millis() - showDamageTaken, 0, 3000, 30, 75);
			p.text(-damageTaken, loc.x, loc.y - h);
		}
		
		
		p.popMatrix();
		
		itemMenu.display();
	}

	public void hurt(int damage) 
	{
		health -= damage;
		if(health < 0)
		{
			health = 0;
		}
		// store damage taken for displaying it above head
		damageTaken = damage;
		showDamageTaken = p.millis();
	}
	
	public void heal(int health)
	{
		this.health += health;
		if(this.health > maxHealth)
		{
			this.health = maxHealth;
		}
	}
	
	public float getFallDistance()
	{
		return fallY - getPixelLocation().y;
	}

	public void stop() 
	{
		motor.setMotorSpeed(0);
	}
	
	public void useItem() 
	{
		if (currentItem != null)
		{
			currentItem.use();
		}
	}

	public void goLeft() 
	{
		motor.setMotorSpeed(6);
		dir = false;
	}

	public void goRight()
	{
		motor.setMotorSpeed(-6);
		dir = true;
	}

	public void jumpForward()
	{
		if(grounded == true)
		{
			if(p.millis() > lastLanded + jumpCooldown)
			{
				fallY = getPixelLocation().y;
				applyForce(jump.add(dir ? farRight : farLeft));
				grounded = false;
			}
		}
	}

	public void jumpBack() 
	{
		if(grounded == true)
		{
			if(p.millis() > lastLanded + jumpCooldown)
			{
				fallY = getPixelLocation().y;
				applyForce(highJump.add(dir ? left : right));
				grounded = false;
			}
		}
	}

	public boolean usedItem() 
	{
		if (currentItem != null)
		{
			if (currentItem.used)
			{
				removeItem(currentItemName);
				return true;
			}
		}
		return false;
	}

	public void pressUp() {
		if (currentItem != null)
		{
			currentItem.pressUp();
		}
	}

	public void pressDown() {
		if (currentItem != null)
		{
			currentItem.pressDown();
		}
	}
}
