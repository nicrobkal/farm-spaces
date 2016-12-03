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
			
			System.out.println("What is the name of your new Farm Space character? ");
			playerName = sc.nextLine();
			
			clientCommandListener = new ClientCommandListener(this); 
			
			try
			{
				if(spaceStats.getPlayer(playerName) != null)
				{
					localPlayer = spaceStats.getPlayer(playerName);
					localPlayer.setClient(this);
					String startMessage = localPlayer.toString() + " has re-joined!! ";
					printMessageToAll(startMessage);
					
					clientOutput.sendEnvelope(new Envelope("Player " + localPlayer.toString() + " has re-joined! ", "String"));
				}
				else
				{
					throw new Exception();
				}
			}
			catch(Exception ex)
			{
				localPlayer = new Player(playerName, spaceStats.startBuilding, spaceStats.playerList.length, this);
				
				localPlayer.setClient(this);
				
				spaceStats.addPlayer(localPlayer);
				
				String startMessage = localPlayer.toString() + " has joined the " + spaceStats.spaceName + " for the first time!! ";
				clientOutput.sendEnvelope(new Envelope(startMessage, "String"));
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
