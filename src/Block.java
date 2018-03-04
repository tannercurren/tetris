
public class Block extends TransformableRaster {
	
	public Block(int gridX, int gridY) {
		// Set up a square
		pixels = new int[900];
		for (int h = 0; h < 30; h++) {
			for (int w = 0; w < 30; w++) {
				if (h == 0 || h == 29 || w == 0 || w == 29) {
					pixels[h*30 + w] = 0xFFD1D1E0;
				} else {
					pixels[h*30 + w] = 0xFF54542B;
				}
			}
		}
		
		dx = 11 + 30 * gridX;
		dy = 5 + 30 * gridY;
		
		width = 30;
		height = 30;
	}
}
