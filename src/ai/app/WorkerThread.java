package ai.app;

public class WorkerThread extends Thread
{
	private static long workers = 0;
	
	private boolean running = true;
	
	private Runnable task;
	private boolean working = false;
	
	public WorkerThread()
	{
		super("Worker thread " + (workers++));
	}
	
	public WorkerThread(String name)
	{
		super(name);
	}
	
	public boolean isWorking()
	{
		return working;
	}
	
	public void giveTask(Runnable task)
	{
		synchronized (this)
		{
			this.task = task;
			working = true;
			this.notifyAll();
		}
	}
	
	public void close()
	{
		synchronized (this)
		{
			running = false;
			task = null;
			this.notifyAll();
		}
	}
	
	@Override
	public void run()
	{
		synchronized (this)
		{
			while (running)
			{
				try
				{
					working = false;
					this.wait();
					if (task != null)
					{
						task.run();
						task = null;
					}
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
