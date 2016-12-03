import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientOutput implements Runnable
{
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Client client;

	public ClientOutput(Socket socket, Client client) 
	{
		try
		{
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
		}
		catch(Exception ex)
		{
			System.out.println("CRITICAL: Could not create connection between Client and Server. ");
		}
		
		this.client = client;
		
		Thread statsListener = new Thread(this);
		statsListener.start();
		
		//Checks for a change in the spaceStats Object shared between the Server and the Client
	}
	
	//Checks for messages sent by the Server
	public void run() 
	{	
		try
		{
			while(true)
			{
				try 
				{
					//Update Space (Seen by current client)
					Envelope envelope = (Envelope)in.readObject();
					
					if(envelope.getType().equals("String"))
					{
						String message = (String)envelope.getContents(String.class);
						System.out.println(message);
					}
					else if(envelope.getType().equals("SpaceStats"))
					{
						SpaceStats spaceStats = (SpaceStats)envelope.getContents(SpaceStats.class);
						client.updateStats(spaceStats);
					}
				}
				catch (Exception ex) 
				{
					throw new Exception();
					//System.exit(0);
				}
			}
		}
		catch(Exception ex)
		{
			System.out.println("Server stopped. Please restart client. ");
		}
	}
	
	public void sendEnvelope(Envelope envelope)
	{
		try
		{
			out.writeObject(envelope);
			out.flush();
		}
		catch(Exception ex)
		{
			System.out.println("Failed to send Envelope to Server. ");
		}
	}
}