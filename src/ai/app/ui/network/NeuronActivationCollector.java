package ai.app.ui.network;

public interface NeuronActivationCollector
{
	public void layerValues(float[] layer);
	public void transLayerValues(float[][] weights);
}
