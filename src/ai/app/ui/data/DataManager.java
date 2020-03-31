package ai.app.ui.data;

import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import ai.network.utils.Data;

public class DataManager extends JTree
{
	private static final long serialVersionUID = -4744817995749118482L;
	
	private DefaultMutableTreeNode root;
	
	private JLabel selected;
	
	public DataManager(JLabel selected)
	{
		super(new DefaultTreeModel(new DefaultMutableTreeNode("root")));
		setRootVisible(false);
		root = (DefaultMutableTreeNode) getModel().getRoot();
		this.selected = selected;
		
		setCellRenderer(new DefaultTreeCellRenderer()
		{
			private static final long serialVersionUID = 450132503317231506L;

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
			{
				Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
				//TODO when the JTree is disabled, the icon doesn't work
				if (leaf && value instanceof DataLeaf)
					setIcon(((DataLeaf) value).getIcon());
				else
					setIcon(null);
				return c;
			}
		});
		
		addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				selected.setText(getSelectionCount() + "/" + getModel().getChildCount(root));
				for (int i = 0; i < e.getPaths().length; i++)
				{
					
				}
			}
		});
		setRowHeight(32);
	}
	
	public void add(String name, Data data, BufferedImage icon)
	{
		root.add(new DataLeaf(name, data, icon));
		((DefaultTreeModel) getModel()).nodeStructureChanged(root);
		
		selected.setText(getSelectionCount() + "/" + getModel().getChildCount(root));
	}
	
	public void removeSelected()
	{
		TreePath[] paths = getSelectionPaths();
		
		for (int i = 0; i < paths.length; i++)
		{
			if (paths[i].getLastPathComponent() instanceof DataLeaf)
			{
				MutableTreeNode child = (MutableTreeNode) paths[i].getLastPathComponent();
				MutableTreeNode parent = (MutableTreeNode) child.getParent();
				((DefaultTreeModel) getModel()).nodesWereRemoved(parent, new int[]{parent.getIndex(child)}, new Object[]{child});
				child.removeFromParent();
				paths[i] = null;
			}
		}
		
		selected.setText(getSelectionCount() + "/" + getModel().getChildCount(root));
	}
	
	public void selectAll()
	{
		if (getSelectionCount() == 0)
			setSelectionInterval(0, getRowCount());
		else
			clearSelection();
		
		selected.setText(getSelectionCount() + "/" + getModel().getChildCount(root));
	}
	
	public Data[] getSelected()
	{
		TreePath[] paths = getSelectionPaths();
		
		int dataAmt = 0;
		
		Data[] data = new Data[paths.length];
		
		for (int i = 0; i < data.length; i++)
			if (paths[i].getLastPathComponent() instanceof DataLeaf)
				data[dataAmt++] = ((DataLeaf) paths[i].getLastPathComponent()).getData();
		
		Data[] newData = new Data[dataAmt];
		System.arraycopy(data, 0, newData, 0, dataAmt);
		
		return newData;
	}
}
