public class Message_Body implements Message_Main
{
	
	private static final long serialVersionUID = 1L;
	private int length;
	private int type;
	private int pieceIndex;
	private Piece_Info data;
	private Bit_Fields bitFieldhandler = null;
	public int messageInd = 0;
	private static int count = 0;
	
	private Message_Body() {		
	}
	
	public static Message_Body messageCreate() {
		Message_Body msg = new Message_Body();
		
		boolean done = msg.initialize();
		
		if(!done){
		
			msg = null;
		}
		
		return msg;
	}
	
	
	private boolean initialize() {
		count++;
		messageInd = count;
		return true;
	}
	
	
	public byte[] getMessage() {
		return null;
	}
	
	
	public int getMessageLength() {
		return this.length;
	}
	
	public void setMessageLength(int msg_len) {
		this.length = msg_len;
	}
	
	
	public int getEachMessageType() {
		return this.type;
	}	
	
	
	public int getMessageType() {
		return type;
	}

	
	public void setMesssageType(int mag_type) {
		this.type = mag_type;
	}

	
	public Piece_Info getPieceInfo() {
		return data;
	}

	
	public void setPieceInfo(Piece_Info info) {
		this.data = info;
	}
	
	
	public int getPieceNumber() {
		return pieceIndex;
	}

	
	public void setPieceNumber(int pc_num) {
		this.pieceIndex = pc_num;
	}
	
	
	public Bit_Fields getBitFieldsObject() {
		return bitFieldhandler;
	}

	
	public void setBitFieldsObject(Bit_Fields bfcontrol) {
		this.bitFieldhandler = bfcontrol;
	}

	
	public int getMessageIndex() {
		return messageInd;
	}	
}
