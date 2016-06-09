package rmi;
import rmi.*;
import rmi.RMIException;

import java.net.*;
import java.io.IOException;
import java.lang.reflect.Method;

public class Skeleton<T> {
	private int port = 0;
	private Class<T> my_c;
	private T my_server;
	private InetSocketAddress my_address;
	private MutualSig tool;
	private String HostName = null;
	private ServerSocket socketServer = null;

	public Skeleton(Class<T> c, T server)
	{
		if(c == null || server == null) throw new NullPointerException("No interface specified");
		else if(!isRemoteInterface(c)) throw new Error("Not remote interface");
		else if(c.isInterface()) {
			this.my_c = c;
			this.my_server = server;
			this.tool = new MutualSig(1);
		}
		else throw new Error("Not interface");
	}

	public Skeleton(Class<T> c, T server, InetSocketAddress address)
	{
		if(c == null || server == null) throw new NullPointerException("No interface specified");
		else if(!isRemoteInterface(c)) throw new Error("Not remote interface");
		else if(c.isInterface()) {
			this.my_c = c;
			this.my_server = server;
			this.my_address = address;

			this.tool = new MutualSig(1);
			if(address != null) {
				this.port = address.getPort();
				this.HostName = address.getHostName();
			}

		}
		else throw new Error("not interface");

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
	protected void service_error(rmi.RMIException exception)
	{

	}

	protected boolean isRemoteInterface(Class<T> c)
	{
		Method[] met = c.getMethods();
		for(int i = 0; i < met.length; i++) {

			Class[] ex = met[i].getExceptionTypes();
			boolean found = false;
			for(int j = 0; j < ex.length; j++) {
				if(ex[j].getTypeName().equals("rmi.RMIException")) {
					found = true;
					break;
				}
			}
			if(found) continue;
			else return false;
		}

		return true;
	}

	public synchronized void start() throws rmi.RMIException
	{
		if(this.my_address == null) this.my_address = new InetSocketAddress("localhost", 9999);
        this.port = this.my_address.getPort();
		//
		if(this.tool.stop == 1)
		{
			this.tool.stop = 0;
			try{
				 this.socketServer = new ServerSocket(this.my_address.getPort());
			} catch (Exception e) {
				throw new RMIException("ServerSocker create fail");
			}


			SkeletonListenThread<T> my_listenThr = new SkeletonListenThread<T>(this.my_address, this.tool, this.my_c, this.my_server, this,socketServer);
			my_listenThr.start();
		}
		//
		notify();
	}

	public synchronized void stop()
	{

		if(this.tool.stop != 1)
		{
			//
			this.tool.stop = 2; // send stop req
			Socket StopSign = new Socket();
			try {
				StopSign.connect(this.my_address);
				//
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(this.tool.stop != 1)
			{
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			stopped(new Throwable());
		}
		notify();
	}
	public synchronized boolean isRunning() {
			return this.tool.stop == 0;
		}
	public String getHostName() {
		return this.HostName;
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
