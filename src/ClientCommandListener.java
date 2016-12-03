import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Scanner;

public class ClientCommandListener implements Runnable, KeyListener
{
	Client client;
	Scanner sc;
	
	public ClientCommandListener(Client client)
	{
		this.client = client;
		
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
			case "/t":
				client.getClientOutput().sendEnvelope(new Envelope(client.getClientPlayer().toString() + ": " + args, "String"));
				break;
				
			case "/exit":
				System.exit(0);
				break;
			}
		}
	}
	
	public void keyPressed(KeyEvent arg0) 
	{
		int key = arg0.getKeyCode();
		
		if(key == KeyEvent.VK_ESCAPE)
		{
			client.getClientOutput().sendEnvelope(new Envelope(client.getClientPlayer().toString() + " has left the server! ", "String"));
			//client.getClientOutput().sendEnvelope(new Envelope(client.getClientPlayer().toString() + " has left the server! " ,"String"));
			client.getClientOutput().sendEnvelope(new Envelope(client.getSpaceStats(), "SpaceStats"));
		}
	}

	public void keyReleased(KeyEvent arg0) 
	{
	}

	public void keyTyped(KeyEvent arg0) 
	{
		
	}
}
