import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class Server
{	
	static ServerSocket serverSocket;
	static Socket socket;
	
	static ServerOutput[] connectedClientsList = new ServerOutput[0];
	
	private static SpaceStats spaceStats;
	
	public static void main(String[] args)
	{
		PipedInputStream inPipe = new PipedInputStream();
	    PipedInputStream outPipe = new PipedInputStream();

	    // 2. set the System.in and System.out streams
	    System.setIn(inPipe);
	    
	    try
	    {
	    	PrintStream ps = new PrintStream(new PipedOutputStream(outPipe), true);
	    	System.setOut(ps);
	    	System.setErr(ps);

		    PrintWriter inWriter = new PrintWriter(new PipedOutputStream(inPipe), true);
		    
		    // 3. create the gui 
		    JFrame frame = new JFrame("\"Spaces\" Server");
		    frame.add(console(outPipe, inWriter));
		    frame.setSize(600, 400);
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    frame.setVisible(true);
	    }
	    catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    }
	    
	    try
		{
	    	String currDir = System.getProperty("user.dir");
	    	FileInputStream fileIn = new FileInputStream(currDir + "\\tmp\\SpaceStats.ser");
			ObjectInputStream ois = new ObjectInputStream(fileIn);
			spaceStats = (SpaceStats) ois.readObject();
		}
		catch(Exception ex)
		{
			System.out.println("Starting new game. ");
			spaceStats = new SpaceStats("Farm Space");
			saveProgress();
		}
	    
		try
		{
			System.out.println("Starting Server.");
			serverSocket=new ServerSocket(7777);
			System.out.println("Server Started.");
			
			//System.out.println(InetAddress.getLocalHost().getHostAddress());
		}
		catch(Exception ex)
		{
			System.out.println("Server failed to start. Maybe it's already running? ");
		}
		
		new Server(spaceStats, serverSocket);
	}
	
	public static JTextArea console(final InputStream out, final PrintWriter in) {
	    final JTextArea area = new JTextArea();

	    // handle "System.out"
	    new SwingWorker<Void, String>() {
	        @Override protected Void doInBackground() throws Exception {
	            Scanner s = new Scanner(out);
	            while (s.hasNextLine()) publish(s.nextLine() + "\n");
	            return null;
	        }
	        @Override protected void process(List<String> chunks) {
	            for (String line : chunks) area.append(line);
	        }
	    }.execute();

	    // handle "System.in"
	    area.addKeyListener(new KeyAdapter() {
	        private StringBuffer line = new StringBuffer();
	        @Override public void keyTyped(KeyEvent e) {
	            char c = e.getKeyChar();
	            if (c == KeyEvent.VK_ENTER) {
	                in.println(line);
	                line.setLength(0); 
	            } else if (c == KeyEvent.VK_BACK_SPACE) { 
	                line.setLength(line.length() - 1); 
	            } else if (!Character.isISOControl(c)) {
	                line.append(e.getKeyChar());
	            } else if (c == KeyEvent.VK_ESCAPE) {
	                saveProgress();
	                printMessageToAll("Server stopped. Please restart client. ");
	                System.exit(0);
	            }
	        }
	    });

	    return area;
	}

	public Server(SpaceStats spaceStats, ServerSocket serverSocket) 
	{
		this.spaceStats = spaceStats;
		
		Autosave autosave = new Autosave(this);
		
		Thread autosaveThread = new Thread(autosave);
		
		ServerCommandListener serverCommands = new ServerCommandListener(this);
		
		while(true)
		{
			try
			{
				socket = serverSocket.accept();
				
				System.out.println("Connecting from: " + socket.getInetAddress());
				
				System.out.println("Connected: " + socket.getInetAddress() + " (SUCCESS). ");
				
				createConnection(socket);
			}
			catch(Exception ex)
			{
				System.out.println("Connection from: " + socket.getInetAddress() + " FAILED. ");
			}
		}
	}
	
	public void createConnection(Socket socket)
	{
		ServerOutput serverOutput = new ServerOutput(socket, this);
		addClient(serverOutput);
		//serverOutput.sendEnvelope(new Envelope(spaceStats, "SpaceStats"));
		printMessageToAll("Allo? ");
		updateStats(spaceStats);
	}
	
	public static void saveProgress()
	{
		String currDir = System.getProperty("user.dir");
		 try {
	         FileOutputStream fileOut =
	         new FileOutputStream(currDir + "\\tmp\\SpaceStats.ser");
	         ObjectOutputStream outS = new ObjectOutputStream(fileOut);
	         outS.writeObject(spaceStats);
	         outS.close();
	         fileOut.close();
	         System.out.println("Game saved at " + currDir + "\\tmp\\SpaceStats.ser");
	         
	         
	      }catch(Exception ex) {
	         ex.printStackTrace();
	      }
	}
	
	public SpaceStats getSpaceStats()
	{
		return spaceStats;
	}
	
	public void updateStats(SpaceStats spaceStats)
	{
		this.spaceStats = spaceStats;
		for(int i = 0; i < connectedClientsList.length; i++)
		{
			try
			{
				//System.out.println("Mehr: " + playerList[i].getClient().getClientPlayer().toString());
				connectedClientsList[i].sendEnvelope(new Envelope(spaceStats, "SpaceStats"));
			}
			catch(Exception ex)
			{
				//System.out.println("Player list length: " + playerList.length);
				//System.out.println("Not all players online. ");
				//ex.printStackTrace();
			}
		}
	}
	
	public static void printMessageToAll(String message)
	{
		System.out.println(message);
		for(int i = 0; i < connectedClientsList.length; i++)
		{
			try
			{
				connectedClientsList[i].sendEnvelope(new Envelope(message, "String"));
			}
			catch(Exception ex)
			{
				//System.out.println("Not all players online. ");
			}
		}
	}
	
	public void addClient(ServerOutput newServerOutput)
	{
		ServerOutput[] tempOutputs = new ServerOutput[connectedClientsList.length + 1];
		
		for(int i = 0; i < connectedClientsList.length; i++)
		{
			tempOutputs[i] = connectedClientsList[i];
		}
		
		tempOutputs[tempOutputs.length - 1] = newServerOutput;
		
		connectedClientsList = tempOutputs;
	}
	
	public void updateClientList()
	{
		for(int i = 0; i < connectedClientsList.length; i++)
		{
			try
			{
				if(connectedClientsList[i].getSocket().toString() == null)
				{
					removeClient(i);
				}
			}
			catch(Exception ex)
			{
				removeClient(i);
			}
		}
	}
	
	public void removeClient(int index)
	{
		for(int i = 0; i < connectedClientsList.length; i++)
		{
			try
			{
				if(i == index)
				{
					connectedClientsList[i] = null;
					ServerOutput[] tempList = new ServerOutput[connectedClientsList.length - 1];
					int counter = 0;
					for(int j = 0; j < connectedClientsList.length - 1; j++)
					{
						if(connectedClientsList[j] != null)
						{
							tempList[counter] = connectedClientsList[j];
							counter++;
						}
						
						break;
					}
					connectedClientsList = tempList;
					break;
				}
			}
			catch(Exception ex)
			{
				connectedClientsList[i] = null;
				ServerOutput[] tempList = new ServerOutput[connectedClientsList.length - 1];
				int counter = 0;
				for(int j = 0; j < connectedClientsList.length - 1; j++)
				{
					if(connectedClientsList[j] != null)
					{
						tempList[counter] = connectedClientsList[j];
						counter++;
					}
					
					break;
				}
				connectedClientsList = tempList;
				break;
			}
		}
	}
}
