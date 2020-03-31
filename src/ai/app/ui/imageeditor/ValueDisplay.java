package ai.app.ui.imageeditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import ai.app.ui.network.NetworkDisplay;

public class ValueDisplay extends JPanel
{
	private static final long serialVersionUID = 9102766707186573185L;
	
	private int x;
	private int y;
	
	private Color[][] colors;
	
	private float[][] lastValues;

	public ValueDisplay(int x, int y)
	{
		setDisplaySize(x, y);
	}
	
	public void setDisplaySize(int x, int y)
	{
		if (x == 0)
			x = 1;
		if (y == 0)
			y = 1;
		
		this.x = x;
		this.y = y;
		
		colors = new Color[x][y];
		
		setSize(getWidth(), getHeight(getWidth()));
		
		setValues(lastValues);
	}
	
	public int getHeight(int width)
	{
		return width * y / x;
	}
	
	public void setValues(float[][] values)
	{
		if (values == null)
			return;
		lastValues = values;
		
		float[][] scaledValues = ImageUtils.rescale(values, x, y);
		for (int i = 0; i < x; i++)
			for (int j = 0; j < y; j++)
				colors[i][j] = NetworkDisplay.getValue(scaledValues[i][j]);
		
		repaint();
	}
	
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		float diameter = getWidth() / (float) x;
		
		for (int i = 0; i < x; i++)
		{
			for (int j = 0; j < y; j++)
			{
				g2d.setColor(colors[i][j]);
				g2d.fillRoundRect((int) (diameter * i), (int) (diameter * j), (int) diameter, (int) diameter, (int) diameter, (int) diameter);
			}
		}
	}
}
