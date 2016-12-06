import java.util.ArrayList;

public class Choice 
{
	private Player playerAffected;
	
	private String choiceName;
	
	private Skill choiceRequirement;
	
	private ArrayList<ChoiceEffect> choiceEffects;
	
	private int skillRequired = 0;
	
	
	public Choice(Player playerAffected, String choiceName, ArrayList<ChoiceEffect> choiceEffects)
	{
		new Choice(playerAffected, choiceName, choiceEffects, new Skill("", "", new Building("", "")), 0);
	}
	
	public Choice(Player playerAffected, String choiceName, ArrayList<ChoiceEffect> choiceEffects, Skill choiceRequirement, int skillRequired)
	{
		this.playerAffected = playerAffected;
		
		this.choiceName = choiceName;
		
		this.choiceRequirement = choiceRequirement;
		
		this.choiceEffects = choiceEffects;
	}
	
	public void addChoiceEffect(ChoiceEffect choiceEffect)
	{
		choiceEffects.add(choiceEffect);
	}
}
