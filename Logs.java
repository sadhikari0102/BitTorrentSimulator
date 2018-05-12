import java.io.BufferedReader;
import java.io.IOException;

public class Logs implements Runnable
{
	BufferedReader br;
	String peerID;
	
	public void run()
	{
		try
		{
			String l = null;
			while( (l = br.readLine()) != null )
			{
				System.out.println(peerID+" : "+l);
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public Logs(String peerID, BufferedReader br)
	{
		this.br = br;
		this.peerID = peerID;
	}
}