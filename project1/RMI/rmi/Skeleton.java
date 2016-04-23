package rmi;
import java.net.*;
import java.io.IOException;
import java.lang.reflect.Method;

public class Skeleton<T> {
	private int port = 0;
	private Class<T> my_c;
	private T my_server;
	private InetSocketAddress my_address;
	private MutableUtil tool;
	private String HostName = null;
	private ServerSocket socketServer = null;
	//private volatile boolean isRunning = false;

	public Skeleton(Class<T> c, T server)
	{
		if(c == null || server == null) throw new NullPointerException("Invalid input for constructor of Skeleton!");
		else if(!isRemoteInterface(c)) throw new Error("Non-remote interface detected!");
		else if(c.isInterface()) {
			this.my_c = c;
			this.my_server = server;
			//this.my_address = new InetSocketAddress("localhost", 5000);
			this.tool = new MutableUtil(1);
		}
		else throw new Error("Input must be an interface!");
	}

	public Skeleton(Class<T> c, T server, InetSocketAddress address)
	{
		if(c == null || server == null || address == null) throw new NullPointerException("Invvalid input for constructor of Skeleton!");
		else if(!isRemoteInterface(c)) throw new Error("Non-remote interface detected!");
		else if(c.isInterface()) {
			this.my_c = c;
			this.my_server = server;
			this.my_address = address;
            this.port = address.getPort();
			this.tool = new MutableUtil(1);
		}
		else throw new Error("Input must be an interface!");
	}

	// Helper public func
	public InetSocketAddress getAddr()
	{
		return this.my_address;
	}

	public synchronized int getUtil()
	{
		return this.tool.stop;
	}

	protected void stopped(Throwable cause)
	{
		this.tool.stop = 1;
	}

	// Never called!
	protected boolean listen_error(Exception exception)
	{
		return false;
	}

	// Never called!
	protected void service_error(RMIException exception)
	{

	}

	protected boolean isRemoteInterface(Class<T> c)
	{
		Method[] met = c.getMethods();
		for(int i = 0; i < met.length; i++) {
			//
			//System.out.println("Method: " + met[i].getName());
			// Find all exceptions thrown by a certain method in this interface
			Class[] ex = met[i].getExceptionTypes();
			boolean found = false;
			for(int j = 0; j < ex.length; j++) {
				//
				//System.out.println("==> " + ex[j].getName());
				//
				if(ex[j].getTypeName().equals("rmi.RMIException")) {
					found = true;
					break;
				}
			}
			//
			if(found) continue;
			else return false;
		}
		//
		return true;
	}

	public synchronized void start() throws RMIException
	{
		if(this.my_address == null) this.my_address = new InetSocketAddress("localhost", 8888);
        this.port = this.my_address.getPort();
		//
		if(this.tool.stop == 1)
		{
			System.out.println("The skeleton starts now!");
			this.tool.stop = 0;
			try{
				 this.socketServer = new ServerSocket(this.my_address.getPort());
			} catch (Exception e) {
				throw new RMIException("ServerSocker create fail");
			}


			Skeleton_listenThr<T> my_listenThr = new Skeleton_listenThr<T>(this.my_address, this.tool, this.my_c, this.my_server, this,socketServer);
			my_listenThr.start();
		}
		else System.out.println("This skeleton has already started!");
		//
		notify();
		System.out.println("end of start() in skeleton........");
	}

	public synchronized void stop()
	{

		System.out.println("in skeleton Stop()..... ");
		if(this.tool.stop == 1) System.out.println("Already stopped!");
		else
		{
			//
			this.tool.stop = 2; // send stop req
			//
			Socket StopSign = new Socket();
			try {
				StopSign.connect(this.my_address);
				//
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//
			while(this.tool.stop != 1)
			{
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//
			stopped(new Throwable());
			System.out.println("Stopping skeleton!");
		}
		//
		notify();
	}
	public synchronized boolean isRunning() {
			return this.tool.stop == 0;
		}
	public String getHostName() {
		return HostName;
	}

    public int getPort() {
        return port;
    }

	public Object getServer() {
		return my_server;
	}

	public Class getIntface() {
		return my_c;
	}
}
