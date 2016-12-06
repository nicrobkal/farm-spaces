import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;

public class Main {
	
	static Client client;
	
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
		    JFrame frame = new JFrame("\"Spaces\" Client Console");
		    
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
	    	//ex.printStackTrace();
	    }
	    
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Print the Hamachi IP you wish to connect to: ");
        
        boolean connected = false;
        
        while(!connected)
        {
        	String ip = sc.nextLine();
            
            try
            {
            	System.out.println("Connecting...");
        		Socket socket = new Socket(ip, 7777);
        		System.out.println("Connection successful. ");
        		
        		connected = true;
        		
        		client = new Client(socket);
            }
            catch(Exception ex)
            {
            	System.out.println("Could not connect to server. ");
            	System.out.println("Try again? ");
            }
        }
        
    }
	
	public static JTextArea inputConsole(final PrintWriter in) {
		final JTextArea area = new JTextArea();

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
	            	//client.printMessage("HEY WE 1! ");
	            	//client.getSpaceStats().removeClient(client); //PROBABLY THE ISSUE
	            	//client.printMessage("HEY WE! ");
	            	//client.getClientOutput().sendEnvelope(new Envelope(client.getSpaceStats(), "SpaceStats"));
	            	try
	            	{
	            		client.getClientOutput().sendEnvelope(new Envelope(client.getClientPlayer().toString() + " disconnected! ", "String"));
	            		
	            		client.getSpaceStats().getPlayer(client.getClientPlayer().toString()).setClient(null);
	            		
		            	client.getClientOutput().sendEnvelope(new Envelope(client.getSpaceStats(), "SpaceStats"));
	            	}
	            	catch(Exception ex)
	            	{
	            		//client.getClientOutput().sendEnvelope(new Envelope("Failed to send updated Space Stats to Server after " + client.getClientPlayer().toString() + " disconnected! ", "String"));
	            	}
	            	
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
	            	try
	            	{
	            		client.getClientOutput().sendEnvelope(new Envelope(client.getClientPlayer().toString() + " disconnected! ", "String"));
	            		
	            		client.getSpaceStats().getPlayer(client.getClientPlayer().toString()).setClient(null);
	            		
		            	client.getClientOutput().sendEnvelope(new Envelope(client.getSpaceStats(), "SpaceStats"));
		            	
		            	System.exit(0);
	            	}
	            	catch(Exception ex)
	            	{
	            		//client.getClientOutput().sendEnvelope(new Envelope("Failed to send updated Space Stats to Server after " + client.getClientPlayer().toString() + " disconnected! ", "String"));
	            		System.exit(0);
	            	}
	            	
	            	
	            }
	        }
	    });
	    
	    area.setEditable(false);
	    
	    return area;
	}
}