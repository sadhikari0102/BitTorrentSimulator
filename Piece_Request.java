import java.util.*;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Piece_Request implements Runnable 
{	
	private static String PREFIX_LOGS = Piece_Request.class.getSimpleName();	
	public BlockingQueue<Message_Body> messsageQueue;
	private boolean disconnected = false;
	private Connector connect;
	private Peers_Data peerHandler;
	private Bit_Fields bFControl = null;
	int [] pieces = new int[1000];	
	
	
	private Piece_Request(){
	}
	
	
	public static Piece_Request InstanceCreation(Connector connecter, Peers_Data peerHandler){		
		if(connecter == null || peerHandler == null) {
			//base case
			return null;
		}
		
		Piece_Request pieceRequestSender = new Piece_Request();
		
		pieceRequestSender.messsageQueue = new ArrayBlockingQueue<Message_Body>(Constant_Val.QUEUE);

		int size = Integer.parseInt(Peer_Property_Tokens.get_value("PieceSize"));
		int cielPiece = (int) Math.ceil(Integer.parseInt(Peer_Property_Tokens.get_value("FileSize")) / (size*1.0));
	
		pieceRequestSender.bFControl = new Bit_Fields(cielPiece);
		
		pieceRequestSender.connect = connecter;
		pieceRequestSender.peerHandler = peerHandler;
		
		return pieceRequestSender;
	}
	
	
	public int pieceCount() {
		Bit_Fields peerBFControl = connect.getMessageBF().getBitFieldsObject();
		int counter = 0;

		for(int i=0 ; i<bFControl.getSize() && counter<pieces.length ; i++) {
			if(!peerBFControl.getBitFieldIndividual(i)  && bFControl.getBitFieldIndividual(i) ) {
				pieces[counter++] = i;
			}
		}
		
		if(counter != 0) {
			Random randomGen = new Random();
			return pieces[randomGen.nextInt(counter)];
		}
		else 
			return -1;

	}
	
	
	public void run()  {	
		
		while(!disconnected ) {
			try {				
				Message_Body messageBody = messsageQueue.take();
				System.out.println(PREFIX_LOGS+": Message Received : "+Constant_Val.getMessage(messageBody.getEachMessageType()));
				
				Message_Body messageRequest = Message_Body.messageCreate();
				messageRequest.setMesssageType(Constant_Val.REQUEST);
				
				Message_Body messageInterested = Message_Body.messageCreate();
				messageInterested.setMesssageType(Constant_Val.INTERESTED);
				
				if(messageBody.getEachMessageType() == Constant_Val.BITFIELD) {
					bFControl = messageBody.getBitFieldsObject();
					
					int missing = pieceCount();
					
					if(missing == -1) {
						Message_Body notInterestedMsg = Message_Body.messageCreate();
						notInterestedMsg.setMesssageType(Constant_Val.NOT_INTERESTED);
						peerHandler.noInterestMsgSend(notInterestedMsg);
					}
					else {
						messageInterested.setPieceNumber(missing);
						peerHandler.interestMsgSend(messageInterested);
						
						messageRequest.setPieceNumber(missing);
						peerHandler.requestMsgSend(messageRequest);
					}									
				}
				
				if(messageBody.getEachMessageType() == Constant_Val.HAVE) {
					int pieceCount = messageBody.getPieceNumber();
					
					try  {
						bFControl.setBitFieldIndividual(pieceCount, true);
					}
					catch (Exception e) {
						
						System.out.println(PREFIX_LOGS+"["+peerHandler.peer_ID+"]: Error : NULL POINTER for piece Index"+pieceCount +" ... "+bFControl);
						e.printStackTrace();
					}
					
					int missingIndex = pieceCount();

					if(missingIndex == -1) {
						Message_Body notInterestedMsg = Message_Body.messageCreate();
						notInterestedMsg.setMesssageType(Constant_Val.NOT_INTERESTED);
						peerHandler.noInterestMsgSend(notInterestedMsg);
					}
					else {
						if(peerHandler.recvdPieceMLast() ) {
							
							peerHandler.lastPieceMsgRcvdSetter(false);
							messageInterested.setPieceNumber(missingIndex);
							peerHandler.interestMsgSend(messageInterested);
							
							messageRequest.setPieceNumber(missingIndex);
							peerHandler.requestMsgSend(messageRequest);
						}	
					}									
				}
				
				if(messageBody.getEachMessageType() == Constant_Val.PIECE) {					
					int count_p = pieceCount();

					if(count_p != -1) {
						if(peerHandler.recvdPieceMLast()) {
							
							peerHandler.lastPieceMsgRcvdSetter(false);
							messageInterested.setPieceNumber(count_p);
							peerHandler.interestMsgSend(messageInterested);
							
							messageRequest.setPieceNumber(count_p);
							peerHandler.requestMsgSend(messageRequest);
						}						
					}									
				}
				
				if(messageBody.getEachMessageType() == Constant_Val.UNCHOKE) {
					int count_p = pieceCount();

					peerHandler.lastPieceMsgRcvdSetter(false);
					
					if(count_p != -1) {
						messageInterested.setPieceNumber(count_p);
						peerHandler.interestMsgSend(messageInterested);
						
						messageRequest.setPieceNumber(count_p);
						peerHandler.requestMsgSend(messageRequest);
					}									
				}
				
				
			}
			catch (Exception e) {				
				e.printStackTrace();
				break;
			}
		}
	}
	
	
	public boolean completeCheckNeighbour()
	{
		if(bFControl != null && bFControl.fileDownloadedCheck() == true)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}