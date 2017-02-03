package slugs;

import java.util.HashMap;

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
	int lastJumped;
	boolean dir; // true if facing right, false otherwise
	boolean showInventory;
	InventoryItem currentItem;
	HashMap<String, Integer> inventory;
	HashMap<String, InventoryItem> itemList;
	private final Vec2 inventoryDimensions = new Vec2(150, 250);
	PImage leftSlug, rightSlug;
	RevoluteJoint motor;
	private boolean grounded;
	int health;
	
	public Player(Slugs p, Box2DProcessing world, Vec2 spawnPoint, HashMap<String, Integer> inventory, HashMap<String, InventoryItem> itemList, float scaleFactor)
	{
		super(p, world, spawnPoint, BodyType.DYNAMIC, true, 1, 1f, 0f, 2);
		left = new Vec2(-250 * scaleFactor, 0);
		right = new Vec2(250 * scaleFactor, 0);
		farLeft = new Vec2(-500 * scaleFactor, 0);
		farRight = new Vec2(500 * scaleFactor, 0);
		jump = new Vec2(0, 750 * scaleFactor);
		highJump = new Vec2(0, 1250 * scaleFactor);
		dir = true;
		lastJumped = p.millis();
		
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
		bodyList[0].createFixture(fd);
		bodyList[0].setUserData(this);
		
		// create wheel
		BodyDef wheelBD = new BodyDef();
		wheelBD.type = BodyType.DYNAMIC;
		
		Vec2 wheelPos = world.coordPixelsToWorld(spawnPoint);
		wheelBD.position.set(new Vec2(wheelPos.x, wheelPos.y - h));
		
		bodyList[1] = world.createBody(wheelBD);
		
		CircleShape wheelShape = new CircleShape();
	    wheelShape.m_radius = w;
	    fd.shape = wheelShape;
	    bodyList[1].createFixture(fd);
	    bodyList[1].setUserData(this);
	    
	    RevoluteJointDef revJD = new RevoluteJointDef();
	    revJD.initialize(bodyList[0], bodyList[1], bodyList[1].getWorldCenter());
	    revJD.motorSpeed = -PConstants.PI*2;
	    revJD.maxMotorTorque = 750f;
	    revJD.enableMotor = true;
	    motor = (RevoluteJoint) world.createJoint(revJD);
	    
	    this.inventory = new HashMap<String, Integer>(inventory);
	    this.itemList = itemList;
	}
	
	// create player with default size
	public Player(Slugs p, Box2DProcessing world, Vec2 spawnPoint, HashMap<String, Integer> inventory, HashMap<String, InventoryItem> itemList)
	{
		this(p, world, spawnPoint, inventory, itemList, 1f);
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
	
	// set the players inventory
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
			System.out.println("Warning: taking " + quantity + " of " + name + " from a player with only " + inventory.get(name) + "of that item");
			inventory.replace(name, 0);
		}
		else
		{
			inventory.replace(name, newQuantity);
		}
	}
	
	public void selectItem(String name)
	{
		currentItem = itemList.get(name);
	}
	
	public boolean isGrounded()
	{
		return grounded;
	}
	
	public void setGrounded(boolean isGrounded)
	{
		this.grounded = isGrounded;
	}
	
	public void toggleInventory()
	{
		showInventory ^= true;
	}
	
	public void showInventory()
	{
		p.fill(0);
		p.stroke(225);
		p.strokeWeight(1);
		p.rectMode(PConstants.CORNER);
		p.rect(p.width - inventoryDimensions.x, p.height - inventoryDimensions.y , inventoryDimensions.x, inventoryDimensions.y);
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
			motor.setMotorSpeed(0);
		}
		
		//forward jump
		if (p.checkKey(PConstants.ENTER))
		{
			if(grounded == true)
			{
				if(p.millis() > lastJumped + 1000)
				{
					applyForce(jump.add(dir ? farRight : farLeft));
					lastJumped = p.millis();
					grounded = false;
				}
			}
		}
		
		// backward jump (like backflip in Worms)
		if (p.checkKey(PConstants.BACKSPACE))
		{
			if(grounded == true)
			{
				if(p.millis() > lastJumped + 1000)
				{
					applyForce(highJump.add(dir ? left : right));
					lastJumped = p.millis();
					grounded = false;
				}
			}
		}
		
		if (showInventory)
		{
			p.fill(0);
			p.stroke(225);
			p.strokeWeight(1);
			p.rectMode(PConstants.CORNER);
			
			p.rect(p.width - inventoryDimensions.x, p.height - inventoryDimensions.y , inventoryDimensions.x, inventoryDimensions.y);
		}
	}

}
