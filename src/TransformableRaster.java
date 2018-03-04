
import java.awt.Point;

public class TransformableRaster implements Animatable {
	protected int [] pixels;
	protected Transform2D transform;
	protected int height;
	protected int width;
	protected int dx, dy;
	
	/**
	 * Constructs a new raster, filled with blank pixels.
	 * @param w Width
	 * @param h Height
	 */
	public TransformableRaster(int w, int h) {
		//allocate our pixels
		pixels = new int[w*h];
		
		//instantiate our transformation
		transform = new Transform2D();
		
		//remember to set the width and height!
		width = w;
		height = h;
	}
	
	/**
	 * Constructor used by subclasses which want absolute control over the pixels.
	 */
	protected TransformableRaster() {
		//instantiate our transformation
		transform = new Transform2D();
	}
	
	
	/**
	 * This resets the transfomration matrix back to the identity matrix.
	 */
	public void resetTransform() {
		transform = new Transform2D();
	}
	
	
	/**
	 * Add t to the current transformation matrix
	 * @param t
	 */
	public void addTransform(Transform2D t) {
		transform.addTransform(t);
	}
	
	
	/**
	 * Replace the current transformation matrix with t
	 * @param t
	 */
	public void setTransform(Transform2D t) {
		transform = t;
	}
	
	
	/** 
	 * Returns a reference to the pixel array
	 * @return
	 */
	public int [] getPixels() {
		return pixels;
	}
	
	
	/**
	 * Get's the height of the raster
	 * @return
	 */
	public int getHeight(){
		return height;
	}
	
	/**
	 * Get's th ewidth of the raster
	 * @return
	 */
	public int getWidth() {
		return width;
	}
	
	
	public void setOffset(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	
	public Point getOffset() {
		Point p = new Point();
		p.setLocation(dx, dy);
		
		return p;
	}
	
	
	@Override
	public void draw(int[] pixels, int w) {
		int h = pixels.length/w;
		int i=0;
		double[]p;
		int px, py;
		
		for(int y=0; y<this.height; y++) {
			for(int x=0; x<this.width; x++, i++) {;
				p = transform.apply(x, y); 
				px = (int) Math.round(p[0]) + dx;
				py = (int) Math.round(p[1]) + dy;
				
				if(px >= 0 && px < w && py >= 0 && py < h) {
					pixels[py*w + px] = this.pixels[i];
					if(px + 1 < w && x+1 < this.width) {
						pixels[py*w + px + 1] = this.pixels[i];
					}
				}
			}
		} 
	}

	@Override
	public void erase(int[] pixels, int w) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(AnimatedCanvas.FrameInfo frameInfo) {
	}

}
