package ai.network.utils;

public class Vector
{
	/**
	 * Adds vector <code>b</code> to vector <code>a</code> and returns <code>a</code>
	 * @param a vector which will be added to
	 * @param b vector which will add to
	 * @return a = a + b
	 * @throws ArithmeticException if the vectors aren't equal length
	 */
	public static float[] add(float[] a, float[] b) throws ArithmeticException
	{
		if (a.length != b.length)
			throw new ArithmeticException("Vectors must be of equal length to be added together!");
		
		for (int i = 0; i < b.length; i++)
			a[i] += b[i];
		
		return a;
	}
	
	/**
	 * Subtracts vector <code>b</code> from vector <code>a</code> and returns <code>a</code>
	 * @param a vector which will be subtracted from
	 * @param b vector which will subtract
	 * @return a = a - b
	 * @throws ArithmeticException if the vectors aren't equal length
	 */
	public static float[] subtract(float[] a, float[] b) throws ArithmeticException
	{
		if (a.length != b.length)
			throw new ArithmeticException("Vectors must be of equal length to be subtracted!");
		
		for (int i = 0; i < b.length; i++)
			a[i] -= b[i];
		
		return a;
	}
	
	public static float distance(float[] a)
	{
		float distance = 0;
		for (int i = 0; i < a.length; i++)
			distance += a[i] * a[i];
		
		return (float) Math.sqrt(distance);
	}
	
	/**
	 * Multiplies vector <code>b</code> with vector <code>a</code> and returns <code>a</code>
	 * @param a vector which will be multiplied
	 * @param b vector which will multiply
	 * @return a = a * b
	 * @throws ArithmeticException if the vectors aren't equal length
	 */
	public static float[] multiply(float[] a, float[] b) throws ArithmeticException
	{
		if (a.length != b.length)
			throw new ArithmeticException("Vectors must be of equal length to be multiplied!");
		
		for (int i = 0; i < b.length; i++)
			a[i] *= b[i];
		
		return a;
	}
	
	/**
	 * Multiplies vector <code>a</code> with vector <code>b</code> and returns a matrix
	 * @param a vector
	 * @param b vector
	 * @return column vector a * row vector b
	 */
	public static float[][] multiplyTranspose(float[] a, float[] b)
	{
		float[][] matrix = new float[b.length][a.length];
		
		for (int i = 0; i < b.length; i++)
			for (int j = 0; j < a.length; j++)
				matrix[i][j] = a[j] * b[i];
		
		return matrix;
	}
	
	private static int[] tens = new int[]{1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000};
	
	public static String toString(float[] vector, int places)
	{
		StringBuilder sb = new StringBuilder("{");
		
		for (int i = 0; i < vector.length; i++)
			sb.append((i == 0 ? "" : ", ") + ((float) Math.round(vector[i] * tens[places])) / tens[places]);
		
		sb.append("}");
		return sb.toString();
	}
}
