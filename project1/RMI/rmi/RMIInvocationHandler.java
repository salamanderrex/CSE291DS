package rmi;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;

public class RMIInvocationHandler implements InvocationHandler, Serializable {


	private InetSocketAddress address;
	private Class myInterface;
	
	public RMIInvocationHandler(InetSocketAddress address, Class c) {

		this.address = address;
		this.myInterface = c;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Exception  {
		
		/* Returns the name of implementing interface and network address 
		 * if local method toString is called */
		if(method.getName().equals("toString") && method.getReturnType().
				getName().equals("java.lang.String") && method.
				getParameterTypes().length == 0) {
			RMIInvocationHandler r = (RMIInvocationHandler) 
					java.lang.reflect.Proxy.getInvocationHandler(proxy);
			
			return r.getInterface().getName() + " " + r.getAddress().toString();
		}
		
		/* Returns a hashCode based on implementing interface and network address 
		 * if local method hashCode is called */
		if(method.getName().equals("hashCode") && method.getReturnType().getName()
				.equals("int") && method.getParameterTypes().length == 0) {
			
			RMIInvocationHandler r = (RMIInvocationHandler) 
					java.lang.reflect.Proxy.getInvocationHandler(proxy);
			
			return r.getInterface().hashCode() * r.getAddress().hashCode();
		}
		
		/* Determines if two proxy objects are equal based on the interface 
		 * they implement and address they connect to */
		if(method.getName().equals("equals")&&method.getReturnType().getName().
				equals("boolean") && method.getParameterTypes().length == 1) {
			
			if(args.length != 1 || args[0] == null)
				return false;
			
			RMIInvocationHandler r = (RMIInvocationHandler) 
					java.lang.reflect.Proxy.getInvocationHandler(proxy);
			RMIInvocationHandler q = 
					(RMIInvocationHandler) java.lang.reflect.
					Proxy.getInvocationHandler(args[0]);
			
			if(r.getInterface().equals(q.getInterface())
					&& r.getAddress().equals(q.getAddress()) ) {
				return true;
			}
			else
				return false;		
		}
		
		Socket connection;
		responseObject serverReturn = null;
		
		try {
			/* Connects to server and forwards information and 
			 * Receives a response. Throws an RMIException if 
			 * Problems occurred */
			connection = new Socket(this.address.getHostName(), this.address.getPort());
			ObjectOutputStream out = 
					new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			ObjectInputStream in = 
					new ObjectInputStream(connection.getInputStream());
			/* Sends method, parameter types, return type,
			 * and arguements */
			out.writeObject(method.getName());
			out.writeObject(method.getParameterTypes());
			out.writeObject(method.getReturnType().getName());
			out.writeObject(args); 
			serverReturn = (responseObject) in.readObject();			
			connection.close();
		} catch (IOException e) {
			throw new RMIException(e.getCause());
		} catch (ClassNotFoundException e) {
			throw new RMIException(e.getCause());
		}
			
		/* if the method on the server threw an exception, 
		 * then the local proxy object will too */
		if(serverReturn.isException()) 
			throw (Exception) serverReturn.getReturn();
		
		return serverReturn.getReturn();
	}
	
	/* Helper methods to retrieve private variables */
	public Class getInterface() {
		return myInterface;
	}
	
	public InetSocketAddress getAddress() {
		return address; 
	}	
}
