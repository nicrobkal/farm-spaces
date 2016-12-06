import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerOutput implements Runnable
{
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	private Socket socket;
	
	Server server;

	public ServerOutput(Socket socket, Server server) 
	{
		try
		{
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
			
			this.socket = socket;
		}
		catch(Exception ex)
		{
			System.out.println("CRITICAL: Could not create connection between Client and Server. ");
		}
		
		this.server = server;
		
		Thread envelopeListenerThread = new Thread(this);
		envelopeListenerThread.start();
	}
	
	public void run() 
	{	
		
			while(true)
			{
				//Update Space (Seen by current client)
				
				try 
				{
					Envelope envelope = (Envelope)in.readObject();
					if(envelope.getType().equals("String"))
					{
						String message = (String)envelope.getContents(String.class);
						
						server.printMessageToAll(message);
					}
					else if(envelope.getType().equals("SpaceStats"))
					{
						SpaceStats spaceStats = (SpaceStats)envelope.getContents(SpaceStats.class);
						server.updateClientList();
						server.updateStats(spaceStats);
						server.saveProgress();
					}
				}
				catch (Exception ex) 
				{
					//System.out.println("Server fried. ");
					//ex.printStackTrace();
					
					server.updateClientList();
					//server.updateStats(spaceStats);
					
					
					//System.exit(0);
					//ex.printStackTrace();
				}
				
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
			//System.out.println("Could not send Envelope to Client. ");
		}
	}
	
	public ObjectOutputStream getOutputStream()
	{
		return out;
	}
	
	public ObjectInputStream getInputStream()
	{
		return in;
	}
	
	public Socket getSocket()
	{
		return socket;
	}
}
