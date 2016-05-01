package rmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;

public class SkeletonInvocationHandler<T> extends Thread {
	private Socket my_sock;
	private Class<T> my_c;
	private T my_server;
	private Skeleton sklt;

	public SkeletonInvocationHandler(Socket socket, Class<T> c, T server, Skeleton sklt)
	{
		this.my_sock = socket;
		this.my_c = c;
		this.my_server = server;
		this.sklt = sklt;
	}

	@SuppressWarnings("unchecked")
	public void run()
	{
		try {
			Object ret = null;
			InputStream iss = this.my_sock.getInputStream();

			ObjectOutputStream os = new ObjectOutputStream(this.my_sock.getOutputStream());
			os.flush();
			ObjectInputStream is = new ObjectInputStream(iss);
			if(is == null) return;
			requestObject reO = (requestObject) is.readObject();
			Object method_name = reO.MethodName;
			Object parameterTypes = reO.ParameterTypes;
			Object returnType = reO.ReturnTypes;
			Object[] args = reO.args;

			if(method_name == null) return;
			Method serverMethod;
			responseObject response = null;
			try {
				serverMethod = this.my_c.getMethod((String) method_name, (Class[]) parameterTypes);
			} catch( NoSuchMethodException e ){
				System.out.println("no such method found!");
				Throwable t = new RMIException(e.getCause());
				response = new responseObject(true, t);
				os.writeObject(response);
				my_sock.close();
				return;
			}
			if(!returnType.equals(serverMethod.getReturnType().getName())) {
				Throwable t = new RMIException("Return Type Mismatch");
				response = new responseObject(true, t);
			}

			try {
				Object serverReturn = serverMethod.
						invoke(my_server, args);
				response = new responseObject(false, serverReturn);
					/* response in not an exception */
			}catch (Exception e){
				response = new responseObject(true, e.getCause());
			}
			os.writeObject(response);
			my_sock.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			sklt.service_error(new RMIException("client cloesed"));
			return;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
