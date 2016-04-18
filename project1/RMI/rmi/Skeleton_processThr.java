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
			//
			InputStream iss = this.my_sock.getInputStream();
			/*
			if(iss.available() <= 0)
			{
				System.out.println("Process thread: Nothing in socket!");
				return;
			}
			*/
			//
			ObjectOutputStream os = new ObjectOutputStream(this.my_sock.getOutputStream());
			os.flush();
			ObjectInputStream is = new ObjectInputStream(iss);
			if(is == null) return;



			requestObject reO = (requestObject) is.readObject();
			Object method_name = reO.getName();
			Object parameterTypes = reO.getParameterTypes();
			Object returnType = reO.getReturnType();
			Object[] args = reO.getArgs();

			// Get desired method name and parameter
			//String method_name = (String)is.readObject();
			// Terminate as needed
			if(method_name == null) return;
			// Get the length of the para list
			//Integer para_len = (Integer)is.readObject();
			// Get para, watch for stub obj
			//Object[] para = new Object[para_len];
			//Method serverMethod = sklt.getIntface().getMethod((String)methodName,(Class[])parameterTypes);
			//for(int xx = 0; xx < para_len; xx++)
			//{
			//	para[xx] = (Object)is.readObject();
				//String chk_stub = para[xx].toString();
				//if(chk_stub.equals("$$Proxy")) para[xx] = Stub.create(this.my_c, (Skeleton<T>) this.sklt);
			//}
			Method serverMethod;
			responseObject response = null;
			// solve for result
			//Method[] candidate = this.my_c.getMethods();
			//boolean success = false;
			//boolean raiseException = false;
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
			/*
			for(int i = 0; i < candidate.length; i++)
			{
				if(candidate[i].getName().equals(method_name)) // make sure the name matches
				{
					if((para == null && candidate[i].getParameterCount() == 0) || (candidate[i].getParameterCount() == para.length)) // make sure the num of para matches
					{
						Class<?>[] para_type = candidate[i].getParameterTypes();
						boolean con = true;
						int my_length = para == null ? 0 : para.length;
						for(int j = 0; j < my_length; j++) // make sure the type of para matches
						{
							if(para[j] == null) {}
							else
							{
								String compA = para[j].getClass().getSimpleName().toLowerCase();
								String compB = para_type[j].getSimpleName().toLowerCase();
								if(!compA.contains(compB))
								{
									if(compA.contains("$proxy"))
									{
										para[j] = para_type[j].cast(para[j]); // cast from dynamic $proxy obj to what it should be
									}
									else
									{
										con = false;
										break;
									}
								}
							}
						}
						//
						if(con)
						{
							try
							{
								success = true;
								ret = candidate[i].invoke(this.my_server, para);
							}
							catch(InvocationTargetException t)
							{
								raiseException = true;
								ret = t.getTargetException();
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
			// return

			if(success)
			{
				os.writeObject(success);
				os.flush();
				os.writeObject(raiseException);
				os.flush();
				os.writeObject(ret);
				os.flush();
			}
			else // There is no such Method
			{
				os.writeObject(success);
				os.flush();
			}
		*/
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
