package rmi;

import java.io.IOException;
import java.net.*;

public class SkeletonListenThread<T> extends Thread {
	private Class<T> my_c;
	private T my_server;
	private InetSocketAddress my_address;
	private MutualSig tool;
	private Skeleton lock;
	private ServerSocket skeleton_server;

	public SkeletonListenThread(InetSocketAddress given_address,
								MutualSig given_tool, Class<T> given_c,
								T given_server, Skeleton given_lock,
								ServerSocket socketServer)
	{
		this.my_address = given_address;
		this.tool = given_tool;
		this.my_c = given_c;
		this.my_server = given_server;
		this.lock = given_lock;
		this.skeleton_server = socketServer;
	}

	public void run()
	{
		try {
			while(this.tool.stop != 1)
			{
				Socket req = skeleton_server.accept();
				synchronized(this.lock)
				{
					if(this.tool.stop == 2)
					{
						this.tool.stop = 1;
						this.lock.notify();
						skeleton_server.close();
						return;
					}
				}
				InetAddress detail = req.getInetAddress();
				SkeletonInvocationHandler<T> process_req = new SkeletonInvocationHandler<T>(req, this.my_c, this.my_server, this.lock);
				process_req.start();
				/*
				ExecutorService executor = Executors.newSingleThreadExecutor();
				Future<String> future = executor.submit(new Callable(){
					public void call() throws Exception{
						process_req.start();
					}
				});
				*/
			}
			//
			if(this.tool.stop == 2) skeleton_server.accept();
			//
			synchronized(this.lock)
			{
				this.tool.stop = 1;
				this.lock.notify();
			}
			//
			if(!skeleton_server.isClosed()) skeleton_server.close();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
