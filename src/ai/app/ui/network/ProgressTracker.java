package ai.app.ui.network;

public interface ProgressTracker
{
	public void progress();
	public void progress(int batches, float successRate);
}
