public class Constant_Val {
	
	public static String getMessage(int k){
		switch (k) {
		case Constant_Val.REQUEST:
			return "MESSAGE_REQUEST";
			
		case Constant_Val.HANDSHAKE:
			return "MESSAGE_HANDSHAKE";
		
		case Constant_Val.CHOKE:
			return "MESSAGE_CHOKE";
			
		case Constant_Val.UNCHOKE:
			return "MESSAGE_UNCHOKE";
			
		case Constant_Val.HAVE:
			return "MESSAGE_HAVE";
		
		case Constant_Val.BITFIELD:
			return "MESSAGE_BITFIELD";

		case Constant_Val.INTERESTED:
			return "MESSAGE_INTERESTED";
			
		case Constant_Val.NOT_INTERESTED:
			return "MESSAGE_NOT_INTERESTED";
			
		case Constant_Val.SHUTDOWN:
			return "MESSAGE_SHUTDOWN";
		
		case Constant_Val.PIECE:
			return "MESSAGE_PIECE";
		}
		return null;
	}
	
	public static final int RAW_DATA = 1000;
	public static final byte CHOKE = 0;	
	public static final byte UNCHOKE = 1;
	public static final byte INTERESTED = 2;
	public static final byte NOT_INTERESTED = 3;
	public static final byte HAVE = 4;
	public static final byte BITFIELD = 5;
	public static final byte REQUEST = 6;
	public static final byte PIECE = 7;
	public static final byte HANDSHAKE = 9;
	public static final byte SHUTDOWN = 100;
	
	public static final String CONFIG_FILE = "Common.cfg";
	public static final String PEER_INFO_FILE = "PeerInfo.cfg";
	
	public static final int QUEUE = 100;
	public static final String INTERVAL = "UnchokingInterval";
	public static final String OPT_INTERVAL = "OptimisticUnchokingInterval";
	public static final String DIR = "Logs";
	public static final String LOG_FILENAME = "log_peer_";
	public static final String HANDSHAKE_HEADER = "P2PFILESHARINGPROJ";
	public static final String LOGGER = "Update_Logs.name";

	public static final String FILE_SIZE = "FileSize";
	public static final int EMPTY = 1;
	public static final int MSG_SIZE_MAX = 40000;

}
