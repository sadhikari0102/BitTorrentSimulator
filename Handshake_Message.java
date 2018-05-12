public class Handshake_Message implements Message_Main
{
	private String peerID;	
	private static int numInstance;
	private int messageNumber;
	

	private Handshake_Message()
	{
		
	}
	
	public static Handshake_Message create_Handshake_Message() {
		
		Handshake_Message hndshk_msg = new Handshake_Message();
		
		boolean passed = true;
		
		numInstance++;
		
		if(passed){ 
			
			hndshk_msg.updateMsgIndex();
		}
		
		if(!passed) {
			hndshk_msg = null;
		}
		return hndshk_msg;
	}
	
	
	public void setPeerId(String peerID) {
		this.peerID = peerID;
	}
	
	private void updateMsgIndex(){
			messageNumber=numInstance;
	}
	
	
	public int getEachMessageType() {
		return Constant_Val.HANDSHAKE;
	}

	public int getLength() 
	{
		return 0;
	}
	
	public String getPeerId()
	{
		return peerID;
	}

	public int getMessageIndex() 
	{
		return messageNumber;
	}

	public int getMessageLength() 
	{
		return 0;
	}
}