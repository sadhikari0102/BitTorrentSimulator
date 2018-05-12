import java.util.Scanner;

public class peerProcess {
	public static void main(String args[]) throws Exception{	
		Scanner sc = new Scanner(System.in);
		Connector controller = Connector.intializePeer(args[0]);
		controller.launch();
	}
}
