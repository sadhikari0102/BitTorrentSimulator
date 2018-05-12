import java.io.Serializable;


public class Piece_Info implements Serializable
{
	private byte[] pieceInfoArray;
	int pieceSize;

	

	public byte[] getPieceValue()  {
		return pieceInfoArray;
	}

	public void setPieceValue(byte[] data) {
		this.pieceInfoArray = data;
	}
	
	public int getPieceSize() {
		if(pieceInfoArray != null)
			return pieceInfoArray.length;
		else
			return -1;			
	}
	
	public Piece_Info(int pieceSize) {
		
		this.pieceSize = pieceSize;
	}
}