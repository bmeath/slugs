package slugs;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;

public class Player extends Entity
{
	float w;
	float h;
	
	public Player(Slugs p5, float x, float y)
	{
		super(p5, x, y, BodyType.KINEMATIC, true, 1, 0.3f, 0.5f);
		
		w = 40;
		h = 30;
		
		
		// upper body
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(p5.world.scalarPixelsToWorld(w/2), p5.world.scalarPixelsToWorld(h/2));
		
		// left eye
		
		
		// right eye
		
		
		//left stalk
		
		
		// right stalk
		
		
		// lower body front
		
		
		// lower body rear
		
	}
	
	protected void render()
	{
		
	}

}
