package ai.app.ui.network;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import ai.app.WorkerThread;

public class NetworkDisplay extends JPanel
{
	private static final long serialVersionUID = -7059771846490278124L;
	
	private WorkerThread repainter = new WorkerThread();
	
	private int[] dimensions;
	private Color[][][] weights;
	private Color[][] value;
	
	private int largestRow;

	public NetworkDisplay(int[] dimensions)
	{
		this.dimensions = dimensions;
		
		for (int i = 0; i < dimensions.length; i++)
			if (dimensions[i] > largestRow)
				largestRow = dimensions[i];
		
		weights = new Color[dimensions.length - 1][][];
		value = new Color[dimensions.length][];
		
		Color black = new Color(0, 0, 0);
		
		for (int i = 0; i < weights.length; i++)
		{
			weights[i] = new Color[dimensions[i + 1]][dimensions[i]];
			for (int j = 0; j < weights[i].length; j++)
				for (int k = 0; k < weights[i][j].length; k++)
					this.weights[i][j][k] = black;
		}
		
		for (int i = 0; i < value.length; i++)
		{
			value[i] = new Color[dimensions[i]];
			for (int j = 0; j < value[i].length; j++)
				this.value[i][j] = black;
		}
		
		repainter.start();
		
		createCache(1, 1);
	}
	
	public void close()
	{
		repainter.close();
	}
	
	public void setWeights(float[][] weights, int i)
	{
		synchronized (repainter)
		{
			for (int j = 0; j < weights.length; j++)
				for (int k = 0; k < weights[j].length; k++)
					this.weights[i][j][k] = getWeight(weights[j][k]);
		}
	}
	
	public void setValue(float[] value, int i)
	{
		synchronized (repainter)
		{
			for (int j = 0; j < value.length; j++)
				this.value[i][j] = getValue(value[j]);
		}
	}
	
	public static Color getWeight(float weight)
	{
		return getColor(
				within(-weight, 0, 1),
				within(weight, 0, 1),
				within((Math.abs(weight) - 1) / 10, 0, 1),
				within(Math.abs(weight), 0, 1f)
		);
	}
	
	public static Color getValue(float value)
	{
		return getColor(
				within(-value, 0, 1),
				within(value, 0, 1),
				within((Math.abs(value) - 1) / 10, 0, 1),
				1
		);
	}
	
	private static float within(float value, float min, float max)
	{
		return Math.min(max, Math.max(min, value));
	}
	
	private static Color getColor(float r, float g, float b, float a)
	{
		return new Color((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255));
	}
	
	public void update()
	{
		repainter.giveTask(repaintCache);
		repaint();
	}
	
	Stroke b;
	
	private BufferedImage cache;
	private Graphics2D g2d;
	
	@Override
	public void paint(Graphics g)
	{
		if (!repainter.isWorking() && (cache.getWidth() != getWidth() || cache.getHeight() != getHeight()))
		{
			synchronized (repainter)
			{
				g2d.dispose();
				createCache(getWidth(), getHeight());
				update();
			}
		}
		
		g.drawImage(cache, 0, 0, getWidth(), getHeight(), null);
	}
	
	private Runnable repaintCache = new Runnable()
	{
		public void run()
		{
			boolean horizontal = (2 * dimensions.length - 1) * cache.getHeight()  > cache.getWidth() * largestRow;
			
			b = new BasicStroke((horizontal ? cache.getHeight() : cache.getWidth()) / (float) largestRow / 40f);
			
			g2d.setColor(getBackground());
			g2d.fillRect(0, 0, cache.getWidth(), cache.getHeight());
			
			g2d.setStroke(b);
			
			if (horizontal)
			{
				float layerWidth = cache.getWidth() / (float) dimensions.length;
				float neuronHeight = cache.getHeight() / (float) largestRow;
				
				for (int i = 0; i < dimensions.length; i++)
				{
					int x = (int) ((0.5f + i) * layerWidth);
					
					if (i > 0)
					{
						int prevX = (int) ((-0.5f + i) * layerWidth);
						
						for (int j = 0; j < dimensions[i]; j++)
						{
							int y = (int) (neuronHeight * ((largestRow + 1) / 2f + j - dimensions[i] / 2f));
							for (int k = 0; k < dimensions[i - 1]; k++)
							{
								int prevY = (int) (neuronHeight * ((largestRow + 1) / 2f + k - dimensions[i - 1] / 2f));
								g2d.setColor(weights[i - 1][j][k]);
								g2d.drawLine(prevX, prevY, x, y);
							}
							repaint();
						}
					}
					
					int radius = (int) Math.max(4, /*neuronHeight*/Math.min((cache.getHeight() / (float) dimensions[i]), layerWidth) / 2) - 3;
					int d = radius << 1;
					for (int j = 0; j < dimensions[i]; j++)
					{
						int y = (int) ((d + 6) * ((largestRow + 1) / 2f + j - dimensions[i] / 2f));

						g2d.setColor(value[i][j]);
						g2d.fillRoundRect(x - radius, y - radius, d, d, d, d);
						//g2d.setColor(value[i][j].brighter());
						//g2d.drawRoundRect(x - radius, y - radius, d, d, d, d);
					}
				}
			} else
			{
				float layerHeight = cache.getHeight() / (float) dimensions.length;
				float neuronWidth = cache.getWidth() / (float) largestRow;
				
				for (int i = 0; i < dimensions.length; i++)
				{
					int y = (int) ((0.5f + i) * layerHeight);
					
					if (i > 0)
					{
						int prevY = (int) ((-0.5f + i) * layerHeight);
						
						for (int j = 0; j < dimensions[i]; j++)
						{
							int x = (int) (neuronWidth * ((largestRow + 1) / 2f + j - dimensions[i] / 2f));
							for (int k = 0; k < dimensions[i - 1]; k++)
							{
								int prevX = (int) (neuronWidth * ((largestRow + 1) / 2f + k - dimensions[i - 1] / 2f));
								g2d.setColor(weights[i - 1][j][k]);
								g2d.drawLine(prevX, cache.getHeight() - prevY, x, getHeight() - y);
							}
							repaint();
						}
					}

					int radius = (int) Math.max(4, /*neuronWidth*/Math.min((cache.getWidth() / (float) dimensions[i]), layerHeight) / 2) - 3;
					int d = radius << 1;
					for (int j = 0; j < dimensions[i]; j++)
					{
						int x = (int) ((d + 6) * ((largestRow + 1) / 2f + j - dimensions[i] / 2f));

						g2d.setColor(value[i][j]);
						g2d.fillRoundRect(x - radius, getHeight() - y - radius, d, d, d, d);
						//g2d.setColor(getForeground());
						//g2d.drawRoundRect(x - radius, getHeight() - y - radius, d, d, d, d);
					}
				}
			}
			
			for (int j = 0; j < 30; j++)
			{
				for (int i = 0; i < 30; i++)
				{
					g2d.setColor(value[0][i + j * 30]);
					g2d.fillRoundRect(6 * i, 6 * j, 6, 6, 6, 6);
				}
			}
			
			repaint();
		}
	};
	
	private void createCache(int width, int height)
	{
		synchronized (repainter)
		{
			cache = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			g2d = cache.createGraphics();
			
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
	}
}
