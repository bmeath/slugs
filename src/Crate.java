import java.awt.Rectangle;

public class Crate extends Entity
{
	public Crate(SlugsGame p5, float x, float y)
	{
		super(p5, x, y);
	}
	
	public boolean collisionCheck(Rectangle r)
	{
		// check if the shapes intersect
		return this.shape.getBounds().intersects(r);
	}
	
	public void display()
	{
		p5.stroke(0, 0, 0);
		p5.fill(247, 238, 158);
		p5.rect(centre.x, centre.y, 25, 25);
	}
}
