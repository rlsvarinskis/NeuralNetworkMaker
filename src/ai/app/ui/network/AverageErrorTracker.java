package ai.app.ui.network;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

public class AverageErrorTracker extends JPanel implements MouseInputListener
{
	private static final long serialVersionUID = 8046746664082688076L;
	
	private ArrayList<Integer> dataAmount = new ArrayList<>();
	private ArrayList<Float> dataCost = new ArrayList<>();
	
	private int minAmount = 0;
	private float minCost = 0;
	private int maxAmount = 0;
	private float maxCost = 1;
	
	private int firstDataPoint = 0;
	
	private int fromAmount = 0;
	private float fromCost = 0;
	private int toAmount = 0;
	private float toCost = 1;
	
	private int amountDelta = 0;
	private float costDelta = 1;
	
	private int lastMouseX = 0;
	
	public AverageErrorTracker()
	{
		addMouseListener(this);
	}
	
	private BasicStroke b = new BasicStroke(2);
	
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int x = 40;
		int y = 20;
		int height = getHeight() - 40;
		int width = getWidth() - x - 20;
		
		int maxWidth = 0;
		
		g2d.setColor(getForeground());
		g2d.setFont(getFont());
		
		synchronized (dataAmount)
		{
			for (int i = 0; i <= 20; i++)
			{
				String str = (Math.round((minCost + costDelta * i / 20) * 1000) / 1000f) + "";
				maxWidth = Math.max(maxWidth, g2d.getFontMetrics().stringWidth(str) + 6);
				g2d.drawString(str, 3, y + (height - (i * height / 20)));
				g2d.drawLine(x, y + height - (i * height / 20), x + width, y + height - (i * height / 20));
			}
			
			x = maxWidth;
			width = getWidth() - x - 20;
			
			g2d.setColor(getBackground().brighter());
			g2d.fillRect(x, y, width, height);
			g2d.setColor(getForeground());
			g2d.drawRect(x, y, width, height);
			
			for (int i = 0; i <= 20; i++)
				g2d.drawLine(x, y + height - (i * height / 20), x + width, y + height - (i * height / 20));
			
			int width60 = width / 60;
			
			for (int i = 0; i <= width60; i++)
				g2d.drawString(Integer.toString((i * amountDelta) + fromAmount), x + i * 60, y + height + 10);
			
			g2d.setStroke(b);
			g2d.clipRect(x, y, width, height);
			
			for (int i = Math.max(1, dataAmount.size() - 20); i < dataAmount.size(); i++)
			{
				int lastAmount = x + (dataAmount.get(i - 1) - fromAmount) * width / amountDelta;
				int lastCost = y + (int) (height - (dataCost.get(i - 1) - minCost) * height / costDelta);
				
				int amount = x + (dataAmount.get(i) - fromAmount) * width / amountDelta;
				int cost = y + (int) (height - (dataCost.get(i) - minCost) * height / costDelta);
				
				g2d.drawLine(lastAmount, lastCost, amount, cost);
				g2d.fillRect(amount - 1, cost - 1, 3, 3);
			}
		}
		g2d.setClip(0, 0, getWidth(), getHeight());
	}
	
	public int getMaxAmount()
	{
		return maxAmount;
	}
	
	public void addPoint(int amount, float error)
	{
		synchronized (dataAmount)
		{
			dataAmount.add(amount);
			dataCost.add(error);
			
			maxCost = Math.max(maxCost, error);
			minCost = Math.min(minCost, error);
			
			maxAmount = Math.max(maxAmount, amount);
			minAmount = Math.min(minAmount, amount);
			
			amountDelta = maxAmount - minAmount;
			costDelta = maxCost - minCost;
			
			if (dataAmount.size() > 20)
				fromAmount = dataAmount.get(dataAmount.size() - 20);
			else
				fromAmount = dataAmount.get(0);
			
			amountDelta = maxAmount - fromAmount;
		}
		
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		lastMouseX = e.getX();
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		lastMouseX = -1;
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		lastMouseX = e.getX();
	}
}
