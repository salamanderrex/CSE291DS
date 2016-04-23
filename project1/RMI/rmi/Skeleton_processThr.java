package rmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;

public class Skeleton_processThr<T> extends Thread {
	private Socket my_sock;
	private Class<T> my_c;
	private T my_server;
	private Object sklt;

	public Skeleton_processThr(Socket socket, Class<T> given_c, T given_server, Object given_sklt)
	{
		this.my_sock = socket;
		this.my_c = given_c;
		this.my_server = given_server;
		this.sklt = given_sklt;
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
			} catch(IllegalAccessException e){
					/* response is an exception */
				Throwable t = new RMIException(e.getCause());
				response = new responseObject(true, t);
			} catch(IllegalArgumentException e) {
					/* response is an exception */
				Throwable t = new RMIException(e.getCause());
				response = new responseObject(true, t);
			} catch(InvocationTargetException e) {
					/* Underlying method threw an exception */
				response = new responseObject(true, e.getCause());
				os.writeObject(response);
			}
			os.writeObject(response);
			my_sock.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("The other side is terminated! Exiting...");
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
