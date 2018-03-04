import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public class AnimatedCanvas extends JPanel {
	/**
	 * List of objects in our animated canvas.
	 */
	private LinkedList<Animatable> objectList;
	
	/**
	 * Controls access to scene drawing
	 */
	Semaphore drawSem;
	
	
	/**
	 * Current Frame
	 */
	BufferedImage frame;
	
	/**
	 * Time statistics
	 */
	public class FrameInfo {
		/**
		 * Time since the start of the animation
		 */
		public long nanoElapsed;
		
		/**
		 * Nanosecond timestamp of the last frame.
		 */
		public long nanoLastFrame;
		
		/** 
		 * Frames per second
		 */
		public long fps;
		
		/** 
		 * Total number of frames in the animation
		 */
		public long frameCount;	
		
		/**
		 * Time since the last frame in nanoseconds
		 */
		public long nanoFrameElapsed;
	}
	
	private FrameInfo frameInfo;
	private long nanoStart;
	
	public AnimatedCanvas() {
		objectList = new LinkedList<Animatable>();
		frameInfo = new FrameInfo();
		
		//set up the Semaphore
		drawSem = new Semaphore(1);
		try {
			drawSem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//start our animation thread
		Thread t = new Thread(new AnimationThread());
		t.start();
		
		
	}
	
	
	public JFrame getWindow() {
		Component p = getParent();
		
		if(p == null) return null;
		
		//find the root 
		while(p.getParent() != null) p = p.getParent();
		
		return (JFrame) p;
	}
	
	
	/**
	 * Add an animatable object to the canvas
	 * @param o Object to add to the canvas
	 */
	public void add(Animatable o) {
		objectList.add(o);
	}
		
	private class AnimationThread implements Runnable {
		private JFrame window;
		private int width, height;
		private int ox, oy;
		
		@Override
		public void run() {
			long lastBatch;  //start time for the last batch to have fps
			
			//acquire the starting time
			nanoStart = System.nanoTime();
			lastBatch = nanoStart;
			
			while(true) {
				try {
					render();
				} catch(Exception ex) {
					
				}
				
				//recompute our statitistics
				long ft = System.nanoTime();
				frameInfo.nanoFrameElapsed = ft - frameInfo.nanoLastFrame;
				frameInfo.nanoLastFrame = ft;
				frameInfo.nanoElapsed = frameInfo.nanoLastFrame - nanoStart;
				
				frameInfo.frameCount++;
				
				//average the last 100 of these
				if(frameInfo.frameCount % 1000 == 0) {
					frameInfo.fps = (long)(100/((frameInfo.nanoLastFrame-lastBatch) / 1.0e9));
					lastBatch = frameInfo.nanoLastFrame;
				}
			}
		}
		
		
		public void render() {
			BufferStrategy bs=null;
	
			if(window == null) {
				window = getWindow();
				if(window == null) return;
			}
	
				
			bs = getWindow().getBufferStrategy();
			try {
				if(bs==null) {
					window.createBufferStrategy(3);
					return;
				}
			} catch(Exception ex) {
					
			}
			
			//get the width and height (if needed)
			if(width == 0 || height == 0) {
				width = getWidth();
				height = getHeight();
				
				//take a deep sigh, and find our origin
				Point l1 = getWindow().getLocationOnScreen();
				Point l2 = getLocationOnScreen();
				ox = (int) (l2.getX() - l1.getX());
				oy = (int) (l2.getY() - l1.getY());
				return;
			}

			if(frame == null)
				frame = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			// Set up frame	
			int [] pixels = ((DataBufferInt) (frame.getRaster().getDataBuffer())).getData();
			for(int i=0; i<pixels.length; i++) {
				pixels[i] = 0xFF666666;
			}
			
			for (int y = 2; y <= 22 * 30 + 7; y++) {
				for (int x = 8; x <= 313; x++) {
					if (y == 2 ||  y == 4 || y == 22 * 30 + 5 || y == 22 * 30 + 7
							|| x == 8 || x == 10 || x == 311 || x == 313 || y == 3
							|| y == 22 * 30 + 6 || x == 9 || x == 312) {
						pixels[y * width + x] = 0xFFD1D1E0;
					}
				}
			}
			
			Graphics2D g = (Graphics2D) frame.getGraphics();
			//update and draw the objects
			for(Animatable o : objectList) {
				o.erase(pixels, frame.getWidth());
				o.update(frameInfo);
				o.draw(pixels, frame.getWidth());
			}
			
			//do fps
			g.setColor(Color.yellow);
			g.drawString("FPS: "+frameInfo.fps, 20,20);
			g.dispose();
			
			//redraw the scene
			g = (Graphics2D) bs.getDrawGraphics();
			g.drawImage(frame, ox, oy, null);
			
			g.dispose();
			bs.show();
		}
	}
	
	// Accessor for Tetris
	public void clearObjectList() {
		objectList = new LinkedList<Animatable>();
	}
	
	// Accessor for Tetris
	public Graphics2D getFrameGraphics() {
		return (Graphics2D) frame.getGraphics();
	}
}
