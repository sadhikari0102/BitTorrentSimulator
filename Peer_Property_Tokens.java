import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class Peer_Property_Tokens 
{
	
	private static final Hashtable<String, String> peerToken = new Hashtable<String, String>();

	static
	{
		try 
		{
			BufferedReader br =  new BufferedReader(new InputStreamReader(new FileInputStream(Constant_Val.CONFIG_FILE)));
			
			String rl = br.readLine();
			
			while(rl != null)
			{
				String temp[] = rl.trim().split(" ");
				peerToken.put(temp[0].trim(), temp[1].trim());
				rl = br.readLine();
			}
			br.close();	
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("Exception: "+e.getMessage());
			throw new ExceptionInInitializerError("Error Loading properties");
		}
	}
	
	public static String get_value(String value)
	{
		return peerToken.get(value);
	}
}