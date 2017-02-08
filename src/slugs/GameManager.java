package slugs;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import org.jbox2d.common.Vec2;

import processing.core.PConstants;

public class GameManager 
{
	Slugs p;
	PauseMenu pauseMenu;
	ArrayList<Player> players;
	private int turnTime;
	private int turnStart;
	private int current;
	private int healthMem;
	Vec2 timerPos;
	
	
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
			location = new Vec2(p.width/2, p.height/2);
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
			p.fill(0);
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
			p.rectMode(PConstants.CENTER);
			p.stroke(255);
			p.strokeWeight(2);
			p.noStroke();
			// black background
			p.rect(location.x, location.y, dimensions.x, dimensions.y);
			
			p.stroke(255);
			p.strokeWeight(2);
			if(dimensions.x >= widthMem)
			{	
				// do this if menu dimensions get incremented beyond full size
				dimensions.x = widthMem;
				dimensions.y = heightMem;
				
				p.rectMode(PConstants.CORNER);
				p.textSize(12);
				p.textAlign(PConstants.CENTER, PConstants.CENTER);
				
				for (Rectangle r: menuButtons.keySet())
				{
					if(p.mouseX > r.x && p.mouseX < r.x + r.width && p.mouseY > r.y && p.mouseY < r.y + r.height)
					{
						p.stroke(127, 176, 255);
						p.strokeWeight(3);
					}
					else
					{
						p.stroke(255);
						p.strokeWeight(1);
					}	
					
					p.noFill();
					p.rect(r.x, r.y, r.width -1, r.height -1);			
					p.fill(255);
					p.text(menuButtons.get(r), r.x + r.width/2, r.y + r.height/2);
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
					if(r.contains(p.mouseX, p.mouseY))
					{
						switch (menuButtons.get(r))
						{
							case "Quit":
								p.setup();
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
	
	public GameManager(Slugs p, ArrayList<Player> players, int turnLength) 
	{
		this.p = p;
		pauseMenu = new PauseMenu(250, 150);
		this.players = players;
		turnStart = p.millis();
		this.timerPos = new Vec2(45, p.height - 35);
		this.turnTime = turnLength;
		current = -1;
		nextTurn();
	}
	
	public void step()
	{
		pauseMenu.display();
		showPlayerStats();
		
		if (timer() == 0 || players.get(current).usedItem() || players.get(current).health < healthMem)
		{
			players.get(current).stop();
			players.get(current).currentItem = null;
			for (int i = 0; i < players.size(); i++)
			{
				if (players.get(i).health == 0)
				{
					players.remove(i);
				}
			}
			
			if (players.size() <= 1)
			{
				
				p.gameState = 2;
			}
			else
			{
				nextTurn();
			}
		}
		else
		{
			updatePlayer();
		}
	}
	
	private void showPlayerStats()
	{
		float x = p.width/2 - 100;
		float y = p.height - (players.size() * 30);
		for (Player player: players)
		{
			// heath bar
			p.fill(0);
			p.stroke(255);
			p.strokeWeight(1);
			p.rectMode(PConstants.CENTER);
			p.rect(p.width/2, y, 325, 25);
			p.fill(255);
			p.textSize(12);
			p.textAlign(PConstants.BOTTOM, PConstants.RIGHT);
			p.text(player.name, x - 50, y + 5);
			p.noFill();
			p.strokeWeight(4);
			p.stroke(0, 200, 0);
			p.line(x, y, x + player.health, y);
			y += 30;	
		}
	}
	
	// shows timer, and returns time left in go
	private int timer()
	{
		int t = turnTime - (p.millis() - turnStart)/1000;
		if (t < 0)
		{
			t = 0;
		}
		p.fill(0);
		p.stroke(255);
		p.rectMode(PConstants.CENTER);
		p.rect(timerPos.x, timerPos.y, 60, 40);
		p.fill(255, 0, 0);
		p.textSize(28);
		p.textAlign(PConstants.CENTER, PConstants.CENTER);
		p.text(t, timerPos.x, timerPos.y - 5);
		return t;
	}
	
	private void nextTurn()
	{
		current = (current + 1) % players.size();
		healthMem = players.get(current).health;
		turnStart = p.millis();
	}
	
	public boolean paused() 
	{
		return pauseMenu.show;
	}

	public void pause() {
		pauseMenu.toggle();
	}
	
	// check for key presses and apply them to the currently active player
	public void updatePlayer()
	{
		if (p.checkKey(' '))
		{
			players.get(current).useItem();
		}
		
		if (p.checkKey(PConstants.LEFT))
		{
			players.get(current).goLeft();
		}
		else if (p.checkKey(PConstants.RIGHT))
		{
			players.get(current).goRight();
		}
		else
		{
			players.get(current).stop();
		}
		
		//forward jump
		if (p.checkKey(PConstants.ENTER))
		{
			players.get(current).jumpForward();
		}
		
		// backward jump (like backflip in Worms)
		if (p.checkKey(PConstants.BACKSPACE))
		{
			players.get(current).jumpBack();
		}
		
		if (p.checkKey(PConstants.UP))
		{
			players.get(current).pressUp();
		}
		
		if (p.checkKey(PConstants.DOWN))
		{
			players.get(current).pressDown();
		}
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) 
	{
		if (!paused())
		{
			if (mouseButton == PConstants.LEFT)
			{
				players.get(current).itemMenu.click();
			}
			
			if (mouseButton == PConstants.RIGHT)
			{
				players.get(current).itemMenu.toggle();
			}
		}
		else
		{
			if (mouseButton == PConstants.LEFT)
			{
				pauseMenu.click();
			}
		}
	}

	public String getResult() {
		String msg= "";
		if (players.size() == 1)
		{
			msg = players.get(0).name + " wins!";
		}
		else
		{
			msg = "Its a draw!";
		}
		return msg;
	}
}
