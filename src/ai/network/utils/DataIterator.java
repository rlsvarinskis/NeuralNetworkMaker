package ai.network.utils;

import java.util.Iterator;

public class DataIterator implements Iterator<Data>
{
	private Data[] data;
	private int index = 0;
	private int to = 0;
	
	public DataIterator(Data[] data)
	{
		this.data = data;
	}
	
	public void setIndex(int index, int to)
	{
		if (to > data.length)
			to = data.length;
		
		this.index = index;
		this.to = to;
	}
	
	public int getFrom()
	{
		return index;
	}
	
	public int getTo()
	{
		return to;
	}
	
	public int length()
	{
		return data.length;
	}
	
	public void shuffle()
	{
		shuffle(data);
	}

	@Override
	public boolean hasNext()
	{
		return index < to;
	}

	@Override
	public Data next()
	{
		return data[index++];
	}
	
	public static <T> T[] shuffle(T[] data)
	{
		for (int i = 0; i < data.length - 1; i++)
			swap(data, i, i + (int) (Math.random() * (data.length - i)));
		
		return data;
	}
	
	public static <T> void swap(T[] data, int a, int b)
	{
		T temp = data[a];
		
		data[a] = data[b];
		data[b] = temp;
	}
}
