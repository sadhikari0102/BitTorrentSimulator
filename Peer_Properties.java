import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class Peer_Properties {
	
public Map<String,Peers_Info> peerMapInfo = null;
	
private static Peer_Properties  peer_Config = null;

	
	public static Peer_Properties instanceCreation(){
		if( peer_Config == null){
			 peer_Config = new  Peer_Properties();
			 peer_Config.fillMap();
		}
		return  peer_Config;
	}
	
	public boolean fillMap(){		
		try {

			BufferedReader br =  new BufferedReader(new InputStreamReader(new FileInputStream(Constant_Val.PEER_INFO_FILE)));
			
			peerMapInfo = new HashMap<String,Peers_Info>();
			
			String peerPropsLine = br.readLine();
			
			Peers_Info peer_Inst = null;
			while(peerPropsLine != null){
				peer_Inst = new Peers_Info();
				String set_token[] = peerPropsLine.trim().split(" ");
				peer_Inst.setID(set_token[0]);
				peer_Inst.setAddress(set_token[1]);
				peer_Inst.setPortNumber(Integer.parseInt(set_token[2]));
				
				if(set_token[3].equals("1")){
					peer_Inst.fileStatus(true);
				}else{
					peer_Inst.fileStatus(false);
				}
				
				peerMapInfo.put(set_token[0],peer_Inst);
				
				peerPropsLine = br.readLine();
			}			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return true;
	}
	
	public Map<String, Peers_Info> getInfo_Map() {
		return peerMapInfo;
	}

}
