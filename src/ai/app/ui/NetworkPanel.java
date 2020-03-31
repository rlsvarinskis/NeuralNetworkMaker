package ai.app.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import ai.app.ui.network.NetworkDisplay;
import ai.app.data.InputsOutputs;
import ai.app.ui.network.AverageErrorTracker;
import ai.app.ui.network.DataAdder;
import ai.app.ui.network.ImageEditorPanel;

public class NetworkPanel extends JPanel
{
	private static final long serialVersionUID = -5072418550990628048L;
	
	private NetworkDisplay networkDisplay;
	private JTable results;
	private AverageErrorTracker averageErrorHistory;
	private ImageEditorPanel imageEditor;
	
	private ResultTableModel resultModel;
	
	private JLabel errorLabel = new JLabel("0");
	
	private JButton networkReinitialize = new JButton("Reinitialize Randomely");
	private JButton networkRun = new JButton("Run");
	
	public NetworkPanel(int... layers)
	{
		networkDisplay = new NetworkDisplay(layers);
		networkDisplay.setBackground(new Color(0, 0, 0));
		networkDisplay.setForeground(Color.LIGHT_GRAY);
		
		results = new JTable(resultModel = new ResultTableModel(InputsOutputs.labels))
		{
			private static final long serialVersionUID = 4333408224472082437L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{
				Component sup = super.prepareRenderer(renderer, row, column);
				
				Object value = getModel().getValueAt(row, column);
				Object correct = getModel().getValueAt(row, -1);
				Object correct2 = getModel().getValueAt(row, -2);
				
				if (correct instanceof Boolean && correct2 instanceof Boolean)
				{
					if ((boolean) correct && (boolean) correct2)
					{
						sup.setBackground(new Color(63, 192, 63));
					} else if ((boolean) correct)
					{
						sup.setBackground(new Color(63, 63, 192));
					} else if ((boolean) correct2)
					{
						sup.setBackground(new Color(192, 63, 63));
					} else
					{
						sup.setBackground(new Color(255, 255, 255));
					}
				} else
				{
					sup.setBackground(new Color(255, 255, 255));
				}
				
				if (value instanceof Float)
				{
					float f = 1 - (float) value;
					float r = 1 - Math.round(f);
					if (isRowSelected(row))
						sup.setBackground(new Color(f * 0.5f, f * 0.5f, f));
					else
						sup.setBackground(new Color(f, f, f));
					sup.setForeground(new Color(r, r, r));
				}
				
				return sup;
			}
		};
		results.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		results.setRowHeight(30);
		results.getColumnModel().getColumn(0).setMinWidth(30);
		results.getColumnModel().getColumn(0).setMaxWidth(30);
		results.getColumnModel().getColumn(0).setPreferredWidth(30);
		results.getColumnModel().getColumn(1).setMinWidth(50);
		
		for (int i = 0; i < layers[layers.length - 1]; i++)
			results.getColumnModel().getColumn(i + 2).setPreferredWidth(50);
		
		averageErrorHistory = new AverageErrorTracker();
		
		imageEditor = new ImageEditorPanel();
		
		initializeComponents(layers);
	}
	
	private void initializeComponents(int[] layers)
	{
		setLayout(new BorderLayout(0, 0));
		
		add(initializeNetworkInfo(), BorderLayout.SOUTH);
		add(initializeNetworkDisplays(layers), BorderLayout.CENTER);
	}
	
	private JTabbedPane initializeNetworkDisplays(int[] layers)
	{
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		JScrollPane tableContainer = new JScrollPane(results, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		results.setFillsViewportHeight(true);
		
		tabbedPane.addTab("Network", networkDisplay);
		tabbedPane.addTab("Results", tableContainer);
		tabbedPane.addTab("Training progress", averageErrorHistory);
		tabbedPane.addTab("Image importer", imageEditor);
		
		return tabbedPane;
	}
	
	private JPanel initializeNetworkInfo()
	{
		JPanel networkInfo = new JPanel();
		networkInfo.setLayout(new BorderLayout(0, 0));
		
		networkInfo.add(initializeNetworkInfoAverageErrorPanel(), BorderLayout.WEST);
		networkInfo.add(initializeNetworkInfoActionsPanel(), BorderLayout.EAST);
		
		return networkInfo;
	}
	
	private JPanel initializeNetworkInfoAverageErrorPanel()
	{
		JPanel networkAverageError = new JPanel();
		
		networkAverageError.add(new JLabel("Average error:"));
		networkAverageError.add(errorLabel);
		
		return networkAverageError;
	}
	
	private JPanel initializeNetworkInfoActionsPanel()
	{
		JPanel networkActions = new JPanel();
		FlowLayout layout = (FlowLayout) networkActions.getLayout();
		layout.setVgap(2);
		layout.setHgap(2);
		
		networkActions.add(networkReinitialize);
		networkActions.add(networkRun);
		
		return networkActions;
	}
	
	public void setActionsEnabled(boolean enabled)
	{
		networkReinitialize.setEnabled(enabled);
		networkRun.setEnabled(enabled);
	}
	
	public void setReinitializeAction(ActionListener l)
	{
		networkReinitialize.addActionListener(l);
	}
	
	public void setRunAction(ActionListener l)
	{
		networkRun.addActionListener(l);
	}
	
	public void setDataAdder(DataAdder d)
	{
		imageEditor.setDataAdder(d);
	}
	
	public void addErrorPoint(int batches, float error)
	{
		averageErrorHistory.addPoint(averageErrorHistory.getMaxAmount() + batches, error);
	}
	
	public void setError(float error)
	{
		errorLabel.setText(Float.toString(error));
	}
	
	public void result(String name, ImageIcon thumbnail, float[] output, boolean correct, boolean correct2)
	{
		resultModel.add(name, thumbnail, output, correct, correct2);
	}
	
	public void layerValues(float[] values, int layer)
	{
		networkDisplay.setValue(values, layer);
	}
	
	public void transLayerValues(float[][] values, int layer)
	{
		networkDisplay.setWeights(values, layer);
	}
	
	public void updateNetworkDisplay()
	{
		networkDisplay.update();
	}
	
	public void close()
	{
		networkDisplay.close();
	}
}
