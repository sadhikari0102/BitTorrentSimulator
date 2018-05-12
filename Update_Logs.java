import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class Update_Logs extends Logger
{
	private String filename;
	private FileHandler file_control;

	public static Update_Logs logs = null;
	
	private String peerId;	
	private SimpleDateFormat time = null;
	
	
	public static Update_Logs getLogger(String peerId) {
		
		if (logs == null)  {
			String dir = "" + Constant_Val.DIR;
			File fl1 = new File(dir);
			fl1.mkdir();
			logs = new Update_Logs(peerId, dir + "/" + Constant_Val.LOG_FILENAME + peerId + ".log", Constant_Val.LOGGER);
			try {
				logs.init();
			} catch (Exception e) {
				logs.close();
				logs = null;
				System.out.println("Logger Not Initialized");
				e.printStackTrace();
			}
		}
		return logs;
	}
	
	public Update_Logs(String peerId, String filename, String name) {
		super(name, null);
		this.filename= filename;
		this.setLevel(Level.FINEST);
		this.peerId = peerId;
	}
	
	public void init() throws SecurityException, IOException	{
		
		file_control = new FileHandler(filename);
		file_control.setFormatter(new CustomLogFormatter());
		time= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.addHandler(file_control);
	}
	
	@Override
	public synchronized void log(Level lvl, String msg) {
		
		super.log(lvl, msg+"\n");
	}
	
	public synchronized void info(String msg) {
		
		Calendar c = Calendar.getInstance();
		String dateInStringFormat = time.format(c.getTime());
		this.log(Level.INFO, "["+dateInStringFormat+"] : "+msg);
	}
	
	public void close()
	{
		try
		{
			if(file_control != null)
			{
				file_control.close();
			}
		}
		catch (Exception e)
		{			
			System.out.println("Logger not closed.");
			e.printStackTrace();
		}
	}
	
	public void warn(String msg)
	{
		Calendar c = Calendar.getInstance();		
		String dateInStringFormat = time.format(c.getTime());
		this.log(Level.WARNING, "["+dateInStringFormat+"]: Peer [peer_ID "+peerId+"] "+msg);
	}

}
