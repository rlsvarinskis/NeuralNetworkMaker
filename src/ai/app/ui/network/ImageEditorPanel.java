package ai.app.ui.network;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import java.awt.BorderLayout;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ai.app.ui.imageeditor.ImageEditor;
import ai.app.ui.imageeditor.ImageUtils;
import ai.app.data.InputsOutputs;
import ai.app.ui.imageeditor.AnswerDisplay;
import ai.app.ui.imageeditor.ValueDisplay;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.ScrollPaneConstants;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

public class ImageEditorPanel extends JPanel
{
	private static final long serialVersionUID = 5983684567867755586L;
	
	private ImageEditor imageEditor;
	private JButton detectLetters;
	private JButton btnLoadImage;
	private JButton btnAddSelected;
	private JSlider thresholdSlider;
	private JSpinner thresholdSpinner;
	private JSlider radiusSlider;
	private JSlider rangeSliderFrom;
	private AnswerDisplay outputValues;
	private JSlider rangeSliderTo;
	private JSpinner heightSpinner;
	private JSpinner widthSpinner;
	private ValueDisplay selectedDisplay;
	
	public ImageEditorPanel()
	{
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane imageScroller = new JScrollPane();
		imageEditor = new ImageEditor(imageScroller);
		imageScroller.setViewportView(imageEditor);
		add(imageScroller);
		
		JPanel sidebar = new JPanel();
		sidebar.setSize(300, 0);
		add(sidebar, BorderLayout.EAST);
		sidebar.setLayout(new BorderLayout(0, 0));
		
		JPanel imageAdjustments = new JPanel();
		sidebar.add(imageAdjustments, BorderLayout.NORTH);
		imageAdjustments.setLayout(new BoxLayout(imageAdjustments, BoxLayout.Y_AXIS));
		
		JPanel thresholdAdjustment = new JPanel();
		imageAdjustments.add(thresholdAdjustment);
		thresholdAdjustment.setBorder(new TitledBorder(null, "Threshold", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		thresholdAdjustment.setLayout(new BorderLayout(0, 0));
		
		thresholdSlider = new JSlider();
		thresholdSlider.setValue(100);
		thresholdSlider.setMajorTickSpacing(10);
		thresholdAdjustment.add(thresholdSlider);
		
		thresholdSpinner = new JSpinner();
		thresholdSpinner.setModel(new SpinnerNumberModel(100, 0, 100, 1));
		thresholdAdjustment.add(thresholdSpinner, BorderLayout.EAST);
		
		thresholdSpinner.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				thresholdSlider.setValue((int) thresholdSpinner.getValue());
				imageEditor.setThreshold(thresholdSlider.getValue() / 100f);
			}
		});
		thresholdSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				thresholdSpinner.setValue(thresholdSlider.getValue());
				imageEditor.setThreshold(thresholdSlider.getValue() / 100f);
			}
		});
		
		JPanel radiusAdjustment = new JPanel();
		imageAdjustments.add(radiusAdjustment);
		radiusAdjustment.setBorder(new TitledBorder(null, "Radius", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		radiusAdjustment.setLayout(new BorderLayout(0, 0));
		
		radiusSlider = new JSlider();
		radiusSlider.setValue(5);
		radiusSlider.setMajorTickSpacing(10);
		radiusAdjustment.add(radiusSlider);
		
		JSpinner radiusSpinner = new JSpinner();
		radiusSpinner.setModel(new SpinnerNumberModel(5, 1, 100, 1));
		radiusAdjustment.add(radiusSpinner, BorderLayout.EAST);
		
		radiusSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				radiusSpinner.setValue(radiusSlider.getValue());
				imageEditor.setRadius(radiusSlider.getValue());
			}
		});
		radiusSpinner.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				radiusSlider.setValue((int) radiusSpinner.getValue());
				imageEditor.setRadius(radiusSlider.getValue());
			}
		});
		
		JPanel rangeAdjustments = new JPanel();
		imageAdjustments.add(rangeAdjustments);
		rangeAdjustments.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Range", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		rangeAdjustments.setLayout(new BorderLayout(0, 0));
		
		rangeSliderFrom = new JSlider();
		rangeSliderFrom.setValue(0);
		rangeSliderFrom.setMaximum(255);
		rangeSliderFrom.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				imageEditor.setStartRGB(Math.min(rangeSliderFrom.getValue(), rangeSliderTo.getValue()));
			}
		});
		rangeAdjustments.add(rangeSliderFrom, BorderLayout.NORTH);
		
		rangeSliderTo = new JSlider();
		rangeSliderTo.setValue(255);
		rangeSliderTo.setMaximum(255);
		rangeSliderTo.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				imageEditor.setEndRGB(Math.max(rangeSliderFrom.getValue(), rangeSliderTo.getValue()));
			}
		});
		rangeAdjustments.add(rangeSliderTo, BorderLayout.SOUTH);
		
		JPanel selectedInfo = new JPanel();
		sidebar.add(selectedInfo, BorderLayout.CENTER);
		selectedInfo.setLayout(new BorderLayout(0, 0));
		
		JPanel selectedDimensions = new JPanel();
		selectedInfo.add(selectedDimensions, BorderLayout.NORTH);
		GridBagLayout gbl_selectedDimensions = new GridBagLayout();
		gbl_selectedDimensions.columnWidths = new int[]{0, 65, 39, 65, 0};
		gbl_selectedDimensions.rowHeights = new int[]{24, 28, 0};
		gbl_selectedDimensions.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_selectedDimensions.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		selectedDimensions.setLayout(gbl_selectedDimensions);
		
		selectedDisplay = new ValueDisplay(30, 30);
		selectedDimensions.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				selectedDisplay.setPreferredSize(new java.awt.Dimension(selectedDimensions.getWidth(), selectedDisplay.getHeight(selectedDimensions.getWidth())));
				selectedDisplay.revalidate();
			}
		});
		GridBagConstraints gbc_selectedDisplay = new GridBagConstraints();
		gbc_selectedDisplay.gridwidth = 4;
		gbc_selectedDisplay.insets = new Insets(0, 0, 5, 0);
		gbc_selectedDisplay.fill = GridBagConstraints.HORIZONTAL;
		gbc_selectedDisplay.gridx = 0;
		gbc_selectedDisplay.gridy = 0;
		selectedDimensions.add(selectedDisplay, gbc_selectedDisplay);
		
		imageEditor.setOnChange(new Runnable()
		{
			public void run()
			{
				selectedDisplay.setValues(imageEditor.getSelected());
			}
		});
		
		JLabel lblWidth = new JLabel("Width:");
		GridBagConstraints gbc_lblWidth = new GridBagConstraints();
		gbc_lblWidth.anchor = GridBagConstraints.WEST;
		gbc_lblWidth.insets = new Insets(0, 0, 0, 5);
		gbc_lblWidth.gridx = 0;
		gbc_lblWidth.gridy = 1;
		selectedDimensions.add(lblWidth, gbc_lblWidth);
		
		widthSpinner = new JSpinner();
		widthSpinner.setModel(new SpinnerNumberModel(new Integer(30), new Integer(1), null, new Integer(1)));
		GridBagConstraints gbc_widthSpinner = new GridBagConstraints();
		gbc_widthSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_widthSpinner.insets = new Insets(0, 0, 0, 5);
		gbc_widthSpinner.gridx = 1;
		gbc_widthSpinner.gridy = 1;
		selectedDimensions.add(widthSpinner, gbc_widthSpinner);
		
		widthSpinner.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				selectedDisplay.setDisplaySize((int) widthSpinner.getValue(), (int) heightSpinner.getValue());
			}
		});
		
		JLabel lblHeight = new JLabel("Height:");
		GridBagConstraints gbc_lblHeight = new GridBagConstraints();
		gbc_lblHeight.anchor = GridBagConstraints.WEST;
		gbc_lblHeight.insets = new Insets(0, 0, 0, 5);
		gbc_lblHeight.gridx = 2;
		gbc_lblHeight.gridy = 1;
		selectedDimensions.add(lblHeight, gbc_lblHeight);
		
		heightSpinner = new JSpinner();
		heightSpinner.setModel(new SpinnerNumberModel(new Integer(30), new Integer(1), null, new Integer(1)));
		GridBagConstraints gbc_heightSpinner = new GridBagConstraints();
		gbc_heightSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_heightSpinner.gridx = 3;
		gbc_heightSpinner.gridy = 1;
		selectedDimensions.add(heightSpinner, gbc_heightSpinner);
		
		heightSpinner.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				selectedDisplay.setDisplaySize((int) widthSpinner.getValue(), (int) heightSpinner.getValue());
			}
		});
		
		JScrollPane selectedOutputScroller = new JScrollPane();
		selectedOutputScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		selectedInfo.add(selectedOutputScroller, BorderLayout.SOUTH);
		
		outputValues = new AnswerDisplay(InputsOutputs.outputs.length); //TODO hardcoded value
		selectedInfo.add(outputValues, BorderLayout.CENTER);
		
		JPanel selectedActions = new JPanel();
		FlowLayout fl_selectedActions = (FlowLayout) selectedActions.getLayout();
		fl_selectedActions.setAlignment(FlowLayout.TRAILING);
		sidebar.add(selectedActions, BorderLayout.SOUTH);
		
		detectLetters = new JButton("Enable letter detection");
		selectedActions.add(detectLetters);
		
		detectLetters.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (imageEditor.toggleAlterImage())
					detectLetters.setText("Disable letter detection");
				else
					detectLetters.setText("Enable letter detection");
			}
		});
		
		btnLoadImage = new JButton("Load image");
		selectedActions.add(btnLoadImage);
		
		btnLoadImage.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (jfc.showOpenDialog(ImageEditorPanel.this) == JFileChooser.APPROVE_OPTION)
				{
					try
					{
						imageEditor.setImage(ImageIO.read(jfc.getSelectedFile()));
					} catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		});
		
		btnAddSelected = new JButton("Add selected");
		selectedActions.add(btnAddSelected);
		
		btnAddSelected.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				/*String[] text = selectedOutput.getText().replaceAll("\\/\\/.*\\n", "\n").split(",");
				
				float[] parsed = new float[text.length];
				
				for (int i = 0; i < text.length; i++)
					parsed[i] = Float.parseFloat(text[i].trim());*/
				
				float[] parsed = outputValues.getAnswers();
				
				if (dataHandler != null)
				{
					float[][] output = ImageUtils.rescale(imageEditor.getSelected(), (int) widthSpinner.getValue(), (int) heightSpinner.getValue());
					float[] flattenedOutput = new float[output.length * output[0].length];
					for (int i = 0; i < output.length; i++)
						for (int j = 0; j < output[i].length; j++)
							flattenedOutput[i + j * output.length] = output[i][j];
					dataHandler.dataAdded(System.currentTimeMillis() + "", flattenedOutput, parsed, imageEditor.getSelectedImage());
				}
			}
		});
	}
	
	private DataAdder dataHandler;
	
	public void setDataAdder(DataAdder d)
	{
		dataHandler = d;
	}
	
	private JFileChooser jfc = new JFileChooser();
}
