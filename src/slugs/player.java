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
	boolean dir; // true if facing right, false otherwise
	boolean showInventory;
	InventoryItem currentItem;
	HashMap<String, Integer> inventory;
	HashMap<String, InventoryItem> itemStore;
	
	PImage leftSlug, rightSlug;
	RevoluteJoint motor;
	private boolean grounded;
	int health;
	float fallY;
	ItemMenu itemMenu;
	
	class ItemMenu
	{
		private Vec2 dimensions;
		// memorise the width, as dimensions will be altered during open/close animation
		private float widthMem;
		private boolean show;
		HashMap<Rectangle, String> itemButtons = new HashMap<Rectangle, String>();
		
		
		public ItemMenu()
		{
			dimensions = new Vec2(150, 250);
			widthMem = dimensions.x;
			show = false;
			int dx = (int) (dimensions.x / 4);
			int dy = (int) (dimensions.y / 8);
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
			
			// select the first item in the inventory by default
			for (String s: inventory.keySet())
			{
				selectItem(s);
				break;
			}
		}
		
		private void toggle()
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
					if(p.mouseX > r.x && p.mouseX < r.x + r.width && p.mouseY > r.y && p.mouseY < r.y + r.height)
					{
						p.strokeWeight(2);
						p.stroke(127, 176, 255);
						p.rect(r.x, r.y, r.width, r.height);
						p.textAlign(PConstants.LEFT, PConstants.CENTER);
						p.fill(255);
						p.text(itemButtons.get(r), p.width - dimensions.x, p.height - dimensions.y - 15);
						p.noFill();
						p.strokeWeight(1);
						p.stroke(127);
					}
					else
					{
						p.rect(r.x, r.y, r.width, r.height);
					}
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
						selectItem(itemButtons.get(r));
					}
				}
			}
		}
	}
	
	public Player(String name, Slugs p, Box2DProcessing world, Vec2 spawnPoint, HashMap<String, Integer> inventory, 
			HashMap<String, InventoryItem> itemStore, int health, float scaleFactor)
	{
		super(p, world, spawnPoint, BodyType.DYNAMIC, true, 1, 1f, 0f);
		left = new Vec2(-250 * scaleFactor, 0);
		right = new Vec2(250 * scaleFactor, 0);
		farLeft = new Vec2(-500 * scaleFactor, 0);
		farRight = new Vec2(500 * scaleFactor, 0);
		jump = new Vec2(0, 750 * scaleFactor);
		highJump = new Vec2(0, 1250 * scaleFactor);
		dir = true;
		lastLanded = p.millis();
		
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
	    revJD.motorSpeed = -PConstants.PI*2;
	    revJD.maxMotorTorque = 1000f;
	    revJD.enableMotor = true;
	    motor = (RevoluteJoint) world.createJoint(revJD);
	    
	    this.inventory = new HashMap<String, Integer>(inventory);
	    System.out.println(inventory.toString());
	    this.itemStore = itemStore;
	    this.health = health;
	    this.fallY = getPixelLocation().y;
	    
	    itemMenu = new ItemMenu();
	    this.name = name;
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
		currentItem = (InventoryItem) itemStore.get(name).clone();
		currentItem.setOwner(this);
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
		this.grounded = true;
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
		p.textAlign(PConstants.CENTER, PConstants.CENTER);
		p.fill(0);
		p.text(name, loc.x, loc.y - 23);
		p.text(health, loc.x, loc.y - 35);
		
		p.popMatrix();
		
		// use weapon/utility
		if (p.checkKey(' '))
		{
			currentItem.use();
		}
		
		if (p.checkKey(PConstants.LEFT))
		{
			motor.setMotorSpeed(5);
			dir = false;
		}
		else if (p.checkKey(PConstants.RIGHT))
		{
			motor.setMotorSpeed(-5);
			dir = true;
		}
		else
		{
			stop();
		}
		
		//forward jump
		if (p.checkKey(PConstants.ENTER))
		{
			if(grounded == true)
			{
				if(p.millis() > lastLanded + 1000)
				{
					fallY = getPixelLocation().y;
					applyForce(jump.add(dir ? farRight : farLeft));
					grounded = false;
				}
			}
		}
		
		// backward jump (like backflip in Worms)
		if (p.checkKey(PConstants.BACKSPACE))
		{
			if(grounded == true)
			{
				if(p.millis() > lastLanded + 1000)
				{
					fallY = getPixelLocation().y;
					applyForce(highJump.add(dir ? left : right));
					grounded = false;
				}
			}
		}
		
		itemMenu.display();
	}

	public void hurt(int damage) 
	{
		health -= damage;
		if(health < 0)
		{
			health = 0;
		}
	}
	
	public float getFallDistance()
	{
		return fallY - getPixelLocation().y;
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) 
	{
		if (mouseButton == PConstants.LEFT)
		{
			itemMenu.click();
		}
		
		if (mouseButton == PConstants.RIGHT)
		{
			itemMenu.toggle();
		}
	}

	public void stop() {
		motor.setMotorSpeed(0);
	}

}
