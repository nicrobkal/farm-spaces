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
			
			String firstCommand = commandList[0];
			
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
				try
				{
					server.printMessageToAll("SERVER: " + args);
				}
				catch(Exception ex)
				{
					System.out.println("Could not write server message ");
				}
				break;
				
			case "save":
				server.saveProgress();
				break;
				
			case "help":
				System.out.println("\nCommand List:");
				
				System.out.println("exit Saves the server and closes the game. ");
				System.out.println("print (Message) Sends a message to the whole server. ");
				System.out.println("save Saves the game. ");
				System.out.println();
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
