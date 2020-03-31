package ai.app.ui.network;

import java.awt.image.BufferedImage;

public interface DataAdder
{
	public void dataAdded(String name, float[] inputs, float[] outputs, BufferedImage icon);
}
