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
        this.ifsf = getIFSF(c);
    }
    //IFSF means speical functions like toString hashCode
    private boolean [] ifsf;

    private static boolean[] getIFSF(Class c) {
        Method[] methods = c.getMethods();

        boolean [] return_ = {false, false, false};
        for (int i = 0; i < methods.length; i++) {
            Class[] exceptions = methods[i].getExceptionTypes();
            if (exceptions.length == 0) {
                continue;
            }
            for (int j = 0; j < exceptions.length; j++) {
                if (exceptions[j].getName().contains("RMIException")) {
                    if (methods[i].getName().equals("equals")) {
                        return_[0] = true;
                    }
                    if (methods[i].getName().equals("toString")) {
                        return_[1] = true;
                    }
                    if (methods[i].getName().equals("hashCode")) {
                        return_[2] = true;
                    }
                    break;
                } else if (j == exceptions.length - 1) {
                }
            }
        }
        return return_;
    }

    public Object invoke(Object proxy, Method method, Object[] arguments) throws Exception {



        if (method.getName().equals("toString") && !ifsf[1] &
                method.getReturnType().getName().equals("java.lang.String") &&
                method.getParameterTypes().length == 0) {

            StubInvocationHandler r = (StubInvocationHandler)
                    Proxy.getInvocationHandler(proxy);

            return "Interface: " + r.getInterface().getName() + " address: " + r.getAddress().toString();
        }


        if (method.getName().equals("hashCode") && !ifsf[2] &
                method.getReturnType().getName().equals("int") &&
                method.getParameterTypes().length == 0) {

            StubInvocationHandler r = (StubInvocationHandler)
                    Proxy.getInvocationHandler(proxy);

            return r.getInterface().hashCode() + r.getAddress().hashCode();
        }

        // check interface, ip address and port equals
        if (method.getName().equals("equals") && !ifsf[0] &
                method.getReturnType().getName().equals("boolean") &&
                method.getParameterTypes().length == 1) {
            System.out.println("IN equals........");

            if (arguments.length != 1 || arguments[0] == null)
                return false;
            try {
            Object b = arguments[0];
            StubInvocationHandler x = (StubInvocationHandler)
                    Proxy.getInvocationHandler(proxy);
            StubInvocationHandler y =
                    (StubInvocationHandler) Proxy.getInvocationHandler(b);



                if (x.getAddress() != null && y.getAddress() != null &&
                        x.getInterface() != null && y.getInterface() != null &&
                        x.getInterface().equals(y.getInterface()) &&
                        x.getAddress().equals(y.getAddress())) {
                    return true;
                } else
                    return false;
            } catch (Exception e) {
                return false;
            }
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
