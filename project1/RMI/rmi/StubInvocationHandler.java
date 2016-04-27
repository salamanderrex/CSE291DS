package rmi;

import java.io.*;
import java.lang.reflect.*;
import java.lang.reflect.Proxy;
import java.net.*;


public class StubInvocationHandler implements InvocationHandler, Serializable {


    private InetSocketAddress address;
    private Class myInterface;

    public InetSocketAddress getAddress() {
            return address;
    }

    public Class getInterface() {
        return myInterface;
    }
   

    public StubInvocationHandler(InetSocketAddress address, Class c) {
        this.address = address;
        this.myInterface = c;
    }

    public Object invoke(Object proxy, Method method, Object[] arguments) throws Exception {


        if (method.getName().equals("toString") &&
                method.getReturnType().getName().equals("java.lang.String") &&
                method.getParameterTypes().length == 0) {

            StubInvocationHandler r = (StubInvocationHandler)
                    Proxy.getInvocationHandler(proxy);

            return "Interface: " + r.getInterface().getName() + " address: " + r.getAddress().toString();
        }


        if (method.getName().equals("hashCode") &&
                method.getReturnType().getName().equals("int") &&
                method.getParameterTypes().length == 0) {

            StubInvocationHandler r = (StubInvocationHandler)
                    Proxy.getInvocationHandler(proxy);

            return r.getInterface().hashCode() + r.getAddress().hashCode();
        }

        // check interface, ip address and port equals
        if (method.getName().equals("equals") &&
                method.getReturnType().getName().equals("boolean") &&
                method.getParameterTypes().length == 1) {

            if (arguments.length != 1 || arguments[0] == null)
                return false;

            Object b = arguments[0];
            StubInvocationHandler x = (StubInvocationHandler)
                    Proxy.getInvocationHandler(proxy);
            StubInvocationHandler y =
                    (StubInvocationHandler) Proxy.getInvocationHandler(b);


            if (x.getInterface().equals(y.getInterface()) &&
                     x.getAddress().toString().equals(y.getAddress().toString())  &&
                     (x.getAddress().getPort() == y.getAddress().getPort())) {
                return true;
            } else
                return false;
        }

        responseObject resultObject = null;

        try {
            Socket connection = new Socket(this.address.getHostName(), this.address.getPort());
            ObjectOutputStream out =
                    new ObjectOutputStream(connection.getOutputStream());
            out.flush();

            ObjectInputStream in =
                    new ObjectInputStream(connection.getInputStream());
            requestObject reqo = new requestObject(method, arguments);
            out.writeObject(reqo);
            resultObject = (responseObject) in.readObject();
            connection.close();

        } catch (IOException e) {
            throw new RMIException(e);
        } catch (Exception e) {
            throw new Exception(e);
        }


        if (resultObject.isException())
            throw (Exception) resultObject.getReturn();

        return resultObject.getReturn();
    }


    
}
