package ai.network.types;

import java.util.Random;

import ai.app.ui.network.NeuronActivationCollector;
import ai.app.ui.network.ProgressTracker;
import ai.network.utils.Data;
import ai.network.utils.DataIterator;
import ai.network.utils.Matrix;
import ai.network.utils.Vector;

public class ANN extends Network
{
	private float[][][] weight;
	private float[][] bias;
	
	public ANN(int inputs, int outputs, int... hidden)
	{
		int[] total = new int[hidden.length + 2];
		
		total[0] = inputs;
		total[total.length - 1] = outputs;
		
		System.arraycopy(hidden, 0, total, 1, hidden.length);
		
		weight = new float[1 + hidden.length][][];
		weightMomentum = new float[weight.length][][];
		
		bias = new float[1 + hidden.length][];
		bias[hidden.length] = new float[outputs];
		biasMomentum = new float[bias.length][];
		biasMomentum[hidden.length] = new float[outputs];
		
		for (int i = 0; i < weight.length; i++)
		{
			weight[i] = new float[total[i + 1]][total[i]];
			weightMomentum[i] = new float[total[i + 1]][total[i]];
		}
		
		for (int i = 0; i < hidden.length; i++)
		{
			bias[i] = new float[hidden[i]];
			biasMomentum[i] = new float[hidden[i]];
		}
	}
	
	public float[][][] getWeights()
	{
		return weight;
	}
	
	public float[][] getBiases()
	{
		return bias;
	}
	
	private float sigmoid(float x)
	{
		return (float) (1f / (1f + Math.exp(-x)));
	}
	
	private float[] sigmoid(float[] x)
	{
		for (int i = 0; i < x.length; i++)
			x[i] = sigmoid(x[i]);
		
		return x;
	}
	
	private float sigmoidDerivative(float x)
	{
		float s = sigmoid(x);
		return (1 - s) * s;
	}
	
	private float[] sigmoidDerivative(float[] x)
	{
		for (int i = 0; i < x.length; i++)
			x[i] = sigmoidDerivative(x[i]);
		
		return x;
	}
	
	private float[] cp(float[] x)
	{
		float[] y = new float[x.length];
		System.arraycopy(x, 0, y, 0, x.length);
		return y;
	}
	
	public void initializeRandomly(long seed)
	{
		Random r = new Random(seed);
		
		for (int i = 0; i < bias.length; i++)
			for (int j = 0; j < bias[i].length; j++)
				bias[i][j] = (float) r.nextGaussian() / 5f * 0;
		
		for (int i = 0; i < weight.length; i++)
			for (int j = 0; j < weight[i].length; j++)
				for (int k = 0; k < weight[i][j].length; k++)
					weight[i][j][k] = (float) r.nextGaussian() / 5f;
	}
	
	public float[] run(float[] inputs)
	{
		for (int i = 0; i < bias.length; i++)
			inputs = sigmoid(Vector.add(Matrix.multiplyTranspose(weight[i], inputs), bias[i]));
		
		return inputs;
	}
	
	public float[] run(float[] inputs, NeuronActivationCollector dataCollector)
	{
		dataCollector.layerValues(inputs);
		for (int i = 0; i < bias.length; i++)
		{
			float[][] activatedWeights = new float[weight[i].length][weight[i][0].length];
			float[] sumWeights = new float[weight[i].length];
			
			for (int j = 0; j < weight[i].length; j++)
			{
				for (int k = 0; k < weight[i][j].length; k++)
				{
					activatedWeights[j][k] = weight[i][j][k] * inputs[k];
					sumWeights[j] += activatedWeights[j][k];
				}
			}
			dataCollector.transLayerValues(activatedWeights);
			
			inputs = sigmoid(Vector.add(sumWeights, bias[i]));
			dataCollector.layerValues(inputs);
		}
		
		return inputs;
	}
	
	public void train(Data[] trainingSet, int repeats, int miniBatchSize, float learningRate, float momentum, ProgressTracker tracker)
	{
		DataIterator data = new DataIterator(trainingSet);
		for (int i = 0; i < repeats; i++)
		{
			data.shuffle();
			
			for (int j = 0; j < data.length(); j += miniBatchSize)
			{
				data.setIndex(j, j + miniBatchSize);
				gradientDescent(data, learningRate, momentum, tracker);
			}
			
			float cost = 0;
			
			for (int j = 0; j < trainingSet.length; j++)
				cost += Vector.distance(Vector.subtract(run(trainingSet[j].getInputs()), trainingSet[j].getOutputs()));
			
			tracker.progress(trainingSet.length, cost / trainingSet.length);
		}
	}
	
	float[][] biasMomentum;
	float[][][] weightMomentum;
	
	public void gradientDescent(DataIterator data, float learningRate, float momentum, ProgressTracker tracker)
	{
		for (int i = 0; i < bias.length; i++)
			for (int j = 0; j < bias[i].length; j++)
				biasMomentum[i][j] *= momentum;
		
		for (int i = 0; i < weight.length; i++)
			for (int j = 0; j < weight[i].length; j++)
				for (int k = 0; k < weight[i][j].length; k++)
					weightMomentum[i][j][k] *= momentum;
		
		while (data.hasNext())
		{
			Delta d = getChangeGradients(data.next());
			
			for (int i = 0; i < bias.length; i++)
				for (int j = 0; j < bias[i].length; j++)
					biasMomentum[i][j] += d.bias[i][j];
			
			for (int i = 0; i < weight.length; i++)
				for (int j = 0; j < weight[i].length; j++)
					for (int k = 0; k < weight[i][j].length; k++)
						weightMomentum[i][j][k] += d.weight[i][j][k];
			
			tracker.progress();
		}
		
		/*float totalBias = 0;
		float totalWeight = 0;

		for (int i = 0; i < bias.length; i++)
			for (int j = 0; j < bias[i].length; j++)
				totalBias += biasMomentum[i][j];
		
		for (int i = 0; i < weight.length; i++)
			for (int j = 0; j < weight[i].length; j++)
				for (int k = 0; k < weight[i][j].length; k++)
					totalWeight += weightMomentum[i][j][k];
		
		System.out.println("Bias: " + totalBias);
		System.out.println("Weight: " + totalWeight);*/
		
		for (int i = 0; i < bias.length; i++)
			for (int j = 0; j < bias[i].length; j++)
				bias[i][j] -= biasMomentum[i][j] * learningRate / data.length();
		
		for (int i = 0; i < weight.length; i++)
			for (int j = 0; j < weight[i].length; j++)
				for (int k = 0; k < weight[i][j].length; k++)
					weight[i][j][k] -= weightMomentum[i][j][k] * learningRate / data.length();
	}
	
	public Delta getChangeGradients(Data data)
	{
		Delta d = new Delta();
		d.bias = new float[bias.length][];
		d.weight = new float[weight.length][][];
		
		float[][] sums = new float[bias.length][];
		float[][] outputs = new float[bias.length + 1][];
		outputs[0] = round(data.getInputs());
		
		for (int i = 0; i < bias.length; i++)
		{
			sums[i] = Vector.add(Matrix.multiplyTranspose(weight[i], outputs[i]), bias[i]);
			outputs[i + 1] = sigmoid(cp(sums[i]));
		}

		d.bias[d.bias.length - 1] = Vector.multiply(Vector.subtract(outputs[outputs.length - 1], data.getOutputs()), sigmoidDerivative(sums[bias.length - 1]));
		d.weight[d.weight.length - 1] = Vector.multiplyTranspose(outputs[outputs.length - 2], d.bias[d.bias.length - 1]);
		
		for (int i = 2; i <= weight.length; i++)
		{
			d.bias[d.bias.length - i] = Vector.multiply(Matrix.multiply(d.bias[d.bias.length - i + 1], weight[weight.length - i + 1]), sigmoidDerivative(sums[sums.length - i]));
			d.weight[d.weight.length - i] = Vector.multiplyTranspose(outputs[outputs.length - i - 1], d.bias[d.bias.length - i]);
		}
		
		return d;
	}
	
	public static float[] round(float[] inputs)
	{
		float[] output = new float[inputs.length];
		for (int i = 0; i < inputs.length; i++)
			output[i] = Math.round(inputs[i]);
		
		return output;
	}
	
	private static class Delta
	{
		float[][] bias;
		float[][][] weight;
	}
}
