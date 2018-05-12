import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class Unchoke implements Runnable{
	
	public ScheduledFuture<?> taskUnchoke = null;
    public ScheduledExecutorService taskScheduler = null;
    private static Unchoke unchokeHandler = null;
    private Connector threadControllerUnchoke = null;
    private Update_Logs log = null;
    
    
    public static synchronized Unchoke instanceCreate(Connector connecter){
	    	if(unchokeHandler == null)	{
	    		
	    		if(connecter == null)
	    			return null;
	    		  		
	    		unchokeHandler = new Unchoke();
	    		boolean done = unchokeHandler.init();
	    		
	    		if(!done ) {
	    			unchokeHandler.taskUnchoke.cancel(true);
	    			unchokeHandler = null;
	    			return null;
	    		}
	    		
	    		unchokeHandler.threadControllerUnchoke = connecter;
	    		unchokeHandler.log = connecter.getLogInstance();
	    	}	
	    	
	    	return unchokeHandler;
    }
    
    private boolean init() {
	    	taskScheduler = Executors.newScheduledThreadPool(1);    	
	    	return true;
    }
    
	public void run() {
		ArrayList<String> chkList = threadControllerUnchoke.choked;

		if(chkList.size() > 0) {
			Random randomEve = new Random();
			threadControllerUnchoke.unchoke(chkList.get(randomEve.nextInt(chkList.size())));
		}
		
		threadControllerUnchoke.statusCheck();
		
		if(threadControllerUnchoke.message_control.statusCheck()) {
			log.info("Peer "+threadControllerUnchoke.getPeerId()+" has downloaded the complete file.");
			threadControllerUnchoke.shutdown();
		}
	}
}