package ai.app.ui.imageeditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import ai.app.data.InputsOutputs;

public class AnswerDisplay extends JPanel implements MouseInputListener
{
	private static final long serialVersionUID = 6390005973576191206L;
	
	private float[] answers;
	
	public AnswerDisplay(int inputs)
	{
		this.answers = new float[inputs];
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		int x = 0;
		int y = 0;
		for (int i = 0; i < answers.length; i++)
		{
			g2d.setColor(new Color(0, answers[i], 0));
			g2d.fillRoundRect(x + 5, y + 20, 20, 20, 20, 20);
			g2d.setColor(getForeground());
			g2d.drawString(InputsOutputs.labels[i], x, y + 20);
			
			x += 30;
			if (x + 30 > getWidth())
			{
				x = 0;
				y += 40;
			}
		}
	}
	
	public float[] getAnswers()
	{
		float[] copy = new float[answers.length];
		for (int i = 0; i < copy.length; i++)
			copy[i] = answers[i];
		
		return copy;
	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{
		int b = getButtonAt(arg0.getX(), arg0.getY());
		
		if (b != -1)
		{
			answers[b] = 1 - answers[b];
			repaint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0)
	{
	}

	@Override
	public void mouseMoved(MouseEvent arg0)
	{
	}
	
	private int getButtonAt(int x, int y)
	{
		int bx = (x / 30);
		int by = (y / 40);
		int button = by * (getWidth() / 30) + bx;
		if (button >= answers.length)
			return -1;
		
		return button;
	}
}
