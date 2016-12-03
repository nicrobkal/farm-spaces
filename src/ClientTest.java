

import java.net.*;
import java.util.Scanner;
import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

public class ClientTest extends Applet implements Runnable, KeyListener
{
	static Socket socket;
	static DataInputStream in;
	static DataOutputStream out;
	private static Scanner sc;
	
	private final int BOARD_SIZE = 20;
	
	static int playerId;
	
	int[] x = new int[BOARD_SIZE];
	int[] y = new int[BOARD_SIZE];
	
	boolean left, up, right, down;
	
	int playerX;
	int playerY;
	
	public void init()
	{
		setSize(200, 200);
		addKeyListener(this);
		try{
			System.out.println("Connecting...");
			socket = new Socket("localhost",7777);
			System.out.println("Connection Succesful");
			
			in = new DataInputStream(socket.getInputStream());
			playerId = in.readInt();
			
			out = new DataOutputStream(socket.getOutputStream());
			
			//PlayerInput userInput = new PlayerInput(in, this);
			//Thread thread = new Thread(userInput);
			//thread.start();
			
			Thread thread2 = new Thread(this);
			thread2.start();
		}
		catch(Exception ex)
		{
			System.out.println("Connection failed. ");
		}
	}
	
	public void run()
	{
		while(true)
		{
			if(left)
			{
				playerX -= 20;
			}
			if(up)
			{
				playerY -= 20;
			}
			if(right)
			{
				playerX += 20;
			}
			if(down)
			{
				playerY += 20;
			}
			
			if(left || up || right || down)
			{
				try
				{
					out.writeInt(playerId);
					out.writeInt(playerX);
					out.writeInt(playerY);
				}
				catch(Exception ex)
				{
					System.out.println("Error sending Coordinates");
				}
			}
			
			repaint();
			
			try
			{
				Thread.sleep(200);
			}
			catch(Exception ex)
			{
				System.out.println("Could not sleep. I blame the sun. ");
			}
			
		}
	}
	
	public void updateCoords(int playerId, int currX, int currY)
	{
		this.x[playerId] = currX;
		this.y[playerId] = currY;
	}
	
	public void paint(Graphics g)
	{
		for(int i = 0; i < BOARD_SIZE; i++)
		{
			g.drawOval(x[i], y[i], 10, 10);
		}
		
	}
	
	public void keyPressed(KeyEvent e)
	{
		int keyCode = e.getKeyCode();
		if(keyCode == KeyEvent.VK_LEFT)
		{
			left = true;
		}
		if(keyCode == KeyEvent.VK_UP)
		{
			up = true;
		}
		if(keyCode == KeyEvent.VK_RIGHT)
		{
			right = true;
		}
		if(keyCode == KeyEvent.VK_DOWN)
		{
			down = true;
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
		int keyCode = e.getKeyCode();
		if(keyCode == KeyEvent.VK_LEFT)
		{
			left = false;
		}
		if(keyCode == KeyEvent.VK_UP)
		{
			up = false;
		}
		if(keyCode == KeyEvent.VK_RIGHT)
		{
			right = false;
		}
		if(keyCode == KeyEvent.VK_DOWN)
		{
			down = false;
		}
	}
	
	public void keyTyped(KeyEvent e)
	{
		
	}
}


/*class PlayerInput implements Runnable
{
	private DataInputStream in;
	//private ClientTest client;
	//ClientPlayerInput(DataInputStream in, ClientTest client) 	//Clients.in = in;
		//this.client = client;
	//}
	
	public void run() 
	{	
		while(true)
		{
			try 
			{
				int playerId = in.readInt();
				int x = in.readInt();
				int y = in.readInt();
				//client.updateCoords(playerId, x, y);
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}*/