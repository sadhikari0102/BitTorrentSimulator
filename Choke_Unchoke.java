import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class Choke_Unchoke implements Runnable 
{


	public ScheduledFuture<?> taskForChokeUnchoke = null;
	private ScheduledExecutorService schedulerTask = null;
	private Connector threadControllerForChokeUnchoke = null;
	private Update_Logs logs = null;

	private static Choke_Unchoke chockeUnchokeHandler = null;
	
	public void run() {

		Integer preferredNeighborsForChoke = 0;
		HashMap<String, Double> mapForSpeed = threadControllerForChokeUnchoke.populateSpeeds();
		
		if (Peer_Property_Tokens.get_value("NumberOfPreferredNeighbors") != null)
			preferredNeighborsForChoke = Integer.parseInt(Peer_Property_Tokens.get_value("NumberOfPreferredNeighbors"));

		if (preferredNeighborsForChoke <= mapForSpeed.size()) 
		{
			ArrayList<String> unchokedForChokeUnchoke = new ArrayList<String>();
			
			Set<Entry<String, Double>> entrySetForChoke = mapForSpeed.entrySet();

			@SuppressWarnings("unchecked")
			Entry<String, Double>[] tempArrForChoke = new Entry[mapForSpeed.size()];
			
			LinkedHashMap<String, Double> mapHashLink = new LinkedHashMap<String, Double>();
			
			tempArrForChoke = entrySetForChoke.toArray(tempArrForChoke);
			
			int countForChoke = 0;
			
			ArrayList<String> chokeUnchoke = new ArrayList<String>();

			for (int i = 0; i < tempArrForChoke.length; i++) {
				for (int j = i + 1; j < tempArrForChoke.length; j++) {
					if (tempArrForChoke[i].getValue().compareTo(tempArrForChoke[j].getValue()) == -1) {
						
						Entry<String, Double> temp = tempArrForChoke[i];
						tempArrForChoke[i] = tempArrForChoke[j];
						tempArrForChoke[j] = temp;
					}
				}
			}
			
			
			for (int i = 0; i < tempArrForChoke.length; i++) 
				mapHashLink.put(tempArrForChoke[i].getKey(), tempArrForChoke[i].getValue());	

			for (Entry<String, Double> entryChoke : mapHashLink.entrySet())
			{
				String key = entryChoke.getKey();
				unchokedForChokeUnchoke.add(key);
				countForChoke++; 
				if (countForChoke == preferredNeighborsForChoke)
					break;

			}


			for (String peerID : unchokedForChokeUnchoke) 
				mapHashLink.remove(peerID);
			
			
			chokeUnchoke.addAll(mapHashLink.keySet());

			String log = "Peer "+threadControllerForChokeUnchoke.getPeerId()+" has neighbors ";
			
			for (String peerID : unchokedForChokeUnchoke) 
				log += peerID + " , ";
			
			logs.info(log);
			
			threadControllerForChokeUnchoke.unchokeMultiple(unchokedForChokeUnchoke);
			threadControllerForChokeUnchoke.chokeMultiple(chokeUnchoke);
		}
	}
	
	public static synchronized Choke_Unchoke instanceCreator(Connector connect) {
		
		if (chockeUnchokeHandler == null) {
			
			if (connect == null) {
				
				return null;
			}

			chockeUnchokeHandler = new Choke_Unchoke();
			chockeUnchokeHandler.schedulerTask = Executors.newScheduledThreadPool(1);	
			chockeUnchokeHandler.logs = connect.getLogInstance();
			chockeUnchokeHandler.threadControllerForChokeUnchoke = connect;
		}
		return chockeUnchokeHandler;
	}

	public void startChoke(int delay_start, int delay_int) {
		
		taskForChokeUnchoke = schedulerTask.scheduleAtFixedRate(this, delay_start, delay_int, TimeUnit.SECONDS);
	}
}