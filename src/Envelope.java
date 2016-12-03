import java.io.Serializable;

public class Envelope<T> implements Serializable
{
	private static final long serialVersionUID = 1L;
	final Object contents;
	String type;
	
	public Envelope(final Object contents, String type) 
	{
        this.contents = contents;
        this.type = type;
	}
	
    public <T> T getContents(Class<T> clazz) 
    {
        return (T) contents;
    }
	
    public String getType() 
    {
        return type;
    }
}