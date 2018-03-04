import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.*;

public class Tetris extends JFrame implements KeyListener {
	// Tetris components
	private int grid[][], squarePixels[];
	private int lines, level, score;
	private char status;
	private long startTime, endTime;
	private Piece piece;
	AnimatedCanvas scene;
	
	// Graphics components
	private BufferedImage frame;
	private int width, height;
	
	
	public Tetris() {
		setFocusable(true);
		addKeyListener(this);
		
		// Start at 0 lines at level 1; every 10 lines is a level, up to 10
		lines = 0;
		level = 1;
		score = 0;
		
		setSize(340, 740);
		
		// Set up the grid (kinda messed up x & y here but whatever, it works)
		grid = new int[10][22];
		
		// Set status; 's' for startup, 'p' for playing, 'l' for lost
		status = 'p';
		
		//create a canvas with a square
		scene = new AnimatedCanvas();
		
		scene.add(new Score(this));
		
		add(scene);
	}
	
	/**
	 * A tetris piece which is given a random type upon substantiation and
	 * can then be rotated.
	 */
	public class Piece {
		public int type;
		public int[] xLoc, yLoc;
		public int rot_pos;
		public Point origin;
		
		public Piece() {
			// Randomly generate our piece's type
			Random randomGenerator = new Random();
			type = randomGenerator.nextInt(7);
			
			// 0 rot_pos for default; adds 1%4 for each rotation
			rot_pos = 0;
			
			// Assign the piece its grid positions
			xLoc = new int[4];
			yLoc = new int[4];
			
			for (int i = 0; i < 4; i++) {
				xLoc[i] = 0;
				yLoc[i] = 0;
			}
			
			if (type == 0) {
				// O spawns
				yLoc[0] = 0;
				yLoc[1] = 0;
				yLoc[2] = 1;
				yLoc[3] = 1;
				xLoc[0] = 4;
				xLoc[1] = 5;
				xLoc[2] = 4;
				xLoc[3] = 5;
				origin = new Point(5, 0);
			} else if (type == 1) {
				// I spawns
				yLoc[0] = 0;
				yLoc[1] = 0;
				yLoc[2] = 0;
				yLoc[3] = 0;
				xLoc[0] = 3;
				xLoc[1] = 4;
				xLoc[2] = 5;
				xLoc[3] = 6;
				origin = new Point(5, 0);
			} else if (type == 2) {
				// T spawns
				yLoc[0] = 0;
				yLoc[1] = 1;
				yLoc[2] = 1;
				yLoc[3] = 1;
				xLoc[0] = 4;
				xLoc[1] = 3;
				xLoc[2] = 4;
				xLoc[3] = 5;
				origin = new Point(4, 1);
			} else if (type == 3) {
				// S spawns
				yLoc[0] = 0;
				yLoc[1] = 0;
				yLoc[2] = 1;
				yLoc[3] = 1;
				xLoc[0] = 4;
				xLoc[1] = 5;
				xLoc[2] = 3;
				xLoc[3] = 4;
				origin = new  Point(4, 0);
			} else if (type == 4) {
				// Z spawns
				yLoc[0] = 0;
				yLoc[1] = 0;
				yLoc[2] = 1;
				yLoc[3] = 1;
				xLoc[0] = 3;
				xLoc[1] = 4;
				xLoc[2] = 4;
				xLoc[3] = 5;
				origin = new Point(4, 1);
			} else if (type == 5) {
				// J spawns
				yLoc[0] = 0;
				yLoc[1] = 1;
				yLoc[2] = 1;
				yLoc[3] = 1;
				xLoc[0] = 3;
				xLoc[1] = 3;
				xLoc[2] = 4;
				xLoc[3] = 5;
				origin = new Point(4, 1);
			} else if (type == 6) {
				// L spawns
				yLoc[0] = 0;
				yLoc[1] = 1;
				yLoc[2] = 1;
				yLoc[3] = 1;
				xLoc[0] = 5;
				xLoc[1] = 3;
				xLoc[2] = 4;
				xLoc[3] = 5;
				origin = new Point(4, 1);
			}
			// TODO: Draw
		}
		
		/*
		 * Move the piece left if possible
		 */
		public void left() {
			boolean possible = true;
			// Check if we can move it (doesn't hit a wall or block)
			for (int i = 0; i < 4; i++) {
				if (xLoc[i] - 1 < 0 || xLoc[i] - 1 >= 10) {
					possible = false;
				}
				// Only check for block if the space exists!
				if (possible) {
					if (grid[xLoc[i] - 1][yLoc[i]] != 0) {
						possible = false;
					}
				}
			}
			if (possible) {
				origin.x -= 1;
				for (int i = 0; i < 4; i++) {
					xLoc[i] -= 1;
				}
				
				// TODO: Draw
				printGrid(piece);
			}
		}
		
		/*
		 * Move the piece right if possible
		 */
		public void right() {
			boolean possible = true;
			// Check if we can move it (doesn't hit a wall or block)
			for (int i = 0; i < 4; i++) {
				if (xLoc[i] + 1 < 0 || xLoc[i] + 1 >= 10) {
					possible = false;
				}
				// Only check for block if the space exists!
				if (possible) {
					if (grid[xLoc[i] + 1][yLoc[i]] != 0) {
						possible = false;
					}
				}
			}
			if (possible) {
				origin.x += 1;
				for (int i = 0; i < 4; i++) {
					xLoc[i] += 1;
				}
				
				// TODO: Draw
				printGrid(piece);
			}
		}
		
		/*
		 * Move the piece down
		 */
		public void down() {
			// Check that there are no boundaries/blocks below
			boolean possible = true;
			for (int i = 0; i < 4; i++) {
				if (piece.yLoc[i] + 1 >= 22 || grid[piece.xLoc[i]][piece.yLoc[i] + 1] != 0) {
					possible = false;
				}
			}
			
			// If we can't move down, just return
			if (!possible)
				return;
			
			// Move the block down
			for (int i = 0; i < 4; i++) {
				yLoc[i] += 1;
			}
			origin.y += 1;
			printGrid(piece);
		}
		
		/*
		 * Rotate based on the piece's current orientation using matrix operations
		 */
		public void rotate() {
			if (type == 0) {
				// O blocks can't rotate, silly
			} else {
				// Save old values in case the rotation violates boundaries
				boolean working = true;
				int[] oldX = new int[4];
				int[] oldY = new int[4];
				for (int i = 0; i < 4; i++) {
					oldX[i] = xLoc[i];
					oldY[i] = yLoc[i];
				}
				
				// Begin rotation
				for (int i = 0; i < 4; i++) {
					// Translate shape so that origin is at (0, 0)
					xLoc[i] -= origin.x;
					yLoc[i] -= origin.y;
					
					// Multiply all pts by -1 to deal with Java coordinates
					xLoc[i] *= -1;
					yLoc[i] *= -1;
					
					// Rotate via matrix transform
					int newX = (int) Math.round(xLoc[i] * Math.cos(Math.PI / 2) - 
							yLoc[i] * Math.sin(Math.PI / 2));
					int newY = (int) Math.round(xLoc[i] * Math.sin(Math.PI / 2) +
							yLoc[i] * Math.cos(Math.PI / 2));
					
					// Ensure that we would not take up an occupied square
					// or go out of bounds
					if (newX * -1 + origin.x < 10 && newY * -1 + origin.y < 22 &&
							newX * -1 + origin.x >= 0 && newY * -1 + origin.y >= 0 &&
							grid[newX * -1 + origin.x][newY * -1 + origin.y] == 0) {
						xLoc[i] = newX;
						yLoc[i] = newY;
					} else {
						// Rotation violates boundaries
						working = false;
						break;
					}
					
					// Multiply by -1 again to return to original orientation
					xLoc[i] *= -1;
					yLoc[i] *= -1;
					
					// Re-translate back to our original location
					xLoc[i] += origin.x;
					yLoc[i] += origin.y;
				}
				// Ditch the rotation if it doesn't work
				if (!working) {
					for (int i = 0; i < 4; i++) {
						xLoc[i] = oldX[i];
						yLoc[i] = oldY[i];
					}
				} else {
					// TODO: Draw
					printGrid(piece);
				}
			}
		}
	}
		
	
	/*
	 * Iterate through the "life cycle" of a single block until it is placed
	 */
	public void iterate() throws InterruptedException {
		boolean place = false;
		
		// Spawn a block
		piece = new Piece();
		
		printGrid(piece);
		
		// Check if player has lost
		for (int i = 0; i < 4; i++) {
			if (grid[piece.xLoc[i]][piece.yLoc[i]] != 0) {
				status = 'l';
				return;
			}
		}
		
		//long start = System.nanoTime();
		//for (long curr = System.nanoTime(); !place; curr = System.nanoTime()) {
		
		while (true) {
			Thread.sleep((long) (1000*((11 - level) * 0.05)));
			// System.out.println("Current: " + curr);
			// System.out.println("Start: " + start);
			// System.out.println("Difference: " + (curr - start));

			// First, check for no room below
			for (int i = 0; i < 4; i++) {
				if (piece.yLoc[i] + 1 >= 22 || grid[piece.xLoc[i]][piece.yLoc[i] + 1] != 0) {
					place = true;
				}
			}

			// If the block needs placement, put it in the grid;
			// otherwise, move down
			if (place) {
				for (int i = 0; i < 4; i++) {
					grid[piece.xLoc[i]][piece.yLoc[i]] = 1;
				}
				int erasedLines = 0;
				// Check if any rows need to be erased
				for (int j = 21; j >= 0; j--) {
					boolean erase = true;
					for (int i = 0; i < 10; i++) {
						if (grid[i][j] == 0)
							erase = false;
					}
					// Move all above rows down if so
					if (erase == true) {
						lines++;
						erasedLines++;
						if (lines > 9 && lines <= 100) {
							level = lines / 10;
						}

						for (int k = j; k > 0; k--) {
							for (int i = 0; i < 10; i++) {
								grid[i][k] = grid[i][k - 1];
							}
						}
						// Blank top row
						for (int i = 0; i < 10; i++) {
							grid[i][0] = 0;
						}
						j++;
					}
				}
				if (erasedLines == 1) {
					score += 40 * (level + 1);
				} else if (erasedLines == 2) {
					score += 100 * (level + 1);
				} else if (erasedLines == 3) {
					score += 300 * (level + 1);
				} else if (erasedLines == 4) {
					score += 1200 * (level + 1);
				}
				printGrid(piece);
				break;
			} else {
				piece.down();
			}
			// }
		}

		// Update timer
		// start = System.nanoTime();
	}
	//}

	/*
	 * Debugging function to print the grid into the console w/ blocks
	 */
	public void printGrid(Piece piece) {
		// Remove other pieces from the board. Realistically, we really
		// should not be doing this. A better solution would be to just
		// transform our Blocks at each movement, but this is only a grid-based
		// game so it's not that important. This is just a recognition of a better
		// solution that exists.
		scene.clearObjectList();
		
		Point p0 = new Point(piece.xLoc[0], piece.yLoc[0]);
		Point p1 = new Point(piece.xLoc[1], piece.yLoc[1]);
		Point p2 = new Point(piece.xLoc[2], piece.yLoc[2]);
		Point p3 = new Point(piece.xLoc[3], piece.yLoc[3]);
		
		for (int j = 0; j < 22; j++) {
			for (int i = 0; i < 10; i++) {
				if ((i == p0.x && j == p0.y) || (i == p1.x && j == p1.y) ||
						(i == p2.x && j == p2.y) || (i == p3.x && j == p3.y)) {
					//System.out.print(1);
					Block b = new Block(i, j);
					scene.add(b);
				} else {
					//System.out.print(grid[i][j]);
					if (grid[i][j] == 1) {
						Block b = new Block(i, j);
						scene.add(b);
					}
				}
			}
			//System.out.print("\n");
		}
		//System.out.print("\n");

		// Draw score
		scene.add(new Score(this));
	}
	
	/*
	 * Run the game; stop if the player has lost
	 */
	public void runGame() throws InterruptedException {
		while (true) {
			if (status == 'p') {
				iterate();
			}
		}
	}
	
	/*
	 * Lines accessor
	 */
	public int getLines() {
		return lines;
	}
	
	/*
	 * Level accessor
	 */
	public int getLevel() {
		return level;
	}
	
	/*
	 * Score accessor
	 */
	public int getScore() {
		return score;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//System.out.println("KEY PRESSED");
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			if (piece != null) piece.left();
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (piece != null) piece.right();
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			if (piece != null) piece.down();
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (piece != null) piece.rotate();
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			if (piece != null)
				for (int i = 0; i < 22; i++) {
					piece.down();
					score++;
				}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Nothing
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Nothing
	}
	
	public static void main(String[] args) throws InterruptedException {
		Tetris window = new Tetris();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		
		window.runGame();
	}
}
