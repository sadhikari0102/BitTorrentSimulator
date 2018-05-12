public class Peers_Info 
{	
	private String ID;	
	private int portNumber;
	private boolean fileExists;
	private String address;

	
	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public boolean containsFile() {
		return fileExists;
	}

	public void fileStatus(boolean fileOrNot) {
		this.fileExists = fileOrNot;
	}

	public String getAddress()  {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
}