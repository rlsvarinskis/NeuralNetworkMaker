package ai.app.ui.imageeditor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.MouseInputListener;

public class ImageEditor extends JPanel implements MouseInputListener, MouseWheelListener, KeyListener
{
	private static final long serialVersionUID = -3155805393690743093L;
	
	private int selectedX = 0;
	private int selectedY = 0;
	private int selectedW = 30;
	private int selectedH = 30;
	
	private int lastCursor = Cursor.DEFAULT_CURSOR;
	private boolean isDragging = false;
	private boolean needsRepaint = false;
	
	private boolean isCTRLDown = false;
	
	private float zoom = 1;
	
	private int[] src;
	private int[] blurred;
	private int[] edited;
	private BufferedImage editedImage;

	private int[] red;
	private int[] green;
	private int[] blue;
	private int[] alpha;
	
	private Color yellow = new Color(255, 255, 0, 127);
	private Color highlight = new Color(255, 255, 255, 127);
	
	private JScrollPane parent;
	
	private int width = 30;
	private int height = 30;
	
	private int radius;
	private float threshold = 1;
	private int startRGB = 0;
	private int endRGB = 255;
	
	private boolean[][] mask;
	
	private boolean alterImage = false;
	
	public ImageEditor(JScrollPane parent)
	{
		addMouseWheelListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		this.parent = parent;
		checkSize();
		
		setRadius(5);
	}
	
	public void setImage(BufferedImage img)
	{
		src = new int[img.getWidth() * img.getHeight()];

		img.getRGB(0, 0, img.getWidth(), img.getHeight(), src, 0, img.getWidth());

		red = new int[src.length];
		green = new int[src.length];
		blue = new int[src.length];
		alpha = new int[src.length];
		
		for (int i = 0; i < src.length; i++)
		{
			int col = src[i];
			alpha[i] = (col >> 24) & 0xFF;
			red[i] = (col >> 16) & 0xFF;
			green[i] = (col >> 8) & 0xFF;
			blue[i] = (col) & 0xFF;
		}
		
		blurred = new int[src.length];
		edited = new int[src.length];
		
		editedImage = wrap(edited, img.getWidth(), img.getHeight());
		width = img.getWidth();
		height = img.getHeight();
		
		updateBlurredImage();
		
		zoom = 1;
		checkSize();
		updateCursor();
		repaint();
		
		fireOnChange();
	}
	
	public boolean toggleAlterImage()
	{
		boolean ret = alterImage = !alterImage;
		updateEditedImage();
		return ret;
	}
	
	public void setRadius(int r)
	{
		radius = r;
		
		mask = new boolean[2 * radius + 1][2 * radius + 1];
		
		for (int i = 0; i < mask.length; i++)
		{
			for (int j = 0; j < mask[i].length; j++)
			{
				int xd = mask.length / 2 - i;
				int yd = mask.length / 2 - j;
				if (xd * xd + yd * yd <= radius * radius)
					mask[i][j] = true;
				
				//if (xd == 0 && yd == 0)
				//	mask[i][j] = false;
			}
		}
		
		updateBlurredImage();
	}
	
	public void setThreshold(float t)
	{
		threshold = t;
		updateBlurredImage();
	}
	
	public void setStartRGB(int startRGB)
	{
		this.startRGB = startRGB;
		updateEditedImage();
	}
	
	public void setEndRGB(int endRGB)
	{
		this.endRGB = endRGB;
		updateEditedImage();
	}
	
	private void updateBlurredImage()
	{
		if (blurred == null)
			return;
		
		if (alterImage)
		{
			int w = editedImage.getWidth();
			
			for (int i = 0; i < editedImage.getWidth(); i++)
			{
				for (int j = 0; j < editedImage.getHeight(); j++)
				{
					int pos = i + j * w;

					int a = 0;
					int r = 0;
					int g = 0;
					int b = 0;
					
					int sum = red[pos] + green[pos] + blue[pos] + alpha[pos];
					
					float totalThreshold = 255 * 4 * threshold + sum;
					
					int total = 0;
					
					int startx = Math.max(radius - Math.min(i, radius), 0);
					int starty = Math.max(radius - Math.min(j, radius), 0);
					int endx = Math.min(w + radius - i, mask.length);
					int endy = Math.min(editedImage.getHeight() + radius - j, mask.length);
					
					for (int x = startx; x < endx; x++)
					{
						int posx = i - radius + x;
						
						for (int y = starty; y < endy; y++)
						{
							if (mask[x][y])
							{
								int posy = j - radius + y;
								int posn = posx + posy * w;
								
								int al = alpha[posn];
								int re = red[posn];
								int gr = green[posn];
								int bl = blue[posn];
								
								if (al + re + gr + bl <= totalThreshold)
								{
									total++;
									a += al;
									r += re;
									g += gr;
									b += bl;
								}
							}
						}
					}
					
					a /= total;
					r /= total;
					g /= total;
					b /= total;

					blurred[pos] = (a << 24) | (r << 16) | (g << 8) | b;
				}
			}
		}
		
		updateEditedImage();
	}
	
	private void updateEditedImage()
	{
		if (editedImage == null)
			return;
		
		if (alterImage)
		{
			int w = editedImage.getWidth();
			
			for (int i = 0; i < editedImage.getWidth(); i++)
			{
				for (int j = 0; j < editedImage.getHeight(); j++)
				{
					int pos = i + j * w;
					int blurredColor = blurred[pos];
	
					int a = (blurredColor >> 24) & 0xFF;
					int r = (blurredColor >> 16) & 0xFF;
					int g = (blurredColor >> 8) & 0xFF;
					int b = (blurredColor) & 0xFF;
					
					r = Math.abs(red[pos] - r);
					g = Math.abs(green[pos] - g);
					b = Math.abs(blue[pos] - b);
					a = Math.abs(a);
					
					int gray = (r + g + b) / 3;
					
					if (gray > endRGB)
						gray = 255;
					else if (gray < startRGB)
						gray = 0;
					else
						gray = (gray - startRGB) * 255 / (endRGB - startRGB + 1);
	
					edited[pos] = (a << 24) | (gray << 16) | (gray << 8) | gray;
				}
			}
		} else
		{
			System.arraycopy(src, 0, edited, 0, src.length);
		}
		
		repaint();
	}
	
	private static Font f = new Font("Arial", Font.PLAIN, (int) (23 * Toolkit.getDefaultToolkit().getScreenResolution() / 72.0));
	
	public void paint(Graphics g)
	{
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		if (editedImage != null)
			g.drawImage(editedImage, 0, 0, getScaled(editedImage.getWidth()), getScaled(editedImage.getHeight()), null);
		
		g.setColor(yellow);
		drawRect(selectedX, selectedY, selectedW, selectedH, g);
		g.setColor(Color.BLACK);
		drawRect(selectedX - 1, selectedY - 1, selectedW + 2, selectedH + 2, g);
		
		if (lastCursor == Cursor.HAND_CURSOR)
		{
			g.setColor(highlight);
			g.fillRect(getScaled(selectedX), getScaled(selectedY), getScaled(selectedW), getScaled(selectedH));
		}
		
		//Graphics2D g2d = (Graphics2D) g;
		//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//g2d.setColor(Color.BLACK);
		//g2d.setFont(f);
		//g2d.drawString("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", selectedX, selectedY + selectedH - g.getFontMetrics().getDescent());
	}
	
	public float[][] getSelected()
	{
		if (selectedW < 1 || selectedH < 1)
			return new float[1][1];
		float[][] selected = new float[selectedW][selectedH];
		
		if (edited != null)
		{
			for (int i = 0; i < selectedW; i++)
			{
				for (int j = 0; j < selectedH; j++)
				{
					int color = edited[(i + selectedX) + (j + selectedY) * width];
					selected[i][j] = ((color >> 24) & 0xFF) / 255f * (((color >> 16) & 0xFF) + ((color >> 8) & 0xFF) + (color & 0xFF)) / 255f / 3f;
				}
			}
		}
		
		return selected;
	}
	
	public BufferedImage getSelectedImage()
	{
		BufferedImage ret = new BufferedImage(selectedW, selectedH, BufferedImage.TYPE_INT_ARGB);
		ret.getGraphics().drawImage(editedImage, -selectedX, -selectedY, width, height, null);
		
		return ret;
	}
	
	private Runnable onChange;
	
	public void setOnChange(Runnable r)
	{
		onChange = r;
	}
	
	private void fireOnChange()
	{
		if (onChange != null)
			onChange.run();
	}
	
	private BufferedImage wrap(int[] data, int width, int height)
	{
		DataBuffer buffer = new DataBufferInt(data, data.length);
		SampleModel sm = ColorModel.getRGBdefault().createCompatibleSampleModel(width, height);
		WritableRaster raster = Raster.createWritableRaster(sm, buffer, null);
		return new BufferedImage(ColorModel.getRGBdefault(), raster, false, null);
	}
	
	private void drawRect(int x, int y, int w, int h, Graphics g)
	{
		g.fillRect(getScaled(x), getScaled(y), getScaled(w), getScaled(1));
		g.fillRect(getScaled(x), getScaled(y + h - 1), getScaled(w), getScaled(1));
		g.fillRect(getScaled(x), getScaled(y + 1), getScaled(1), getScaled(h - 2));
		g.fillRect(getScaled(x + w - 1), getScaled(y + 1), getScaled(1), getScaled(h - 2));
	}
	
	public int getScaled(int v)
	{
		return (int) Math.ceil(zoom * v);
	}
	
	private int lastX = -1;
	private int lastY = -1;
	
	private int startedX = 0;
	private int startedY = 0;
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
			isCTRLDown = true;
	}
	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
			isCTRLDown = false;
		if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V)
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
			if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor))
			{
				try
				{
					Image clipboard = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
					BufferedImage converted = new BufferedImage(clipboard.getWidth(null), clipboard.getHeight(null), BufferedImage.TYPE_INT_ARGB);
					converted.getGraphics().drawImage(clipboard, 0, 0, null);
					
					setImage(converted);
				} catch (UnsupportedFlavorException ex)
				{
					ex.printStackTrace();
				} catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
			setCursor(Cursor.getDefaultCursor());
		}
	}
	@Override
	public void keyTyped(KeyEvent e)
	{
		if (e.isControlDown())
		{
			if (e.getKeyCode() == KeyEvent.VK_EQUALS)
				zoom(1);
			else if (e.getKeyCode() == KeyEvent.VK_MINUS)
				zoom(-1);
		}
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		if (isCTRLDown)
		{
			zoom(-e.getPreciseWheelRotation());
		} else
		{
		}
	}
	@Override
	public void mouseClicked(MouseEvent e)
	{
	}
	@Override
	public void mouseEntered(MouseEvent e)
	{
	}
	@Override
	public void mouseExited(MouseEvent e)
	{
		lastX = -1;
		lastY = -1;
		
		updateCursor();
		if (needsRepaint)
			repaint();
	}
	@Override
	public void mousePressed(MouseEvent e)
	{
		requestFocus();
		isDragging = true;
		startedX = (int) (e.getX() / zoom) - selectedX;
		startedY = (int) (e.getY() / zoom) - selectedY;
		//tooltip = createToolTip();
	}
	@Override
	public void mouseReleased(MouseEvent e)
	{
		isDragging = false;
		if (selectedW < 0)
		{
			selectedX += selectedW;
			selectedW = -selectedW;
		}
		if (selectedH < 0)
		{
			selectedY += selectedH;
			selectedH = -selectedH;
		}
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		lastX = e.getX();
		lastY = e.getY();
		
		updateCursor();
		if (needsRepaint)
			repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		lastX = e.getX();
		lastY = e.getY();
		
		updateCursor();
		if (needsRepaint)
			repaint();
	}
	
	private void zoom(double amt)
	{
		zoom *= Math.pow(2, amt);
		if (zoom < 0.125f)
			zoom = 0.125f;
		checkSize();
		updateCursor();
		repaint();
	}
	
	private void checkSize()
	{
		if (src == null)
		{
			width = parent.getViewport().getWidth();
			height = parent.getViewport().getHeight();
		}
		setPreferredSize(new Dimension(getScaled(width), getScaled(height)));
		revalidate();
	}
	
	//private JToolTip tooltip;
	
	private void updateCursor()
	{
		needsRepaint = false;
		if (isDragging)
		{
			checkSize();
			if (lastCursor == Cursor.HAND_CURSOR)
			{
				selectedX = (int) ((lastX / zoom) - startedX);
				selectedY = (int) ((lastY / zoom) - startedY);
				if (selectedX < 0)
					selectedX = 0;
				if (selectedY < 0)
					selectedY = 0;
				
				if (selectedY + selectedH > height)
					selectedY = height - selectedH;
				if (selectedX + selectedW > width)
					selectedX = width - selectedW;
				
				fireOnChange();
				
				needsRepaint = true;
				
				//tooltip.setTipText("X: " + selectedX + "px, y: " + selectedY + "px");
			} else if (lastCursor != Cursor.DEFAULT_CURSOR)
			{
				boolean n = lastCursor == Cursor.NW_RESIZE_CURSOR || lastCursor == Cursor.NE_RESIZE_CURSOR || lastCursor == Cursor.N_RESIZE_CURSOR;
				boolean s = lastCursor == Cursor.SW_RESIZE_CURSOR || lastCursor == Cursor.SE_RESIZE_CURSOR || lastCursor == Cursor.S_RESIZE_CURSOR;
				boolean e = lastCursor == Cursor.NE_RESIZE_CURSOR || lastCursor == Cursor.SE_RESIZE_CURSOR || lastCursor == Cursor.E_RESIZE_CURSOR;
				boolean w = lastCursor == Cursor.NW_RESIZE_CURSOR || lastCursor == Cursor.SW_RESIZE_CURSOR || lastCursor == Cursor.W_RESIZE_CURSOR;
				
				if (n)
				{
					selectedH -= (int) (lastY / zoom) - selectedY;
					selectedY += (int) (lastY / zoom) - selectedY;
					if (selectedY < 0)
					{
						selectedH += selectedY;
						selectedY = 0;
					}
					needsRepaint = true;
				} else if (s)
				{
					selectedH = (int) (lastY / zoom) - selectedY;
					if (selectedY + selectedH < 0)
						selectedH = -selectedY;
					needsRepaint = true;
				}

				if (w)
				{
					selectedW -= (int) (lastX / zoom) - selectedX;
					selectedX += (int) (lastX / zoom) - selectedX;
					if (selectedX < 0)
					{
						selectedW += selectedX;
						selectedX = 0;
					}
					needsRepaint = true;
				} else if (e)
				{
					selectedW = (int) (lastX / zoom) - selectedX;
					if (selectedX + selectedW < 0)
						selectedW = -selectedX;
					needsRepaint = true;
				}
				
				if (needsRepaint)
					fireOnChange();
				
				//tooltip.setTipText("Width: " + selectedW + "px, height: " + selectedH + "px");
			}
			//tooltip.setLocation(lastX, lastY);
			//tooltip.setVisible(true);
			//setToolTipText("ayy");
		} else
		{
			if (lastX > getScaled(selectedX + 1) && lastX < getScaled(selectedX + selectedW - 1) && lastY >= getScaled(selectedY + 1) && lastY <= getScaled(selectedY + selectedH - 1))
			{
				if (lastCursor != Cursor.HAND_CURSOR)
				{
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					lastCursor = Cursor.HAND_CURSOR;
					needsRepaint = true;
				}
			} else
			{
				boolean x1 = lastX >= getScaled(selectedX - 1) && lastX <= getScaled(selectedX + 1);
				boolean y1 = lastY >= getScaled(selectedY - 1) && lastY <= getScaled(selectedY + 1);
				boolean x = lastX >= getScaled(selectedX - 1) && lastX <= getScaled(selectedX + selectedW + 1);
				boolean x2 = lastX >= getScaled(selectedX + selectedW - 1) && lastX <= getScaled(selectedX + selectedW + 1);
				boolean y2 = lastY >= getScaled(selectedY + selectedH - 1) && lastY <= getScaled(selectedY + selectedH + 1);
				boolean y = lastY >= getScaled(selectedY - 1) && lastY <= getScaled(selectedY + selectedH + 1);
				
				int lc = lastCursor;
				
				if (x1 && y1)
					lastCursor = Cursor.NW_RESIZE_CURSOR;
				else if (x2 && y2)
					lastCursor = Cursor.SE_RESIZE_CURSOR;
				else if (x1 && y2)
					lastCursor = Cursor.SW_RESIZE_CURSOR;
				else if (x2 && y1)
					lastCursor = Cursor.NE_RESIZE_CURSOR;
				else if (x1 && y)
					lastCursor = Cursor.W_RESIZE_CURSOR;
				else if (x2 && y)
					lastCursor = Cursor.E_RESIZE_CURSOR;
				else if (y1 && x)
					lastCursor = Cursor.N_RESIZE_CURSOR;
				else if (y2 && x)
					lastCursor = Cursor.S_RESIZE_CURSOR;
				else
					lastCursor = Cursor.DEFAULT_CURSOR;
				
				if (lc == Cursor.HAND_CURSOR)
					needsRepaint = true;
				
				if (lc != lastCursor)
					setCursor(Cursor.getPredefinedCursor(lastCursor));
			}
		}
	}
}
