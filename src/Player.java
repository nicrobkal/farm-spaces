

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Player implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String playerName = "ADMIN_OVERRIDE";
	private Skill[] skillList = new Skill[0];
	private Building currBuilding;
	private transient Client client;
	//private Occupation occupation
	//private int communityRank

	public int playerID;
	
	public Player(String playerName, Building currBuilding, int playerID, Client client)
	{
		this.playerName = playerName; 
		
		this.currBuilding = currBuilding;
		
		this.playerID = playerID;
		
		this.client = client;
	}
	
	public String toString()
	{
		return playerName;
	}
	
	public void addSkill(Skill newSkill)
	{
		Skill[] tempSkills = new Skill[skillList.length + 1];
		
		for(int i = 0; i < skillList.length; i++)
		{
			tempSkills[i] = skillList[i];
		}
		
		tempSkills[tempSkills.length - 1] = newSkill;
		
		skillList = tempSkills;
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
