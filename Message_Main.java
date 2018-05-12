import java.io.Serializable;

public interface Message_Main extends Serializable
{
	public int getEachMessageType();	
	public int getMessageLength();
	public int getMessageIndex();
}