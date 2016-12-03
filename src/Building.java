

import java.io.Serializable;

public class Building implements Serializable
{
	private String buildingName;
	private String buildingDesc;
	
	public int level;
	public int MAX_LEVEL = 0;
	
	public Building(String buildingName, String buildingDesc)
	{
		new Building(buildingName, buildingDesc, 0);
	}
	
	public Building(String buildingName, String buildingDesc, int buildingLevel)
	{
		new Building(buildingName, buildingDesc, 0, 5);
	}
	
	public Building(String buildingName, String buildingDesc, int buildingLevel, int MAX_LEVEL)
	{
		this.buildingName = buildingName;
		this.buildingDesc = buildingDesc;
		this.level = buildingLevel;
		this.MAX_LEVEL = MAX_LEVEL;
	}
	
	public String toString()
	{
		return buildingName;
	}
	
	public String getDesc()
	{
		return buildingDesc;
	}
}
