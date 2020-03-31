package ai.app.ui;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

public class ResultTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = -1529504612546189930L;
	
	private String[] outputs;

	private ArrayList<Boolean> correctL = new ArrayList<>();
	private ArrayList<Boolean> correctD = new ArrayList<>();
	private ArrayList<ImageIcon> thumbnail = new ArrayList<>();
	private ArrayList<String> name = new ArrayList<>();
	private ArrayList<float[]> output = new ArrayList<>();
	
	private int correctLAmt = 0;
	private int correctDAmt = 0;
	private int correctTotalAmt = 0;
	
	public ResultTableModel(String[] outputs)
	{
		this.outputs = outputs;
	}
	
	public void add(String name, ImageIcon thumbnail, float[] output, boolean correctL, boolean correctD)
	{
		this.correctL.add(correctL);
		this.correctD.add(correctD);
		this.thumbnail.add(thumbnail);
		this.name.add(name);
		this.output.add(output);
		if (correctL)
			correctLAmt++;
		if (correctD)
			correctDAmt++;
		if (correctL && correctD)
			correctTotalAmt++;
		System.out.println("Correct: (Letter: " + correctLAmt + "; Diacritic: " + correctDAmt + "; Both: " + correctTotalAmt + ")/" + this.name.size());
		fireTableRowsInserted(this.name.size() - 1, this.name.size() - 1);
	}
	
	@Override
	public int getColumnCount()
	{
		return 2 + outputs.length;
	}

	@Override
	public int getRowCount()
	{
		return name.size();
	}

	@Override
	public Object getValueAt(int arg0, int arg1)
	{
		if (arg0 >= getRowCount() || arg1 >= getColumnCount())
			return null;
		if (arg1 == -1)
			return correctL.get(arg0);
		if (arg1 == -2)
			return correctD.get(arg0);
		if (arg1 == 0)
			return thumbnail.get(arg0);
		else if (arg1 == 1)
			return name.get(arg0);
		else
			return output.get(arg0)[arg1 - 2];
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}
	
	@Override
	public String getColumnName(int column)
	{
		if (column >= getColumnCount())
			return "";
		if (column == 0)
			return "";
		else if (column == 1)
			return "Name";
		else
			return outputs[column - 2];
	}
	
	@Override
	public Class<?> getColumnClass(int column)
	{
		if (column == 0)
			return ImageIcon.class;
		else if (column == 1)
			return String.class;
		else
			return Float.class;
	}
}
