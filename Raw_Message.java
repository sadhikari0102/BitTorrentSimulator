import java.nio.ByteBuffer;

public class Raw_Message 
{

	private static Raw_Message message;
	
	private Raw_Message(){
		
	}
	
	
	public void close(){
		
	}
	
	
	public static Raw_Message createMessage() {
		if(message == null) {
			
			message = new Raw_Message();
			boolean done = true;
			
			if(!done ) {
				message.close();
				message = null;
			}
		}
		return message;
	} 
	
	public byte[] handshake_Message(byte[] bytesraw) {
		
		char hndshkHdr[] = Constant_Val.HANDSHAKE_HEADER.toCharArray();
		
		byte[] topHdr = new byte[32];

		for(int i = 0; i< 18; i++) {
			topHdr[i] = (byte)hndshkHdr[i];	
		}
		
		for(int i = 18; i<31;i++) {
			
			topHdr[i] = (byte)0;
			
		}
		
		topHdr[31] = bytesraw[3];
		
		return topHdr;
	}
	
	public byte[] request_Message(int pcNum) {
		return null;
	}

	
	public byte[] choke_Message() {		
		
		ByteBuffer buff_Bytes = ByteBuffer.allocate(5);
		buff_Bytes.putInt(Constant_Val.EMPTY);
		buff_Bytes.put(Constant_Val.CHOKE);
		byte[] msgchk = buff_Bytes.array();
		return msgchk;
		
	}
	
	
	public byte[] unchoke_Message() {
		
		ByteBuffer buff_Bytes = ByteBuffer.allocate(5);
		buff_Bytes.putInt(Constant_Val.EMPTY);
		buff_Bytes.put(Constant_Val.UNCHOKE);
		byte[] msg_unchk = buff_Bytes.array();
		return msg_unchk;		
	}
	

	public byte[] interested_Message() {
		
		ByteBuffer buff_Bytes = ByteBuffer.allocate(5);
		buff_Bytes.putInt(Constant_Val.EMPTY);
		buff_Bytes.put(Constant_Val.INTERESTED);
		byte[] msg_intrst = buff_Bytes.array();
		return msg_intrst;
	}
	
	public byte[] not_Interested_Message() {
		
		ByteBuffer buff_Bytes = ByteBuffer.allocate(5);
		buff_Bytes.putInt(Constant_Val.EMPTY);
		buff_Bytes.put(Constant_Val.NOT_INTERESTED);
		byte[] msg_notintrst = buff_Bytes.array();
		return msg_notintrst;

	}

	
	public byte[] have_Message(byte[] payLoad) {
		ByteBuffer buff_Bytes = ByteBuffer.allocate(9);
		buff_Bytes.putInt(5);
		buff_Bytes.put(Constant_Val.HAVE);
		buff_Bytes.put(payLoad);
		byte[] msg_have = buff_Bytes.array();
		return msg_have;
		
	}	
	
	
	public byte[] bitfield_Message(byte[] payload) {
		int lengthOfpayload = payload.length;
		ByteBuffer buff_Bytes = ByteBuffer.allocate(lengthOfpayload+5);
		buff_Bytes.putInt(lengthOfpayload+1);
		buff_Bytes.put(Constant_Val.BITFIELD);
		buff_Bytes.put(payload);
		byte[] msg_bf = buff_Bytes.array();
		return msg_bf;
	}

	
	public byte[] request_Message(byte[] payLoad) {
		ByteBuffer buff_Bytes = ByteBuffer.allocate(9);
		buff_Bytes.putInt(5);
		buff_Bytes.put(Constant_Val.REQUEST);
		buff_Bytes.put(payLoad);
		byte[] msg_rqst = buff_Bytes.array();
		return msg_rqst;
	}
	
	
	public Handshake_Message checkHandShakeMsg(byte[] data) {
		return null;
	}
	
	
	public Message_Body checkPeer2PeerMsg(byte[] data) { 
		return null;
	}
	
	
	public Message_Main retrieve_Message_Instance(byte[] data) {
		if( data== null || data.length < 5)
			return null;
		
		
		byte dataKind = data[4];
		
		Message_Body msg_body = Message_Body.messageCreate();

		
		switch (dataKind)  {
			case Constant_Val.CHOKE:
				msg_body = Message_Body.messageCreate();
				msg_body.setMesssageType(Constant_Val.CHOKE);
				msg_body.setMessageLength(1);
				msg_body.setPieceInfo(null);
				return msg_body;
				
			case Constant_Val.UNCHOKE:
				msg_body = Message_Body.messageCreate();
				msg_body.setMesssageType(Constant_Val.UNCHOKE);
				msg_body.setMessageLength(1);
				msg_body.setPieceInfo(null);
				return msg_body;
				
			case Constant_Val.INTERESTED:
				msg_body = Message_Body.messageCreate();
				msg_body.setMesssageType(Constant_Val.INTERESTED);
				msg_body.setMessageLength(1);
				msg_body.setPieceInfo(null);
				return msg_body;	
				
			case Constant_Val.NOT_INTERESTED:
				msg_body = Message_Body.messageCreate();
				msg_body.setMesssageType(Constant_Val.NOT_INTERESTED);
				msg_body.setMessageLength(1);
				msg_body.setPieceInfo(null);
				return msg_body;			
				
			case Constant_Val.HAVE:
				msg_body = Message_Body.messageCreate();
				msg_body.setMessageLength(5);
				msg_body.setMessageLength(Constant_Val.HAVE);
				msg_body.setPieceNumber((int)data[8]);
				break;
				
			case Constant_Val.REQUEST:
				msg_body = Message_Body.messageCreate();
				msg_body.setMessageLength(5);
				msg_body.setMessageLength(Constant_Val.REQUEST);
				msg_body.setPieceNumber((int)data[8]);
				break;
		}
		return null;
	}
}