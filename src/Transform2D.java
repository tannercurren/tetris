
public class Transform2D {
	private double [][] m;  //transformation matrix
	
	/**
	 * Initialize to the Identity transformation.
	 */
	public Transform2D() {
		//start with the identity
		m = new double [3][3];
		for(int i=0; i<3; i++) 
			m[i][i] = 1.0;
	}
	
	
	/**
	 * Multiply our transformation matrix by the transformation matrix in t.
	 * @param t
	 */
	public void addTransform(Transform2D t) {
		m = matrixMultiply(m, t.m);
	}
	
	
	/**
	 * Factory function which returns a translation transformation.
	 * @param x
	 * @param y
	 * @return
	 */
	public static Transform2D translate(double x, double y) {
		Transform2D t = new Transform2D();
		
		t.m[0][2] = x;
		t.m[1][2] = y;
		
		return t; 
	}
	
	
	
	/**
	 * Factory function which returns a scale transformation.
	 * @param w
	 * @param h
	 * @return
	 */
	public static Transform2D scale(double w, double h) {
		Transform2D t = new Transform2D();
		
		t.m[0][0] = w;
		t.m[1][1] = h;
		
		return t;
	}
	
	
	/**
	 * Factory function which returns a rotate transformation.
	 * @param angle - angle of the rotation in radians
	 * @return
	 */
	public static Transform2D rotate(double angle) {
		Transform2D t = new Transform2D();
		double sa = Math.sin(angle);
		double ca = Math.cos(angle);
		
		t.m[0][0] = ca;
		t.m[0][1] = sa;
		t.m[1][0] = -sa;
		t.m[1][1] = ca;
		
		return t;
	}
	
	
	/**
	 * Factory function which returns an xshear transformation.
	 * @param a  - Shearing factor
	 * @return
	 */
	public static Transform2D xShear(double a) {
		Transform2D t = new Transform2D();
		
		t.m[0][1] = a;
		
		return t;
	}
	
	
	/**
	 * Factory function which returns a yshear transformation.
	 * @param b - Shearing factor
	 * @return
	 */
	public static Transform2D yShear(double b) {
		Transform2D t = new Transform2D();
		
		t.m[1][0] = b;
		
		return t;
	}
	
	
	/**
	 * Factory function which returns a reflection about the origin.
	 * @return
	 */
	public static Transform2D oReflect() {
		Transform2D t = new Transform2D();
		
		t.m[0][0] = -1;
		t.m[1][1] = -1;
		
		return t;
	}
	
	
	/**
	 * Factory function which returns a reflection about the x axis
	 * @return
	 */
	public static Transform2D xReflect() {
		Transform2D t = new Transform2D();
		
		t.m[1][1] = -1;
		
		return t;
	}
	
	
	/**
	 * Factory function which returns a reflection about the y axis
	 * @return
	 */
	public static Transform2D yReflect() {
		Transform2D t = new Transform2D();
		
		t.m[0][0] = -1;
		
		return t;
	}
	
	
	
	/**
	 * Applies the transformation to a given point.
	 * @param x 
	 * @param y
	 * @return an array where return[0] = x and return[1] = y
	 */
	public double [] apply(double x, double y) {
		double [] result = new double[2];
		
		//build and return the result
		result[0] = m[0][0] * x + m[0][1] * y + m[0][2];
		result[1] = m[1][0] * x + m[1][1] * y + m[1][2];
		return result;
	}
	
	
	private double [][] matrixMultiply(double [][] a, double[][] b) {
		double [][] c;
		int m;
		int n;
		int p;
		
		n = a.length;  //number of rows in a
		m = a[0].length;  //number of columns in a
		p = b[0].length;  //cols in b
		
		//allocate the result
		c = new double[n][p];
		
		//do the multiplication in a naive way
		for(int i=0; i<n; i++) {
			for(int j=0; j<p; j++) {
				for(int k=0; k<m; k++) {
					c[i][j] += a[i][k] * b[k][j];
				}
			}
		}
		
		return c;
	}
}
