package ai.network.utils;

public class Matrix
{
	/**
	 * Multiplies both matrices and returns a third matrix as a result.<br>
	 * Matrices should be indexed by <code>matrix[row][column]</code>
	 * @param a first matrix
	 * @param b second matrix
	 * @return a*b
	 * @throws ArithmeticException if the matrices can't be multiplied due to their dimensions
	 */
	public static float[][] multiply(float[][] a, float[][] b) throws ArithmeticException
	{
		if (a.length == 0 || b.length != a[0].length)
			throw new ArithmeticException("First matrix's column count must match second matrix's row count!");
		
		float[][] ab = new float[a.length][b[0].length];
		for (int i = 0; i < ab.length; i++)
		{
			if (a[i].length != b.length)
				throw new ArithmeticException("Matrix must be rectangular!");
			
			for (int j = 0; j < ab[i].length; j++)
				for (int k = 0; k < a[0].length; k++)
					ab[i][j] += a[i][k] * b[k][j];
		}
		
		return ab;
	}
	
	/**
	 * Multiplies a vector by a matrix.<br>
	 * Matrices should be indexed by <code>matrix[row][column]</code>
	 * @param a vector
	 * @param b matrix
	 * @return a*b
	 * @throws ArithmeticException if the vector has a different length than the matrix's row count
	 */
	public static float[] multiply(float[] a, float[][] b) throws ArithmeticException
	{
		if (b.length == 0 || a.length != b.length)
			throw new ArithmeticException("Vectors length must be equal to matrix's row count!");
		
		for (int i = 1; i < b.length; i++)
			if (b[i].length != b[0].length)
				throw new ArithmeticException("Matrix must be rectangular!");
		
		float[] ab = new float[b[0].length];
		//Output will be an array of a dot column i of b
		
		for (int i = 0; i < ab.length; i++)
			for (int j = 0; j < a.length; j++)
				ab[i] += a[j] * b[j][i];
		
		return ab;
	}
	
	/**
	 * Multiplies a vector by a matrix.<br>
	 * Matrices should be indexed by <code>matrix[row][column]</code>
	 * @param a vector
	 * @param b matrix
	 * @return a*(b^T)
	 * @throws ArithmeticException if the vector has a different length than the matrix's row count
	 */
	public static float[] multiplyTranspose(float[][] b, float[] a) throws ArithmeticException
	{
		if (a.length == 0 || b.length == 0 || a.length != b[0].length)
			throw new ArithmeticException("Vectors length must be equal to matrix's row count!");
		
		for (int i = 1; i < b.length; i++)
			if (b[i].length != b[0].length)
				throw new ArithmeticException("Matrix must be rectangular!");
		
		float[] ab = new float[b.length];
		
		for (int i = 0; i < b.length; i++)
			for (int j = 0; j < a.length; j++)
				ab[i] += a[j] * b[i][j];
		
		return ab;
	}
}
