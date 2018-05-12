import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;

public class Piece_Process {
	
	public static final String PREFIX_LOGS = Piece_Process.class.getSimpleName();
	private static Piece_Process piece_process;	
	int size_p;
	int numberOfPieces ;
	private static Bit_Fields bitField ;
	RandomAccessFile opStream;
	FileInputStream ipStream;
	
	
	
	private Piece_Process(){
		
	}
		
	
	public boolean initialize(boolean filePresentOrNot, String peerID){
		
		if(Peer_Property_Tokens.get_value("PieceSize")!=null)
			size_p = Integer.parseInt(Peer_Property_Tokens.get_value("PieceSize"));
		
			if(Peer_Property_Tokens.get_value("FileSize")!= null)
			numberOfPieces = (int) Math.ceil(Integer.parseInt(Peer_Property_Tokens.get_value("FileSize")) / (size_p*1.0)) ;
		

		try {
			bitField = new Bit_Fields(numberOfPieces);
			
			if(filePresentOrNot) {
				bitField.setBit();
			}
			
			String file_Out = new String();			
			file_Out = Peer_Property_Tokens.get_value("FileName");
			
			String dir_name = "peer_"+peerID;
			File dir = new File(dir_name);
			
			if(!filePresentOrNot){

				dir.mkdir();

			}
			
			file_Out = dir.getAbsolutePath()+"/"+file_Out;
			
			File outFile = new File(file_Out);
			if(outFile.exists()){
		
			}
			
			opStream = new RandomAccessFile(file_Out,"rw");
			
			opStream.setLength(Integer.parseInt(Peer_Property_Tokens.get_value(Constant_Val.FILE_SIZE)));
			
			return true;
			
		}
		catch(Exception e) {
			
		  e.printStackTrace();
		  return false;
		}	
	}

	
	
	synchronized public static Piece_Process createPieceControl(boolean iffileAvail, String peerID){
		if(piece_process == null){
			piece_process = new Piece_Process();
			boolean done = piece_process.initialize(iffileAvail,peerID);
			if(!done)
				piece_process = null;
	
		}
		return piece_process;
	}
	
	
		
	synchronized public void close(){
		try {
			if(opStream!= null){
				opStream.close();
			}
			
			if(ipStream != null){
				ipStream.close();
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
				
	}
	
	synchronized public Piece_Info getData(int index){
		
		Piece_Info pcData = new Piece_Info(size_p);
		
		if(bitField.getBitFieldIndividual(index)) {
			try{
				byte[] new_Data = new byte[size_p];
				opStream.seek(index*size_p);
				int sizeOfData = opStream.read(new_Data);
				
				if(sizeOfData != size_p){
					
					byte[] latestData = new byte[sizeOfData];
					for(int i=0 ; i<sizeOfData ; i++){
						latestData[i] = new_Data[i];
					}
					pcData.setPieceValue(new_Data);
				}else{
					pcData.setPieceValue(new_Data);
				}
				
				return pcData;
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
			
		return null;
	}
	
	synchronized public boolean pieceWriter(int index,Piece_Info one_pcinfo){
		
		if(!bitField.getBitFieldIndividual(index)) {
			try {
				
				opStream.seek(index*size_p);
				opStream.write(one_pcinfo.getPieceValue());
				
				bitField.setBitFieldIndividual(index, true);
				
				return true;
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		return false;
		
	}
	
	
	synchronized public int[] getAvailability() {
		int j=0;
		int access[] = new int[numberOfPieces];
		for (int i=0;i<bitField.getSize();i++) {
			if (bitField.getBitFieldIndividual(i)) {
				access[j++] = i;
			}
		}
		return access;
	}
	
	
	synchronized public int[] getMissing(){
	
		int count = 0;
		for (int i=0;i<bitField.getSize();i++) {
			
			if(! bitField.getBitFieldIndividual(i))
			{				
				count++;
			}
		}
		int piecesMissingList[] = new int[count];
		count = 0; 
		for ( int i = 0 ; i < bitField.getSize() ; i++ )
		{
			if(! bitField.getBitFieldIndividual(i)) {				
				piecesMissingList[count++] = i;
			}
											
		}				
		return piecesMissingList;

	}
	
	synchronized public boolean statusCheck() {
		return bitField.fileDownloadedCheck();
	}
	
	public Bit_Fields returnBF() {
		return bitField;
	}
}
