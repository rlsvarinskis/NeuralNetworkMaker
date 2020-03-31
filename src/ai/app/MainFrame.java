package ai.app;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ai.app.data.InputsOutputs;
import ai.app.ui.MenuBarManager;
import ai.app.ui.NetworkPanel;
import ai.app.ui.TrainingPanel;
import ai.app.ui.network.DataAdder;
import ai.app.ui.network.NeuronActivationCollector;
import ai.app.ui.network.ProgressTracker;
import ai.network.types.ANN;
import ai.network.utils.Data;
import ai.network.utils.Vector;

import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame
{
	private static final long serialVersionUID = 6632694708815548016L;
	
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
		new DimensionPicker();
	}
	
	private ANN network;
	
	private WorkerThread worker;
	
	private MenuBarManager menuBar;
	private TrainingPanel trainingPanel;
	private NetworkPanel networkPanel;
	private JProgressBar progressBar;
	
	private File opened;
	
	private boolean isSaved = true;
	
	public MainFrame(int inputs, int outputs, int[] layers, File from)
	{
		super("Neural Network Trainer");
		opened = from;
		addWindowListener(new WindowListener()
		{
			@Override
			public void windowOpened(WindowEvent e) {
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		network = new ANN(inputs, outputs, layers);
		network.initializeRandomly(System.currentTimeMillis());
		
		worker = new WorkerThread();
		worker.start();
		
		int[] l = new int[layers.length + 2];
		System.arraycopy(layers, 0, l, 1, layers.length);
		l[0] = inputs;
		l[l.length - 1] = outputs;
		
		initializeComponents(l);
		setSize(1366, 768);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void initializeComponents(int[] layers)
	{
		progressBar = new JProgressBar();
		trainingPanel = new TrainingPanel(train);
		networkPanel = new NetworkPanel(layers);
		menuBar = new MenuBarManager();

		menuBar.addFileNewAction(newAction);
		menuBar.addFileOpenAction(open);
		menuBar.addFileSaveAction(save);
		menuBar.addFileSaveAsAction(saveAs);
		menuBar.addFileExportAction(export);
		menuBar.addFileExitAction(exit);
		
		menuBar.addEditReinitializeAction(reinitializeRandomly);
		menuBar.addEditTrainAction(train);
		menuBar.addEditRunAction(run);
		
		networkPanel.setReinitializeAction(reinitializeRandomly);
		networkPanel.setRunAction(run);
		networkPanel.setDataAdder(dataAdder);
		
		getContentPane().add(progressBar, BorderLayout.SOUTH);
		getContentPane().add(trainingPanel, BorderLayout.WEST);
		getContentPane().add(networkPanel, BorderLayout.CENTER);
		setJMenuBar(menuBar);
	}
	
	public void setSaved(boolean saved)
	{
		if (isSaved != saved)
		{
			if (saved)
				setTitle("Neural Network Trainer");
			else
				setTitle("*Neural Network Trainer");
			isSaved = saved;
		}
	}
	
	private ActionListener newAction = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			if (close())
				new DimensionPicker();
		}
	};
	
	private ActionListener open = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			//TODO save before
			JFileChooser jfc = new JFileChooser();
			
			if (jfc.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)
			{
				open(jfc.getSelectedFile());
			}
		}
	};
	
	private ActionListener save = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			save();
		}
	};
	
	private ActionListener saveAs = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			saveAs();
		}
	};
	
	private ActionListener export = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			export();
		}
	};
	
	private ActionListener exit = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			close();
		}
	};
	
	private static MainFrame open(File f)
	{
		//TODO open
		return null;
	}
	
	private void save()
	{
		if (opened == null)
			saveAs();
		else
			save(opened);
	}
	
	private void saveAs()
	{
		JFileChooser jfc = new JFileChooser();
		
		if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
			save(jfc.getSelectedFile());
	}
	
	private void save(File f)
	{
		opened = f;
		//TODO save
		setSaved(true);
	}
	
	private void export()
	{
		JFileChooser jfc = new JFileChooser();
		
		if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
			export(jfc.getSelectedFile());
	}
	
	private void export(File f)
	{
		//TODO export
	}
	
	private boolean close()
	{
		if (!isSaved)
		{
			int save = JOptionPane.showOptionDialog(MainFrame.this, "Do you want to exit without saving?", "Exit without saving?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Save", "Exit", "Cancel"}, "Save");
			
			if (save == JOptionPane.CLOSED_OPTION || save == 2)
				return false;
			else if (save == 0)
				save();
		}
		setVisible(false);
		worker.close();
		networkPanel.close();
		dispose();
		return true;
	}
	
	private DataAdder dataAdder = new DataAdder()
	{
		public void dataAdded(String name, float[] inputs, float[] outputs, BufferedImage icon)
		{
			trainingPanel.addData(name, inputs, outputs, icon);
			try
			{
				InputsOutputs.save(new Data(inputs, outputs), name);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	};
	
	private ActionListener train = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			int[] selected = trainingPanel.getSelectedData();
			if (selected.length == 0)
				return;
			
			trainingPanel.setEnabled(false);
			menuBar.setTrainEnabled(false);
			networkPanel.setActionsEnabled(false);
			setSaved(false);
			
			int repeats = trainingPanel.getRepeatAmount();
			int batchSize = trainingPanel.getBatchSize();
			float learningRate = trainingPanel.getLearningRate();
			float momentum = trainingPanel.getMomentum();
			
			Data[] data = trainingPanel.getData();
			
			progressBar.setValue(0);
			progressBar.setMaximum(repeats * data.length);
			progressBar.setMinimum(0);
			
			worker.giveTask(new Runnable()
			{
				public void run()
				{
					network.train(data, repeats, batchSize, learningRate, momentum, new ProgressTracker()
					{
						public void progress()
						{
							progressBar.setValue(progressBar.getValue() + 1);
							progressBar.repaint();
						}
						
						public void progress(int batches, float error)
						{
							networkPanel.addErrorPoint(batches, error);
							networkPanel.setError(error);
						}
					});
					trainingPanel.setEnabled(true);
					menuBar.setTrainEnabled(true);
					networkPanel.setActionsEnabled(true);
				}
			});
		}
	};
	
	private ActionListener run = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			trainingPanel.setEnabled(false);
			menuBar.setTrainEnabled(false);
			networkPanel.setActionsEnabled(false);
			
			int[] selected = trainingPanel.getSelectedData();
			
			if (selected.length == 0)
				return;
			
			String name = trainingPanel.getName(selected[0]);
			Data toRun = trainingPanel.getData(selected[0]);
			ImageIcon thumbnail = trainingPanel.getThumbnail(selected[0]);

			worker.giveTask(new Runnable()
			{
				public void run()
				{
					float[] output = network.run(toRun.getInputs(), new NeuronActivationCollector()
					{
						int layer = 0;
						int transLayer = 0;
						
						public void layerValues(float[] values)
						{
							networkPanel.layerValues(values, layer++);
						}
						
						public void transLayerValues(float[][] values)
						{
							networkPanel.transLayerValues(values, transLayer++);
						}
					});
					float[] outputs = network.run(toRun.getInputs());
					float[] roundedOut = Vector.subtract(ANN.round(outputs), toRun.getOutputs());
					float last = roundedOut[roundedOut.length - 1];
					roundedOut[roundedOut.length - 1] = 0;
					
					networkPanel.result(name, thumbnail, output, Vector.distance(roundedOut) == 0, last == 0);
					
					for (int i = 1; i < selected.length; i++)
					{
						outputs = network.run(trainingPanel.getData(selected[i]).getInputs());
						roundedOut = Vector.subtract(ANN.round(outputs), trainingPanel.getData(selected[i]).getOutputs());
						roundedOut[roundedOut.length - 1] = 0;
						last = roundedOut[roundedOut.length - 1];
						networkPanel.result(trainingPanel.getName(selected[i]), trainingPanel.getThumbnail(selected[i]), outputs, Vector.distance(roundedOut) == 0, last == 0);
					}
					
					networkPanel.updateNetworkDisplay();
					trainingPanel.setEnabled(true);
					menuBar.setTrainEnabled(true);
					networkPanel.setActionsEnabled(true);
				}
			});
		}
	};
	
	private ActionListener reinitializeRandomly = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			network.initializeRandomly(System.currentTimeMillis());
			setSaved(true);
		}
	};
}
