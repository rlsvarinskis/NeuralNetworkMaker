package ai.app;

import javax.swing.JDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class DimensionPicker extends JDialog
{
	private static final long serialVersionUID = 4366064989328825094L;
	
	private ArrayList<JLabel> labels = new ArrayList<>();
	private ArrayList<JSpinner> layers = new ArrayList<>();
	
	private JSpinner inputsSpinner;
	private JSpinner layersSpinner;
	private JSpinner outputsSpinner;
	
	private JPanel layersPanel;
	
	private JButton createButton;
	
	public DimensionPicker()
	{
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Neural network dimensions");
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel paddingPanel = new JPanel();
		getContentPane().add(paddingPanel);
		paddingPanel.setLayout(new BorderLayout(0, 0));
		
		paddingPanel.add(createTopPanel(), BorderLayout.NORTH);
		paddingPanel.add(createLayersPanel(), BorderLayout.CENTER);
		paddingPanel.add(createBottomPanel(), BorderLayout.SOUTH);
		
		layersSpinner.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				int to = (int) layersSpinner.getValue();
				while (to > labels.size())
					addLayer();
				while (to < labels.size())
					removeLayer();
			}
		});
		
		createButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				createButton.setEnabled(false);
				int inputs = (int) inputsSpinner.getValue();
				int outputs = (int) outputsSpinner.getValue();
				int[] layer = new int[layers.size()];
				
				for (int i = 0; i < layer.length; i++)
					layer[i] = (int) layers.get(i).getValue();
				
				new MainFrame(inputs, outputs, layer, null);
				
				dispose();
			}
		});
		
		pack();
		
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void removeLayer()
	{
		if (labels.size() > 0)
		{
			layersPanel.remove(labels.size() * 2 - 1);
			layersPanel.remove(labels.size() * 2 - 2);
			
			labels.remove(labels.size() - 1);
			layers.remove(layers.size() - 1);
		}
		invalidate();
		pack();
	}
	
	private void addLayer()
	{
		JLabel lblLayer = new JLabel("Layer " + (labels.size() + 1) + ":");
		GridBagConstraints gbc_lblLayer = new GridBagConstraints();
		gbc_lblLayer.anchor = GridBagConstraints.WEST;
		gbc_lblLayer.fill = GridBagConstraints.VERTICAL;
		gbc_lblLayer.insets = new Insets(0, 0, 0, 5);
		gbc_lblLayer.gridx = 0;
		gbc_lblLayer.gridy = labels.size();
		layersPanel.add(lblLayer, gbc_lblLayer);
		
		JSpinner spinner = new JSpinner();
		lblLayer.setLabelFor(spinner);
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.fill = GridBagConstraints.BOTH;
		gbc_spinner.gridx = 1;
		gbc_spinner.insets = new Insets(0, 0, 2, 0);
		gbc_spinner.gridy = layers.size();
		
		layersPanel.add(spinner, gbc_spinner);
		
		labels.add(lblLayer);
		layers.add(spinner);
		
		invalidate();
		pack();
	}
	
	private JPanel createTopPanel()
	{
		JPanel topPanel = new JPanel();
		GridBagLayout gbl_topPanel = new GridBagLayout();
		gbl_topPanel.columnWidths = new int[]{0, 70, 0};
		gbl_topPanel.rowHeights = new int[]{20, 0, 0};
		gbl_topPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_topPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		topPanel.setLayout(gbl_topPanel);
		
		JLabel lblInputs = new JLabel("Inputs:");
		GridBagConstraints gbc_lblInputs = new GridBagConstraints();
		gbc_lblInputs.anchor = GridBagConstraints.WEST;
		gbc_lblInputs.insets = new Insets(0, 0, 5, 5);
		gbc_lblInputs.gridx = 0;
		gbc_lblInputs.gridy = 0;
		topPanel.add(lblInputs, gbc_lblInputs);
		
		inputsSpinner = new JSpinner();
		lblInputs.setLabelFor(inputsSpinner);
		GridBagConstraints gbc_inputsSpinner = new GridBagConstraints();
		gbc_inputsSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_inputsSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_inputsSpinner.gridx = 1;
		gbc_inputsSpinner.gridy = 0;
		topPanel.add(inputsSpinner, gbc_inputsSpinner);
		inputsSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		
		JLabel lblLayers = new JLabel("Layers:");
		GridBagConstraints gbc_lblLayers = new GridBagConstraints();
		gbc_lblLayers.anchor = GridBagConstraints.WEST;
		gbc_lblLayers.insets = new Insets(0, 0, 0, 5);
		gbc_lblLayers.gridx = 0;
		gbc_lblLayers.gridy = 1;
		topPanel.add(lblLayers, gbc_lblLayers);
		
		layersSpinner = new JSpinner();
		lblLayers.setLabelFor(layersSpinner);
		layersSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(0), null, new Integer(1)));
		GridBagConstraints gbc_layersSpinner = new GridBagConstraints();
		gbc_layersSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_layersSpinner.gridx = 1;
		gbc_layersSpinner.gridy = 1;
		topPanel.add(layersSpinner, gbc_layersSpinner);
		
		JPanel topPanelPadding = new JPanel();
		FlowLayout fl_topPanelPadding = (FlowLayout) topPanelPadding.getLayout();
		fl_topPanelPadding.setHgap(6);
		topPanelPadding.add(topPanel);
		
		return topPanelPadding;
	}
	
	private JPanel createLayersPanel()
	{
		layersPanel = new JPanel();
		layersPanel.setBorder(new TitledBorder(null, "Layers", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gbl_layersPanel = new GridBagLayout();
		gbl_layersPanel.columnWidths = new int[]{0, 0};
		gbl_layersPanel.rowHeights = new int[]{16, 0};
		gbl_layersPanel.columnWeights = new double[]{0.0, 1.0};
		gbl_layersPanel.rowWeights = new double[]{0.0};
		layersPanel.setLayout(gbl_layersPanel);
		
		addLayer();
		
		return layersPanel;
	}
	
	private JPanel createBottomPanel()
	{
		JPanel bottomPanelPadding = new JPanel();
		FlowLayout fl_bottomPanelPadding = (FlowLayout) bottomPanelPadding.getLayout();
		fl_bottomPanelPadding.setVgap(3);
		fl_bottomPanelPadding.setHgap(3);
		
		JPanel bottomPanel = new JPanel();
		bottomPanelPadding.add(bottomPanel);
		GridBagLayout gbl_bottomPanel = new GridBagLayout();
		gbl_bottomPanel.columnWidths = new int[]{0, 0, 0};
		gbl_bottomPanel.rowHeights = new int[]{12, 9, 0};
		gbl_bottomPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_bottomPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		bottomPanel.setLayout(gbl_bottomPanel);
		
		JLabel lblOutputs = new JLabel("Outputs:");
		GridBagConstraints gbc_lblOutputs = new GridBagConstraints();
		gbc_lblOutputs.anchor = GridBagConstraints.WEST;
		gbc_lblOutputs.insets = new Insets(0, 0, 5, 5);
		gbc_lblOutputs.gridx = 0;
		gbc_lblOutputs.gridy = 0;
		bottomPanel.add(lblOutputs, gbc_lblOutputs);
		
		outputsSpinner = new JSpinner();
		lblOutputs.setLabelFor(outputsSpinner);
		outputsSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		GridBagConstraints gbc_outputsSpinner = new GridBagConstraints();
		gbc_outputsSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_outputsSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_outputsSpinner.gridx = 1;
		gbc_outputsSpinner.gridy = 0;
		bottomPanel.add(outputsSpinner, gbc_outputsSpinner);
		
		createButton = new JButton("Create");
		GridBagConstraints gbc_createButton = new GridBagConstraints();
		gbc_createButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_createButton.gridx = 1;
		gbc_createButton.gridy = 1;
		bottomPanel.add(createButton, gbc_createButton);
		
		return bottomPanelPadding;
	}
}
