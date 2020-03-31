package ai.network.utils;

public class Data
{
	private float[] inputs;
	private float[] outputs;
	
	public Data(float[] inputs, float[] outputs)
	{
		this.inputs = inputs;
		this.outputs = outputs;
	}
	
	public float[] getInputs()
	{
		return inputs;
	}
	
	public float[] getOutputs()
	{
		return outputs;
	}
	
	public float compare(float[] answers)
	{
		float total = 0;
		
		for (int i = 0; i < answers.length; i++)
			total += Math.pow(outputs[i] - answers[i], 2);
		
		return total * total;
	}
}
