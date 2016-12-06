import java.applet.Applet;
import java.awt.event.KeyListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Serializable
{
	static Socket socket;
	private static Scanner sc;
	
	private String playerName;
	private Player localPlayer;
	
	private static ClientOutput clientOutput;
	private static ClientCommandListener clientCommandListener;
	
	private SpaceStats spaceStats;
	
	public Client(Socket socket)
	{
		try
		{
			this.socket = socket;
			
			sc = new Scanner(System.in);
			
			clientOutput = new ClientOutput(socket, this);
			
			
			
			
			boolean playerEntered = false;
			
			while(!playerEntered)
			{
				System.out.println("What is the name of your Farm Space character? ");
				playerName = sc.nextLine();
				
				try
				{
					if(spaceStats.getPlayer(playerName) != null && !spaceStats.clientOnline(playerName))
					{
						clientCommandListener = new ClientCommandListener(this); 
						localPlayer = spaceStats.getPlayer(playerName);
						localPlayer.setClient(this);
						
						printMessageToAll("Player " + localPlayer.toString() + " has re-joined! ");
						playerEntered = true;
					}
					else
					{
						if(spaceStats.clientOnline(playerName))
						{
							System.out.println("Player is already online! ");
						}
						else
						{
							throw new Exception();
						}
					}
				}
				catch(Exception ex)
				{
					clientCommandListener = new ClientCommandListener(this); 
					localPlayer = new Player(playerName, spaceStats.startBuilding, spaceStats.playerList.length, this);
					
					localPlayer.setClient(this);
					
					spaceStats.addPlayer(localPlayer);
					
					String startMessage = localPlayer.toString() + " has joined the " + spaceStats.spaceName + " for the first time!! ";
					clientOutput.sendEnvelope(new Envelope(startMessage, "String"));
					
					playerEntered = true;
				}
			}
			
			
			clientOutput.sendEnvelope(new Envelope(spaceStats, "SpaceStats"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void updateStats(SpaceStats newStats)
	{
		spaceStats = newStats;
	}
	
	public Player getClientPlayer()
	{
		return localPlayer;
	}
	
	public ClientOutput getClientOutput()
	{
		return clientOutput;
	}
	
	public void printMessage(String message)
	{
		System.out.println(message);
	}
	
	public void printMessageToAll(String message)
	{
		clientOutput.sendEnvelope(new Envelope(message, "String"));
	}
	
	public SpaceStats getSpaceStats()
	{
		return spaceStats;
	}
}
