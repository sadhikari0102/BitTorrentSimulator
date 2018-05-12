import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Connector {

	public static String PREFIX_LOGS = Connector.class.getSimpleName();
	public ArrayList<String> allPeers = new ArrayList<String>();

	public boolean connected = false;
	
	public Raw_Message message = null;
	public Piece_Process message_control = null;
	public static Connector connect = null;
	public ArrayList<Peers_Data> neighbours = null;
	public Choke_Unchoke choke_control = null;
	public Unchoke unchoke_control = null;
	public Host server;
	public Update_Logs logs = null;
	public Peer_Properties properties = null;

	public String peerId;
	public ArrayList<String> choked = new ArrayList<String>();
	
	public String getPeerId() 
	{
		return peerId;
	}

	private void connect() 
	{		
		Set<String> listOfPids = properties.getInfo_Map().keySet();

		for (String neighbors : listOfPids) 
		{		
			if (Integer.parseInt(neighbors) < Integer.parseInt(peerId)) 
			{
				logs.info("Peer " + peerId + " makes a connection to Peer " + neighbors);
				
				Peers_Info info= properties.getInfo_Map().get(neighbors);
				String nextHost = info.getAddress();
				int nextPort_no = info.getPortNumber();

				try 
				{
					Socket neighborPeerSocket = new Socket(nextHost, nextPort_no);

					Peers_Data neighborPeerHandler = Peers_Data.initializeLink(neighborPeerSocket, this);

					neighborPeerHandler.setPeer_ID(info.getID());

					neighbours.add(neighborPeerHandler);

					new Thread(neighborPeerHandler).start();
				}
				catch (UnknownHostException e) 
				{
					e.printStackTrace();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		this.connected = true;
	}

	public boolean statusCheck() 
	{
		System.out.println("Checking Peer  " + peerId + "  for download completion");
		if (connected == false || server.isDone == false)
		{
			return false;
		}

		if (properties.getInfo_Map().size() == allPeers.size())
		{
			System.out.println(allPeers.size());
			choke_control.taskForChokeUnchoke.cancel(true);
			unchoke_control.taskUnchoke.cancel(true);
			logs.close();
			message_control.close();
			System.out.println("Exit");
			try{
			System.exit(0);
			}
			catch(Exception e) {
			System.out.println("ERROR");
			}
		}

		return false;
	}
	
	public static synchronized Connector intializePeer(String peerId) 
	{
		if (connect == null) 
		{
			connect = new Connector();
			connect.peerId = peerId;
			boolean flag=false;
			connect.properties = Peer_Properties.instanceCreation();

			if (connect.properties == null){
				
				flag = false;
				connect=null;
				return null;
			}

			connect.message = Raw_Message.createMessage();
			
			if (connect.message == null)
			{
				flag = false;
				connect=null;
				return null;
			}

			if (Peer_Properties.instanceCreation().getInfo_Map().get(peerId).containsFile() == false)
			{
				connect.message_control = Piece_Process.createPieceControl(false, peerId);
			} 
			else 
			{
				connect.message_control = Piece_Process.createPieceControl(true, peerId);
			}

			if (connect.message_control == null) 
			{
				flag = false;
				connect=null;
				return null;
			}

			connect.neighbours = new ArrayList<Peers_Data>();

			connect.logs = Update_Logs.getLogger(peerId);
			if (connect.logs == null)
			{
				System.out.println("logger initialization error");	
				flag = false;
				connect=null;
				return null;
			}

			connect.server = Host.intialize(peerId, connect);

			connect.logs = Update_Logs.getLogger(peerId);

			flag=true;

			if (flag == false) 
			{
				connect = null;
			}
		}
		
		return connect;
	}

	public void launch() 
	{
			new Thread(server).start();
		
		connect();

		choke_control = Choke_Unchoke.instanceCreator(this);
		
		int interval_chkUnchk = Integer.parseInt(Peer_Property_Tokens.get_value(Constant_Val.INTERVAL));
		choke_control.startChoke(0, interval_chkUnchk);

		unchoke_control= Unchoke.instanceCreate(this);
		int interval_unchk = Integer.parseInt(Peer_Property_Tokens.get_value(Constant_Val.OPT_INTERVAL));
	
		unchoke_control.taskUnchoke = unchoke_control.taskScheduler.scheduleAtFixedRate(unchoke_control, 10, interval_unchk, TimeUnit.SECONDS);
	}

	public synchronized Message_Body getMessageBF()
	{

		Message_Body msg = Message_Body.messageCreate();

		msg.setBitFieldsObject(message_control.returnBF());
	
		if (msg.getBitFieldsObject() == null)
		{
			
		}
		msg.setMesssageType(Constant_Val.BITFIELD);

		return msg;
	}

	public HashMap<String, Double> populateSpeeds() 
	{
		HashMap<String, Double> speedList = new HashMap<String, Double>();

		for (Peers_Data nextPeer : neighbours)
		{
			speedList.put(nextPeer.peer_ID, nextPeer.fetchSpeed());
		}
		return speedList;
	}
	
	public int peerCount() {
		
		Set<String> allPeers= properties.getInfo_Map().keySet();

		int total = 0;

		for (String neighbors : allPeers) 
		{	
			if (Integer.valueOf(neighbors) > Integer.valueOf(peerId)) 
			{
				total++;
			}
		}

		return total;
	}

	public synchronized void save(Message_Body msg_pc, String srcPeer){	
		
		message_control.pieceWriter(msg_pc.getPieceNumber(), msg_pc.getPieceInfo());
		logs.info("Peer " + connect.getPeerId() + " has downloaded the piece " + msg_pc.getPieceInfo() + " from " + srcPeer + ". Now the number of pieces it has is " + (message_control.returnBF().getSetBitCount()));
	}

	public void have(int pc_number, String havingPeer){
		
		Message_Body have_msg = Message_Body.messageCreate();
		have_msg.setPieceNumber(pc_number);
		have_msg.setMesssageType(Constant_Val.HAVE);

		for (Peers_Data nextPeer : neighbours) {
			
			if (havingPeer.equals(nextPeer.peer_ID) != true) {
				nextPeer.haveMsgSend(have_msg);
			}
		}
	}
	
	public synchronized Update_Logs getLogInstance() {
		
		return logs;
	}
	
	public int[] remainingPieces() {
		
		return message_control.getMissing();
	}

	public Message_Body getMessage(int pc_num)
	{
		Piece_Info piece = message_control.getData(pc_num);
		
		if (piece == null) {
			return null;
		}
		else {
			
			Message_Body msg = Message_Body.messageCreate();
			msg.setMesssageType(Constant_Val.PIECE);
			msg.setPieceNumber(pc_num);
			msg.setPieceInfo(piece);
			return msg;
		}
	}

	public void chokeMultiple(ArrayList<String> allPeers)
	{
		choked = allPeers;
		Message_Body chk_msg = Message_Body.messageCreate();
		chk_msg.setMesssageType(Constant_Val.CHOKE);

		for (String chkPeers : allPeers) 
		{
			for (Peers_Data nextPeer : neighbours)
			{
				if (nextPeer.peer_ID.equals(chkPeers)) 
				{
					if (nextPeer.receivedACK == true) 
						nextPeer.chokeMsgSend(chk_msg);
						break;
				}
			}
		}
	}

	
	public void unchokeMultiple(ArrayList<String> allPeers)
	{
		Message_Body unchk_msg = Message_Body.messageCreate();
		unchk_msg.setMesssageType(Constant_Val.UNCHOKE);
		
		for (String unchkTargetPeer : allPeers) {
			for (Peers_Data nextPeer : neighbours) 
			{
				if (nextPeer.peer_ID.equals(unchkTargetPeer))
				{
					if (nextPeer.receivedACK == true) 
						nextPeer.unchokeMsgSend(unchk_msg);
					break;
				}
			}
		}
	}

	
	public void shutdown() 
	{
		if (connected == false || server.isDone == false) {

			return;
		}

		Message_Body shut_down_msg = Message_Body.messageCreate();

		shut_down_msg.setMesssageType(Constant_Val.SHUTDOWN);
	
		for (Peers_Data nextPeer : neighbours) 
		{	
			nextPeer.shutdownMsg(shut_down_msg);
		}
		allPeers.add(peerId);
	}
	
	public void unchoke(String unchkTargetPeer) 
	{
		Message_Body unchk_msg = Message_Body.messageCreate();
		unchk_msg.setMesssageType(Constant_Val.UNCHOKE);

		logs.info("Peer " + unchkTargetPeer+" is unchoked by "+peerId);
		
		for (Peers_Data nextPeer : neighbours) 
		{
			if (nextPeer.peer_ID.equals(unchkTargetPeer))
			{
				if (nextPeer.receivedACK == true) 
				{
					nextPeer.unchokeMsgSend(unchk_msg);
				}
				break;
			}
		}
	}

}