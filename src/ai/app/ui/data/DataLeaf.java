package ai.app.ui.data;

import java.awt.image.BufferedImage;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import ai.network.utils.Data;

public class DataLeaf implements MutableTreeNode
{
	private MutableTreeNode parent;
	
	private String name;
	private Data data;
	private ImageIcon icon;
	
	public DataLeaf(String name, Data data, BufferedImage icon)
	{
		this.name = name;
		this.data = data;
		this.icon = new ImageIcon(icon);
	}
	
	public Object getUserObject()
	{
		return this;
	}
	
	public ImageIcon getIcon()
	{
		return icon;
	}
	
	public Data getData()
	{
		return data;
	}
	
	@Override
	public boolean getAllowsChildren()
	{
		return false;
	}
	
	public String toString()
	{
		return name;
	}

	@Override
	public Enumeration children()
	{
		return null;
	}

	@Override
	public TreeNode getChildAt(int arg0)
	{
		return null;
	}

	@Override
	public int getChildCount()
	{
		return 0;
	}

	@Override
	public int getIndex(TreeNode arg0)
	{
		return -1;
	}

	@Override
	public TreeNode getParent()
	{
		return parent;
	}

	@Override
	public boolean isLeaf()
	{
		return true;
	}

	@Override
	public void insert(MutableTreeNode child, int index)
	{
	}

	@Override
	public void remove(int index)
	{
	}

	@Override
	public void remove(MutableTreeNode node)
	{
	}

	@Override
	public void removeFromParent()
	{
		parent.remove(this);
		parent = null;
	}

	@Override
	public void setParent(MutableTreeNode newParent)
	{
		parent = newParent;
	}

	@Override
	public void setUserObject(Object object)
	{
	}
}
