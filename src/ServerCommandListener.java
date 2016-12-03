import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Scanner;

public class ServerCommandListener implements Runnable, KeyListener
{
	SpaceStats spaceStats;
	Server server;
	Scanner sc;
	
	public ServerCommandListener(Server server)
	{
		this.spaceStats = server.getSpaceStats();
		this.server = server;
		
		sc = new Scanner(System.in);
		
		Thread commandListener = new Thread(this);
		commandListener.start();
	}
	
	public void run()
	{
		while(true)
		{
			String fullCommand = sc.nextLine();
			
			String[] commandList = fullCommand.split(" ");
			
			String args = "";
			
			for(int i = 1; i < commandList.length; i++)
			{
				args += commandList[i] + " ";
			}
			
			String firstCommand = commandList[0].toLowerCase();
			
			switch(firstCommand)
			{
			case "exit":
				for(int i = 0; i < spaceStats.playerList.length; i++)
				{
					try
					{
						server.printMessageToAll("Server stopped. Please restart client. ");
					}
					catch(Exception ex)
					{
						System.out.println("Problem stopping server. ");
					}
				}
				System.exit(0);
				break;
				
			case "print":
				for(int i = 0; i < spaceStats.playerList.length; i++)
				{
					try
					{
						server.printMessageToAll(args);
					}
					catch(Exception ex)
					{
						System.out.println("Could not write server message ");
					}
				}
				break;
			
			case "remove":
				try
				{
					if(spaceStats.playerList.length < 1)
					{
						throw new Exception();
					}
					for(int i = 0; i < spaceStats.playerList.length; i++)
					{
						if(spaceStats.getPlayer(commandList[1]) != null)
						{
							spaceStats.removePlayer(commandList[1]);
							
							server.printMessageToAll("Player " + commandList[1] + " was deleted from the server. ");
							
							spaceStats.getPlayer(commandList[1]).getClient().printMessage("Your player " + commandList[1] + " was deleted. Please restart the client. ");
							
							System.out.println("Player" + commandList[1] + "removed. ");
							
							server.saveProgress();
						}
						else
						{
							throw new Exception();
						}
					}
				}
				catch(Exception ex)
				{
					System.out.println("Player \"" + commandList[1] + "\" not found. ");
					ex.printStackTrace();
				}
				break;
				
			case "save":
				server.saveProgress();
				break;
				
			default:
				break;
			}
		}
	}

	public void keyPressed(KeyEvent arg0) 
	{
		int key = arg0.getKeyCode();
		
		if(key == KeyEvent.VK_ESCAPE)
		{
			server.saveProgress();
			System.exit(0);
		}
	}

	public void keyReleased(KeyEvent arg0) 
	{
	}

	public void keyTyped(KeyEvent arg0) 
	{
	}
}
