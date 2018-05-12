import java.io.Serializable;

public class Bit_Fields implements Serializable
{
	private boolean ArrayOfBits[];
	private int size;
	
	//parameterized constructor
	public Bit_Fields(int numberOfPieces)
	{
		//initialize the array with the number of bits
		ArrayOfBits = new boolean[numberOfPieces];
		size = numberOfPieces;
		
		for(int i = 0; i < size; i++)
			ArrayOfBits[i] = false;
	
	}

	public void setBit()
	{
		for(int i=0 ; i<ArrayOfBits.length ; i++)
			ArrayOfBits[i] = true;
	}
	
	
	public int getSetBitCount()
	{
		int total = 0;
		for(int i = 0; i < this.ArrayOfBits.length; i++){
			if(this.ArrayOfBits[i]==true)
				total++;
		}
		return total;
	}
	
	
	public boolean fileDownloadedCheck() {	
		if(ArrayOfBits==null || ArrayOfBits.length==0) {
			return false;
		}
		
		for(int increment = 0 ; increment < this.getSize() ; increment++){
			
			if(ArrayOfBits[increment] != true){
				return false;
			}
			
		}
		return true;
	}
	
	public int getSize() {
		return size;
	}
	
	
	public boolean[] getBitFieldArray() {
		return ArrayOfBits;
	}
	
	
	public void setBitFieldArray(boolean[] ArrayOfBits) {
		this.ArrayOfBits = ArrayOfBits;
	}
	
	
	public boolean getBitFieldIndividual(int no) {
		return ArrayOfBits[no];
	}
	
	
	synchronized public void setBitFieldIndividual(int index, boolean bool) {
		ArrayOfBits[index] = bool;
	}

}