
public class Autosave implements Runnable{
	
	Server server;
	
	public Autosave(Server server)
	{
		this.server = server;
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				Thread.sleep(300000);
				server.saveProgress();
			}
			catch(Exception ex)
			{
				System.out.println("Was unable to auto-save Space. ");
			}
		}
	}
}
