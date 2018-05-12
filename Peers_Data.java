import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;


public class Peers_Data implements Runnable
{	
	public static final String PREFIX_LOGS = Peers_Data.class.getSimpleName();
	
	private ObjectInputStream inputStream;
	public ObjectOutputStream outputStream;
	
	public Raw_Message messageID;
	public Connector connect;
	public Message_Sending_Peer peerServer;
	public Piece_Request request_Piece;
	public boolean ischoked;
	public boolean choked_p;
	public long start_time;
	public int size_data;
	public String peer_ID;
	private Socket socket_n;

	public Update_Logs log_control = null;
	public boolean receivedACK = false;
	public boolean sentHandShakeMsg = false;
	public boolean startReqChunk = false;
	public boolean lastPieceMsgRcvd = true;
	
	private Peers_Data() {
		
	}
	
	public void run() {
		
		if(peer_ID != null) {
			handshake_start();
		}

		try {	
			while(true) {
				
				Message_Main msg = (Message_Main)inputStream.readObject();
				
				int msgType = msg.getEachMessageType();
				
				Message_Body p2pmsg;
				
				switch (msgType) 
				{
					case Constant_Val.HANDSHAKE:
						if(msg instanceof Handshake_Message) {						
							Handshake_Message hndshkMsg = (Handshake_Message)msg;
							examinMsghandshake(hndshkMsg);
						}
						else {
							System.out.println("Message is not a Handshake Message");
						}
						break;
					
					case Constant_Val.REQUEST:
						p2pmsg = (Message_Body)msg; 
						requestControl(p2pmsg);
						break;
					
					case Constant_Val.BITFIELD:
						p2pControl((Message_Body)msg);
						break;
					
					case Constant_Val.CHOKE:
						p2pmsg = (Message_Body)msg;
						choked_p=true;
						log_control.info("Peer "+connect.getPeerId()+" is choked by "+peer_ID);
						break;
					
					case Constant_Val.HAVE:
						p2pmsg = (Message_Body)msg;
						haveMsgControl(p2pmsg);
						break;
						
					case Constant_Val.INTERESTED:
						p2pmsg = (Message_Body)msg;
						receiveInterestMsg(p2pmsg);
						break;
					
					case Constant_Val.NOT_INTERESTED:
						p2pmsg = (Message_Body)msg;
						receiveNoInterestMsg(p2pmsg);
						break;
						
					case Constant_Val.PIECE:
						p2pmsg = (Message_Body)msg;
						pieceGetter(p2pmsg);
						break;
						
					case Constant_Val.UNCHOKE:
						p2pmsg = (Message_Body)msg;
						choked_p = false;
						log_control.info("Peer "+connect.getPeerId()+" is unchoked by "+peer_ID);
						try {
							request_Piece.messsageQueue.put(p2pmsg);
						}
						catch (InterruptedException e){
							
							e.printStackTrace();
						}
						break;
					
					case Constant_Val.SHUTDOWN:
						p2pmsg = (Message_Body)msg;
						connect.allPeers.add(peer_ID);
						break;
				}
			}
		}
		catch(SocketException e){
			System.out.println("Connection Reset with exception: "+e.getMessage());
		}
		catch (EOFException e) {
			System.out.println("Peer "+peer_ID+" Disconnected.!!");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	synchronized public static Peers_Data initializeLink(Socket socket, Connector connecter) {
		Peers_Data peerdata = new Peers_Data();
		
		peerdata.socket_n = socket;
		peerdata.connect = connecter;
		
		boolean done = false;

		if(peerdata.socket_n == null) {
			peerdata.close();
			peerdata = null;
			return null;
		}
		
		try {	
			peerdata.outputStream = new ObjectOutputStream(peerdata.socket_n.getOutputStream());
			peerdata.inputStream = new ObjectInputStream(peerdata.socket_n.getInputStream());			
		}
		catch (IOException e) {
			e.printStackTrace();
			peerdata.close();
			peerdata = null;
			return null;
		}
		
		peerdata.messageID = Raw_Message.createMessage();
		
		if(peerdata.messageID == null) {
			peerdata.close();
			return null;
		}
		
		if(connecter == null) {
			peerdata.close();
			return null;
		}
		
		peerdata.peerServer = Message_Sending_Peer.createInstace(peerdata.outputStream,peerdata);
		
		if(peerdata.peerServer == null) {
			peerdata.close();
			return null;
		}
		
		new Thread(peerdata.peerServer).start();

		peerdata.request_Piece = Piece_Request.InstanceCreation(connecter, peerdata);

		peerdata.log_control = connecter.getLogInstance();
		done=true;
		
		if(!done ) {
			peerdata.close();
			peerdata = null;
		}
		return peerdata;
	}
	
	public void setReceivedHandshake(boolean rcvdACK) 
	{
		this.receivedACK = rcvdACK;
	}

	
	public synchronized void chunkRequestStartSetter(boolean chnkRqst) 
	{
		this.startReqChunk = chnkRqst;
	}
	
	public synchronized boolean sendHandshakeBits() {
		
		try  {
			Handshake_Message hndmsg = (Handshake_Message)inputStream.readObject();
			
			peer_ID = hndmsg.getPeerId();

			Thread.sleep(4000);
			
			examinMsghandshake(hndmsg);	

			return true;
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 

		return false;
	}
	
	
	private void pieceGetter(Message_Body msg_pc) {
		
		connect.save(msg_pc, peer_ID);
		connect.have(msg_pc.getPieceNumber(),peer_ID);
		
		size_data += msg_pc.getPieceInfo().getPieceSize();
		
		lastPieceMsgRcvdSetter(true);

		try {
			request_Piece.messsageQueue.put(msg_pc);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	private void p2pControl(Message_Body msg_p2p) {	
		try {
			request_Piece.messsageQueue.put(msg_p2p);
			
			if(receivedACK && sentHandShakeMsg  && !startReqChunk ) {
				new Thread(request_Piece).start();
				start_time = System.currentTimeMillis();
				size_data = 0;
				chunkRequestStartSetter(true) ;
			}
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}

	
	private void examinMsghandshake(Handshake_Message handshakeMsg)
	{	
		peer_ID = handshakeMsg.getPeerId();
		sendBitField();
		
		if(sentHandShakeMsg == false)
		{
			log_control.info("Peer "+connect.getPeerId()+" is now connected from "+peer_ID+".");
			handshake_start();
		}
		
		receivedACK = true;
		
		if(receivedACK == true && sentHandShakeMsg == true && startReqChunk == false) {
			new Thread(request_Piece).start();
			start_time = System.currentTimeMillis();
			size_data = 0;
			chunkRequestStartSetter(true);
		}
	}
	
	synchronized public void close() {
		try {
			if(inputStream != null)
			{
				inputStream.close();
			}			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private void requestControl(Message_Body msg_rqst) {
		if(ischoked == false) {
			Message_Body msgPcs = connect.getMessage(msg_rqst.getPieceNumber());
			
			if(msgPcs != null) {
				try {
					Thread.sleep(2000);
					peerServer.sendMessage(msgPcs);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private void haveMsgControl(Message_Body msg_hv) {
		log_control.info("Peer "+connect.getPeerId()+" received the 'HAVE' message from "+peer_ID+" for piece: "+msg_hv.getPieceNumber());
		
		try {
			request_Piece.messsageQueue.put(msg_hv);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	private void receiveInterestMsg(Message_Body msg_intrst) {
		log_control.info("Peer "+connect.getPeerId()+" received the 'INTERESTED' message from "+peer_ID);
	}
	
	
	private void receiveNoInterestMsg(Message_Body message) {
		log_control.info("Peer "+connect.getPeerId()+" received the 'NOT INTERESTED' message from "+peer_ID);
	}
	
	
	synchronized boolean handshake_start() {
		try {
			Handshake_Message message = Handshake_Message.create_Handshake_Message();
			message.setPeerId(connect.getPeerId());
			peerServer.sendMessage(message);
			sentHandShakeMsg = true;
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	synchronized boolean sendBitField() {
		try {			
			Message_Body msgs = connect.getMessageBF();
			peerServer.sendMessage(msgs);
			Thread.sleep(2000);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	
	public void interestMsgSend(Message_Body msg_intrst) {
		try {
			if(!choked_p) {
				peerServer.sendMessage(msg_intrst);
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public boolean downloadStatus() {
		if(startReqChunk) {
			return request_Piece.completeCheckNeighbour();
		}
		return false;
	}
	
	
	public void noInterestMsgSend(Message_Body msg_noIntrst) {
		try {
			peerServer.sendMessage(msg_noIntrst);

		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public void requestMsgSend(Message_Body msg_rqst) {
		try {
			if(!choked_p ) {
				peerServer.sendMessage(msg_rqst);
			}			
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public void chokeMsgSend(Message_Body msg_chk) {		
		try {
			if(!ischoked ) {
				start_time = System.currentTimeMillis();
				size_data = 0;

				setIsChoked(true);
				peerServer.sendMessage(msg_chk);
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public void unchokeMsgSend(Message_Body msg_unchk) {
		try {
			if(ischoked ) {
				start_time = System.currentTimeMillis();
				size_data = 0;

				setIsChoked(false);
				peerServer.sendMessage(msg_unchk);
			}
			
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public void haveMsgSend(Message_Body msg_hv) {
		try {
			peerServer.sendMessage(msg_hv);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public void shutdownMsg(Message_Body msg_shtdwn) {
		try {
			peerServer.sendMessage(msg_shtdwn);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private void setIsChoked(boolean ischk)	{
		ischoked = ischk;
	}
	
	
	synchronized public void setPeer_ID(String peer_ID) {
		this.peer_ID = peer_ID;	
	}
	
	
	public boolean recvdPieceMLast() {
		return lastPieceMsgRcvd;
	}

	
	public void lastPieceMsgRcvdSetter(boolean msg_lastPcRcvd) {
		this.lastPieceMsgRcvd = msg_lastPcRcvd;
	}
	
	
	public double fetchSpeed() {
		long totalTimeSpan = System.currentTimeMillis() - start_time;
		if(totalTimeSpan != 0) {
			return ((size_data * 1.0) / (totalTimeSpan * 1.0) );
		}	
		return 0;
	}

}