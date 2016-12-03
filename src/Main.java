import javax.swing.*;
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
		    //JScrollPane jPane = new JScrollPane();
		    //frame.add(jPane);
		    frame.add(console(outPipe, inWriter));
		    //frame.add(new JScrollBar(5));
		    frame.setSize(600, 400);
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    frame.setVisible(true);
	    }
	    catch(Exception ex)
	    {
	    	ex.printStackTrace();
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
	            } else if (c == KeyEvent.VK_ESCAPE) {
	            	//client.printMessage("HEY WE 1! ");
	            	//client.getSpaceStats().removeClient(client); //PROBABLY THE ISSUE
	            	//client.printMessage("HEY WE! ");
	            	//client.getClientOutput().sendEnvelope(new Envelope(client.getSpaceStats(), "SpaceStats"));
	            	client.getClientOutput().sendEnvelope(new Envelope(client.getClientPlayer().toString() + " disconnected! ", "String"));
	            	//client.getClientOutput().sendEnvelope(new Envelope(client.getClientPlayer().toString() + " disconnected! ", "String"));
	            	client.getClientOutput().sendEnvelope(new Envelope(client.getSpaceStats(), "SpaceStats"));
	                System.exit(0);
	            } else if (!Character.isISOControl(c)) {
	                line.append(e.getKeyChar());
	            }
	        }
	    });

	    return area;
	}
}
