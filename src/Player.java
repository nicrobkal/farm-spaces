import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.PriorityQueue;

public class Player implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String playerName = "ADMIN_OVERRIDE";
	private Skill[] skillList;
	private Building currBuilding;
	private Client client;
	private PriorityQueue<Event> eventQueue = new PriorityQueue<Event>();
	//private Occupation occupation
	//private int communityRank

	public int playerID;
	
	public Player(String playerName, Building currBuilding, int playerID, Client client)
	{
		this.playerName = playerName; 
		
		this.currBuilding = currBuilding;
		
		this.playerID = playerID;
		
		this.client = client;
		
		this.skillList = client.getSpaceStats().spaceSkills;
	}
	
	public String toString()
	{
		return playerName;
	}
	
	public void improveSkill(Skill improvedSkill, int addedXP)
	{
		//for(int i = 0; i < )
	}
	
	public Client getClient()
	{
		return client;
	}
	
	public void setClient(Client client)
	{
		this.client = client;
	}
}
