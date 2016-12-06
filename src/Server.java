import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class Server
{	
	static ServerSocket serverSocket;
	static Socket socket;
	
	static ServerOutput[] connectedClientsList = new ServerOutput[0];
	
	private static SpaceStats spaceStats;
	
	private static Building townHall = new Building("Town Hall", "The central hub of town where you can perform various official matters. ");
	private static Building powerPlant = new Building("Power Plant", "The power plant of the town, where all the power is generated. ");
	private static Building openFields = new Building("Open Fields", "The open fields of town, where crops are grown and animals are raised. ");
	private static Building warehouse = new Building("Warehouse", "The storage warehouse in town, where all of the town's resources are kept. ");
	private static Building mine = new Building("Mine", "The abundant mine in town, where people go to mine, and collect natural resources. ");
	private static Building fort = new Building("Fort", "The ancient fort in town occupied by monsters, and a good place to collect loot. ");
	
	private static Skill farming = new Skill("Farming", "The ability to grow higher quality crops, and raise animals to make better products. ", openFields);
	private static Skill thievery = new Skill("Thievery", "The ability to steal without being caught, and pick locks of higher calibre. ", warehouse);
	private static Skill charisma = new Skill("Charisma", "The ability to convince others to see your point of view, and have them like you more. ", townHall);
	private static Skill combat = new Skill("Combat", "The ability to defend yourself as well as deal damage in a fight. ", fort);
	private static Skill mining = new Skill("Mining", "The ability to find better ore and gems and craft better ingots. ", mine);
	private static Skill computing = new Skill("Computing", "The ability to hack terminals and take their information. ", powerPlant);
	
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
		    frame.getContentPane().setLayout(new BorderLayout());
		    frame.setPreferredSize(new Dimension(800, 600));
		    
		    JPanel inputPanel = new JPanel(new BorderLayout());
		    
	        inputPanel.setPreferredSize(new Dimension(800, 50));
	        
	        inputPanel.add(new JScrollPane(inputConsole(inWriter)));
	        
	        JPanel outputPanel = new JPanel(new BorderLayout());
	        
	        outputPanel.add(new JScrollPane(outputConsole(outPipe)));
	        
	        frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);
	        frame.getContentPane().add(outputPanel, BorderLayout.CENTER);
	        
	        frame.pack();
	        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
			
			spaceStats.addSkill(farming);
			spaceStats.addSkill(thievery);
			spaceStats.addSkill(charisma);
			spaceStats.addSkill(combat);
			spaceStats.addSkill(mining);
			spaceStats.addSkill(computing);
			
			saveProgress();
		}
	    
		try
		{
			System.out.println("Starting Server.");
			serverSocket=new ServerSocket(7777);
			System.out.println("Server Started.");
		}
		catch(Exception ex)
		{
			System.out.println("Server failed to start. Maybe it's already running? ");
		}
		
		new Server(spaceStats, serverSocket);
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
				//System.out.println("Connection from: " + socket.getInetAddress() + " FAILED. ");
			}
		}
	}
	
	public void createConnection(Socket socket)
	{
		ServerOutput serverOutput = new ServerOutput(socket, this);
		addClient(serverOutput);
		//serverOutput.sendEnvelope(new Envelope(spaceStats, "SpaceStats"));
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
	         System.out.println("Game saved. "); // at " + currDir + "\\tmp\\SpaceStats.ser");
	         
	         
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
				//System.out.println("ERROR.");
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
	
	public static JTextPane inputConsole(final PrintWriter in) {
		final JTextPane area = new JTextPane();

		area.setFont(new Font("Times New Roman", Font.PLAIN, 28));
		
	    // handle "System.in"
	    area.addKeyListener(new KeyAdapter() {
	        private StringBuffer line = new StringBuffer();
	        @Override public void keyTyped(KeyEvent e) {
	            char c = e.getKeyChar();
	            if (c == KeyEvent.VK_ENTER) {
	                in.println(line);
	                line.setLength(0); 
	                area.setText("");
	            } else if (c == KeyEvent.VK_BACK_SPACE) {
	            	if(line.length() > 0)
	            	{
	            		line.setLength(line.length() - 1); 
	            	}
	            	else
	            	{
	            		line.append(' ');
	            	}
	            } else if (c == KeyEvent.VK_ESCAPE) {
	            	saveProgress();
	            	System.exit(0);
	            	
	            } else if (!Character.isISOControl(c)) {
	                line.append(e.getKeyChar());
	            }
	        }
	    });

	    return area;
	}
	
	public static JTextArea outputConsole(final InputStream out) {
		final JTextArea area = new JTextArea();
	    
		area.setFont(new Font("Times New Roman", Font.PLAIN, 24));
		
		area.setBackground(new Color(200, 220, 255));
		
	    // handle "System.out"
	    new SwingWorker<Void, String>() {
	        @Override protected Void doInBackground() throws Exception {
	            Scanner s = new Scanner(out);
	            while (s.hasNextLine())
	            	{
	            		publish(s.nextLine() + "\n");
	            	}
	            return null;
	        }
	        @Override protected void process(List<String> chunks) {
	            for (String line : chunks) area.append(line);
	        }
	    }.execute();
	    
	    area.addKeyListener(new KeyAdapter() {
	        private StringBuffer line = new StringBuffer();
	        @Override public void keyTyped(KeyEvent e) {
	            char c = e.getKeyChar();

	            if (c == KeyEvent.VK_ESCAPE) {
	            	saveProgress();
	            	System.exit(0);
	            }
	        }
	    });
	    
	    area.setEditable(false);
	    
	    return area;
	}
}
