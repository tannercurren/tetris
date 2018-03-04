import java.awt.Graphics2D;

public interface Animatable {
	public void draw(int [] pixels, int w);
	public void erase(int [] pixels, int w);
	public void update(AnimatedCanvas.FrameInfo frameInfo);
}
