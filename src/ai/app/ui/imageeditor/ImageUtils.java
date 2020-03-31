package ai.app.ui.imageeditor;

public class ImageUtils
{
	static void blur()
	{
		
	}
	
	public static float[][] rescale(float[][] src, int x, int y)
	{
		float[][] dst = new float[x][y];
		
		for (int i = 0; i < src.length; i++)
		{
			for (int j = 0; j < src[i].length; j++)
			{
				int dstXFrom = i * x / src.length;
				int dstXTo = ((i + 1) * x + src.length - 1) / src.length;
				
				int dstYFrom = j * y / src[i].length;
				int dstYTo = ((j + 1) * y + src[i].length - 1) / src[i].length;
				
				float startX = (float) i * (float) x / (float) src.length;
				float endX = (i + 1f) * (float) x / (float) src.length;
				
				float startY = (float) j * (float) y / (float) src[i].length;
				float endY = (float) (j + 1) * (float) y / (float) src[i].length;
				
				for (int xc = dstXFrom; xc < dstXTo; xc++)
				{
					for (int yc = dstYFrom; yc < dstYTo; yc++)
					{
						dst[xc][yc] += src[i][j] * (Math.min(endX, xc + 1) - Math.max(startX, xc)) * (Math.min(endY, yc + 1) - Math.max(startY, yc));
					}
				}
			}
		}
		
		return dst;
	}
}
