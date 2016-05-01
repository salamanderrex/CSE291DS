package rmi;

import java.io.IOException;
import java.net.*;

public class SkeletonListenThread<T> extends Thread {
	private Class<T> my_c;
	private T my_server;
	private InetSocketAddress my_address;
	private MutualSig mux;
	private Skeleton sklt;
	private ServerSocket skeleton_server;

	public SkeletonListenThread(InetSocketAddress address,
								MutualSig mux, Class<T> c,
								T server, Skeleton sklt,
								ServerSocket socketServer)
	{
		this.my_address = address;
		this.mux = mux;
		this.my_c = c;
		this.my_server = server;
		this.sklt = sklt;
		this.skeleton_server = socketServer;
	}

	public void run()
	{
		try {
			while(this.mux.stop != 1)
			{
				Socket req = skeleton_server.accept();
				synchronized(this.sklt)
				{
					if(this.mux.stop == 2)
					{
						this.mux.stop = 1;
						this.sklt.notify();
						skeleton_server.close();
						return;
					}
				}
				SkeletonInvocationHandler<T> process_req = new SkeletonInvocationHandler<T>(req, this.my_c, this.my_server, this.sklt);
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
			if(this.mux.stop == 2) skeleton_server.accept();
			//
			synchronized(this.sklt)
			{
				this.mux.stop = 1;
				this.sklt.notify();
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
