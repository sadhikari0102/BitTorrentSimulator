import java.io.ObjectOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class Message_Sending_Peer implements Runnable {
	
	private static final String LOGGER_PREFIX = Message_Sending_Peer.class.getSimpleName();	
	private BlockingQueue<Message_Main> blockQueue_Messages;
	private ObjectOutputStream objOutputStream = null;
	private boolean shutDownForPeerSend = false;
	
	
	private Message_Sending_Peer(){
		
	}
	
	public static Message_Sending_Peer createInstace(ObjectOutputStream opStream, Peers_Data PeerHandler){
		
		Message_Sending_Peer msgSendingPeer = new Message_Sending_Peer();
		boolean flag = msgSendingPeer.init();		
		if(flag == false){
			msgSendingPeer.undo_init();
			msgSendingPeer = null;
			return null;
		}
		
		msgSendingPeer.objOutputStream = opStream;
		return msgSendingPeer;
	}
	
	public void undo_init(){
		if(blockQueue_Messages !=null && blockQueue_Messages.size()!=0){
			blockQueue_Messages.clear();
		}
		blockQueue_Messages = null;
	}
	
	private boolean init(){
		blockQueue_Messages = new ArrayBlockingQueue<Message_Main>(Constant_Val.QUEUE);
		return true;
	}
	
	
	public void run() {
		
		if(blockQueue_Messages == null){
			throw new IllegalStateException(LOGGER_PREFIX+": This object is not initialized properly. This might be result of calling deinit() method");
		}
		
		while(shutDownForPeerSend == false){
			try {				
				Message_Main message = blockQueue_Messages.take();
				
				objOutputStream.writeUnshared(message);
				objOutputStream.flush();
					
				
				message = null;
			} catch (Exception e) {				
					//e.printStackTrace();	
				System.out.println("SYSTEM GOT TERMINATED..!!");
				
				break;
			}
		}
	}
	
	public void sendMessage(Message_Main message) throws InterruptedException{
		if(blockQueue_Messages == null){
			throw new IllegalStateException("");
		}else{
			blockQueue_Messages.put(message);
		}
	}
	
	public void shut_down(){
		shutDownForPeerSend = true;
	}
}
