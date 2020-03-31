package ai.app.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

public class MenuBarManager extends JMenuBar
{
	private static final long serialVersionUID = 1414076467090618586L;
	
	private JMenuItem mntmNew = new JMenuItem("New");
	private JMenuItem mntmOpen = new JMenuItem("Open");
	private JMenuItem mntmSave = new JMenuItem("Save");
	private JMenuItem mntmSaveAs = new JMenuItem("Save As");
	private JMenuItem mntmExport = new JMenuItem("Export");
	private JMenuItem mntmExit = new JMenuItem("Exit");
	
	private JMenuItem mntmAssignRandomWeights = new JMenuItem("Reinitialize Randomly");
	private JMenuItem mntmImportTrainingData = new JMenuItem("Add Training Data");
	private JMenuItem mntmTrain = new JMenuItem("Train");
	private JMenuItem mntmRun = new JMenuItem("Run");
	
	private JMenuItem mntmHelp = new JMenuItem("Help");
	private JMenuItem mntmAbout = new JMenuItem("About");
	
	public MenuBarManager()
	{
		add(createFileMenu());
		add(createEditMenu());
		add(createHelpMenu());
	}
	
	public void setTrainEnabled(boolean enabled)
	{
		mntmNew.setEnabled(enabled);
		mntmOpen.setEnabled(enabled);
		mntmAssignRandomWeights.setEnabled(enabled);
		mntmTrain.setEnabled(enabled);
		mntmRun.setEnabled(enabled);
	}
	
	public void addFileNewAction(ActionListener l)
	{
		mntmNew.addActionListener(l);
	}
	
	public void addFileOpenAction(ActionListener l)
	{
		mntmOpen.addActionListener(l);
	}
	
	public void addFileSaveAction(ActionListener l)
	{
		mntmSave.addActionListener(l);
	}
	
	public void addFileSaveAsAction(ActionListener l)
	{
		mntmSaveAs.addActionListener(l);
	}
	
	public void addFileExportAction(ActionListener l)
	{
		mntmExport.addActionListener(l);
	}
	
	public void addFileExitAction(ActionListener l)
	{
		mntmExit.addActionListener(l);
	}
	
	public void addEditReinitializeAction(ActionListener l)
	{
		mntmAssignRandomWeights.addActionListener(l);
	}
	
	public void addEditTrainAction(ActionListener l)
	{
		mntmTrain.addActionListener(l);
	}
	
	public void addEditRunAction(ActionListener l)
	{
		mntmRun.addActionListener(l);
	}
	
	private JMenu createFileMenu()
	{
		JMenu mnFile = new JMenu("File");

		JSeparator separator = new JSeparator();
		JSeparator separator1 = new JSeparator();
		
		mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mntmSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mntmExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		
		mnFile.add(mntmNew);
		mnFile.add(mntmOpen);
		mnFile.add(mntmSave);
		mnFile.add(mntmSaveAs);
		mnFile.add(separator);
		mnFile.add(mntmExport);
		mnFile.add(separator1);
		mnFile.add(mntmExit);
		
		return mnFile;
	}
	
	private JMenu createEditMenu()
	{
		JMenu mnEdit = new JMenu("Edit");
		
		JSeparator separator_2 = new JSeparator();
		JSeparator separator_3 = new JSeparator();
		
		mntmImportTrainingData.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
		mntmTrain.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));
		mntmRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		
		mntmTrain.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(null, "Menu action", "Aktion!", JOptionPane.PLAIN_MESSAGE); //TODO make the menu bar work
			}
		});
		
		mnEdit.add(mntmAssignRandomWeights);
		mnEdit.add(separator_2);
		mnEdit.add(mntmImportTrainingData);
		mnEdit.add(separator_3);
		mnEdit.add(mntmTrain);
		mnEdit.add(mntmRun);
		
		return mnEdit;
	}
	
	private JMenu createHelpMenu()
	{
		JMenu mnHelp = new JMenu("Help");
		
		mnHelp.add(mntmHelp);
		mnHelp.add(mntmAbout);
		
		return mnHelp;
	}
}
