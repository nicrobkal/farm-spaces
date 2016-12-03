import java.io.*;
import java.net.*;

public class ServerTest {
	
	private static final int MAX_PLAYERS = 5;
	
	static ServerSocket serverSocket;
	static Socket socket;
	static DataOutputStream out;
	static Player[] players = new Player[MAX_PLAYERS];
	static DataInputStream in;
	
	public static void main(String[] args) throws Exception 
	{
		System.out.println("Starting Server.");
		serverSocket=new ServerSocket(7777);
		System.out.println("Server Started.");
		
		while(true)
		{
			socket = serverSocket.accept();
			
			for (int i = 0; i < MAX_PLAYERS; i++)
			{
				if (players[i] == null) 
				{
					System.out.println("Connection from: " + socket.getInetAddress());
					out = new DataOutputStream(socket.getOutputStream());
					in = new DataInputStream(socket.getInputStream());
					//players[i] = new Player(out, in, players, i);
					//Thread thread = new Thread(players[i]);
					//thread.start();
					break;
				}
			}
		}
	}
}

/*class Player implements Runnable
{
	DataOutputStream out;
	DataInputStream in;
	Player[] players;
	int playerId;
	int playerIdIn;
	int xIn;
	int yIn;
	
	public Player(DataOutputStream out, DataInputStream in, Player[] players, int playerId)
	{
		this.players = new Player[players.length];
		
		this.out = out;
		this.in = in;
		this.players = players;
		this.playerId = playerId;
	}
	
	public void run() 
	{
		try
		{
			out.writeInt(playerId);
		}
		catch(Exception ex)
		{
			System.out.println("Failed to send User ID. ");
		}
		
		while(true) 
		{
			try 
			{
				playerIdIn = in.readInt();
				xIn = in.readInt();
				yIn = in.readInt();
				
				for (int i = 0;i<10;i++) 
				{
					if(players[i] != null) 
					{
						players[i].out.writeInt(playerIdIn);
						players[i].out.writeInt(xIn);
						players[i].out.writeInt(yIn);
					}
				}
				
			} 
			catch (IOException e) 
			{
				players[playerId] = null;
				break;
			}
		}
	}
}*/
