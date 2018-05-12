import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class Host implements Runnable{
	
	public static Host server = null;
	public boolean isDone = false;
	public Update_Logs logs = null;
	public ServerSocket socket = null;
	String peerID;	
	public Peer_Properties properties = null;
	public String serverId;
	public Connector connect;
	
	public static Host intialize(String id, Connector ccontrol){
		if(server == null){
			
			server = new Host();
			server.serverId = id;
			server.connect = ccontrol;
			
			boolean isInitialized = server.properties(ccontrol);
			if(isInitialized == false){
				
				server = null;
			}
		}
		return server;
	}
	
	public void run() {
		Map<String,Peers_Info> peerMap = properties.getInfo_Map();
		Peers_Info serverPeerInfo = peerMap.get(serverId);
		
		int server_port = serverPeerInfo.getPortNumber();
		
		try {
			
			socket = new ServerSocket(server_port);
			
			int expectedConnections = connect.peerCount();
			
			for(int i=0 ; i<expectedConnections ; i++){
				
				Socket neighborSocket = socket.accept();
				
				Peers_Data peer_neighborControl = Peers_Data.initializeLink(neighborSocket, connect);
				
				connect.neighbours.add(peer_neighborControl);
				//(peer_neighborControl);
				
				new Thread(peer_neighborControl).start();
			}					
			
			isDone = true;
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}
	
	public boolean properties (Connector ccontrol){
		
		logs = connect.getLogInstance();		
		properties = Peer_Properties.instanceCreation();
		if(properties == null){
			return false;
		}	
		return true;
	}
}
