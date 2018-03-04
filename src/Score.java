import java.awt.*;
import java.awt.image.*;

public class Score implements Animatable {
	private BufferedImage img;
	private Tetris game;
	
	public Score(Tetris tetris) {
		this.game = tetris;
		
		img = new BufferedImage(500, 700, BufferedImage.TYPE_INT_ARGB); //outa be big enough
	}
	
	@Override
	public void draw(int[] pixels, int w) {
		int iw = img.getWidth();
		
		Graphics2D g = (Graphics2D) img.getGraphics();
		//black it out
		g.setColor(new Color(0xFF666666));
		g.fillRect(0,  0,  img.getWidth(), img.getHeight());
		//g.fillRect(10, 10, 40, 40);
		//set the color and font and write the string
		g.setColor(Color.BLACK);
		g.setFont(g.getFont().deriveFont(20f));   //make it sort of large-ish
		g.drawString("Lines: " + game.getLines(), 0, 673);
		g.drawString("Level: " + game.getLevel(), 95, 673);
		g.drawString("Score: " + game.getScore(), 190, 673);
		g.dispose();
		
		//get the pixels
		int [] imgPixels  = ((DataBufferInt) (img.getRaster().getDataBuffer())).getData();
		
		int j = w * 15 + 5;
		for(int i=0; i<imgPixels.length; i++) {
			if(imgPixels[i] != 0xFF666666)
				pixels[j] = imgPixels[i];
			
			if(i > 0 && i % iw == 0) {
				j += w-iw+1;
			} else {
				j++;
			}
		}
	}

	@Override
	public void update(AnimatedCanvas.FrameInfo frameInfo) {

	}

	@Override
	public void erase(int[] pixels, int w) {
		// TODO Auto-generated method stub
		
	}

}
