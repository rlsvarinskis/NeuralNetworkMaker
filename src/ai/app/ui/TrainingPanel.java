package ai.app.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import ai.app.data.InputsOutputs;
import ai.app.ui.data.DataLeaf;
import ai.app.ui.data.DataManager;
import ai.network.utils.Data;

public class TrainingPanel extends JPanel
{
	private static final long serialVersionUID = 4384428322826405463L;
	
	private DataManager trainingData;
	
	private JButton btnAddTrainingData = new JButton("Add");
	private JButton btnSelectAll = new JButton("Select all");
	private JButton btnRemove = new JButton("Remove");
	
	private JLabel selectedAmount = new JLabel("0");
	private JSpinner epochsSpinner = new JSpinner();
	private JSpinner batchSizeSpinner = new JSpinner();
	private JSpinner learningRateSpinner = new JSpinner();
	private JSpinner momentumSpinner = new JSpinner();
	
	private JButton trainButton = new JButton("Train");

	public TrainingPanel(ActionListener train)
	{
		setBorder(new TitledBorder(null, "Network training", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(59, 59, 59)));
		setLayout(new BorderLayout(0, 0));
		
		JPanel trainingDataActions = new JPanel();
		add(trainingDataActions, BorderLayout.NORTH);
		trainingDataActions.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		
		trainingDataActions.add(btnAddTrainingData);
		trainingDataActions.add(btnSelectAll);
		trainingDataActions.add(btnRemove);
		
		btnSelectAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				trainingData.selectAll();
			}
		});
		btnRemove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				trainingData.removeSelected();
			}
		});
		
		trainingData = new DataManager(selectedAmount);
		
		for (int i = 0; i < InputsOutputs.data.size(); i++)
			trainingData.add(InputsOutputs.name.get(i), InputsOutputs.data.get(i), InputsOutputs.thumbnail.get(i));
		
		JScrollPane trainingDataContainer = new JScrollPane(trainingData);
		add(trainingDataContainer);
		
		JPanel trainingSettings = new JPanel();
		trainingSettings.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		add(trainingSettings, BorderLayout.SOUTH);
		trainingSettings.setLayout(new GridLayout(6, 2, 2, 2));
		
		JLabel selectedLabel = new JLabel("Selected:");
		selectedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		trainingSettings.add(selectedLabel);
		
		selectedLabel.setLabelFor(selectedAmount);
		selectedAmount.setHorizontalAlignment(SwingConstants.RIGHT);
		trainingSettings.add(selectedAmount);
		
		JLabel epochsLabel = new JLabel("Times:");
		epochsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		trainingSettings.add(epochsLabel);
		epochsSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		
		epochsSpinner.setToolTipText("How many times do you want to use this training set to train the neural network");
		epochsLabel.setLabelFor(epochsSpinner);
		trainingSettings.add(epochsSpinner);
		
		JLabel batchSizeLabel = new JLabel("Batch size:");
		batchSizeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		trainingSettings.add(batchSizeLabel);
		batchSizeSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		
		batchSizeSpinner.setToolTipText("In what small batches should the training data be subdivided in when training the neural network");
		batchSizeLabel.setLabelFor(batchSizeSpinner);
		trainingSettings.add(batchSizeSpinner);
		
		JLabel learningRateLabel = new JLabel("Change multiplier:");
		learningRateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		trainingSettings.add(learningRateLabel);
		
		learningRateSpinner.setToolTipText("How big of an effect should the training have on the neural network's values");
		learningRateSpinner.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1f)));
		learningRateLabel.setLabelFor(learningRateSpinner);
		trainingSettings.add(learningRateSpinner);
		
		JLabel momentumLabel = new JLabel("Momentum:");
		momentumLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		trainingSettings.add(momentumLabel);
		
		momentumSpinner.setToolTipText("How big of an effect should the training have on the neural network's values");
		momentumSpinner.setModel(new SpinnerNumberModel(new Float(0), new Float(0), new Float(1), new Float(0.1f)));
		learningRateLabel.setLabelFor(momentumSpinner);
		trainingSettings.add(momentumSpinner);
		
		JLabel emptyLabel = new JLabel();
		trainingSettings.add(emptyLabel);
		
		trainButton.addActionListener(train);
		trainingSettings.add(trainButton);
	}
	
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		epochsSpinner.setEnabled(enabled);
		batchSizeSpinner.setEnabled(enabled);
		learningRateSpinner.setEnabled(enabled);
		momentumSpinner.setEnabled(enabled);
		trainButton.setEnabled(enabled);
		trainingData.setEnabled(enabled);
		btnAddTrainingData.setEnabled(enabled);
		btnSelectAll.setEnabled(enabled);
		btnRemove.setEnabled(enabled);
	}
	
	public int getRepeatAmount()
	{
		return (int) epochsSpinner.getValue();
	}
	
	public int getBatchSize()
	{
		return (int) batchSizeSpinner.getValue();
	}
	
	public float getLearningRate()
	{
		return (float) learningRateSpinner.getValue();
	}
	
	public float getMomentum()
	{
		return (float) momentumSpinner.getValue();
	}
	
	public void addData(String name, float[] inputs, float[] outputs, BufferedImage icon)
	{
		trainingData.add(name, new Data(inputs, outputs), icon);
	}
	
	public int[] getSelectedData()
	{
		return trainingData.getSelectionRows();
	}
	
	public String getName(int i)
	{
		return ((DataLeaf) trainingData.getPathForRow(i).getLastPathComponent()).toString();
	}
	
	public ImageIcon getThumbnail(int i)
	{
		return ((DataLeaf) trainingData.getPathForRow(i).getLastPathComponent()).getIcon();
	}
	
	public Data getData(int i)
	{
		return ((DataLeaf) trainingData.getPathForRow(i).getLastPathComponent()).getData();
	}
	
	public Data[] getData()
	{
		return trainingData.getSelected();
	}
}
