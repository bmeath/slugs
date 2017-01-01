import processing.core.*;

public class SlugsGame extends PApplet
{
	public static void main(String[] args)
	{
		PApplet.main("SlugsGame");
	}
	
	int gameState = 0;
	
	public void settings()
	{
		size(1280, 720);
	}
	
	public void setup()
	{
	}
	
	public void draw()
	{
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
		}
	}
	
	public void initScreen()
	{
		background(0);
		textAlign(CENTER);
		text("SLUGS", width/2, height/3);
		text("Click to begin", width/2, height/2);
	}
	
	public void gameScreen()
	{
		
	}
	
	public void endScreen()
	{
		
	}
	
	public void mousePressed()
	{
	  // if we are on the initial screen when clicked, start the game
	  if (gameState == 0)
	  {
		  gameScreen();
	  }
	}
}