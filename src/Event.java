import java.util.ArrayList;
import java.util.Scanner;

public class Event implements Comparable<Event>
{
	private ArrayList<Choice> choiceList = new ArrayList<Choice>();
	
	private String eventDesc;
	
	private int priority;
	
	private Scanner sc;
	
	public Event(String eventDesc, ArrayList<Choice> choiceList)
	{
		new Event(eventDesc, choiceList, 0);
	}
	
	public Event(String eventDesc, ArrayList<Choice> choiceList, int priority)
	{
		this.eventDesc = eventDesc;
		this.choiceList = choiceList;
		this.priority = priority;
		sc = new Scanner(System.in);
	}
	
	public String toString()
	{
		return eventDesc;
	}
	
	public void printChoices()
	{
		for(int i = 0; i < choiceList.size(); i++)
		{
			System.out.println(i + 1 + ": " + choiceList.get(i));
		}
	}
	
	public void startEvent()
	{
		System.out.println(eventDesc);
		
		printChoices();
		
		int choiceMade = -1;
		
		while(choiceMade < 0 || choiceMade > choiceList.size())
		{
			try
			{
				choiceMade = sc.nextInt();
			}
			catch(Exception ex)
			{
				choiceMade = -1;
			}
		}
		
		
	}

	public int compareTo(Event event) 
	{
		if(this.priority > event.priority)
		{
			return 1;
		}
		if(this.priority < event.priority)
		{
			return -1;
		}
		
		return 0;
	}
}
