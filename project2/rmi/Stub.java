package rmi;

import rmi.Skeleton;
import rmi.StubInvocationHandler;

import java.net.*;
import java.lang.reflect.*;
import java.lang.reflect.Proxy;

/**
 * RMI stub factory.
 * <p>
 * <p>
 * RMI stubs hide network communication with the remote server and provide a
 * simple object-like interface to their users. This class provides methods for
 * creating stub objects dynamically, when given pre-defined interfaces.
 * <p>
 * <p>
 * The network address of the remote server is set when a stub is created, and
 * may not be modified afterwards. Two stubs are equal if they implement the
 * same interface and carry the same remote server address - and would
 * therefore connect to the same skeleton. Stubs are serializable.
 */
public abstract class Stub {
    /**
     * Creates a stub, given a skeleton with an assigned adress.
     * <p>
     * <p>
     * The stub is assigned the address of the skeleton. The skeleton must
     * either have been created with a fixed address, or else it must have
     * already been started.
     * <p>
     * <p>
     * This method should be used when the stub is created together with the
     * skeleton. The stub may then be transmitted over the network to enable
     * communication with the skeleton.
     *
     * @param c        A <code>Class</code> object representing the interface
     *                 implemented by the remote object.
     * @param skeleton The skeleton whose network address is to be used.
     * @return The stub created.
     * @throws IllegalStateException If the skeleton has not been assigned an
     *                               address by the user and has not yet been
     *                               started.
     * @throws UnknownHostException  When the skeleton address is a wildcard and
     *                               a port is assigned, but no address can be
     *                               found for the local host.
     * @throws NullPointerException  If any argument is <code>null</code>.
     * @throws Error                 If <code>c</code> does not represent a remote interface
     *                               - an interface in which each method is marked as throwing
     *                               <code>RMIException</code>, or if an object implementing
     *                               this interface cannot be dynamically created.
     */
    public static <T> T create(Class<T> c, Skeleton<T> skeleton)
            throws UnknownHostException {


        //System.out.println("in Stub create");
        //System.out.println("well, c is " + c);
        //System.out.println("well skeleton is " + skeleton);
        if (c == null || skeleton == null) {
            System.out.println("I want to throw NullPointerException");
            throw new NullPointerException();
        }

        //System.out.println("checkpoint1");
        if (!c.isInterface()) {
            throw new Error("not a interface ");
        }
        if (!checkRMIException(c)) {
            throw new Error("Has method does not have RMI Exception type");
        }
        //System.out.println("checkpoint2");

        //System.out.println("hostname is " + skeleton.getHostName());
        //System.out.println("is running" + skeleton.isRunning());
        if (skeleton.getHostName() == null && skeleton.isRunning() == false) {
            System.out.println("I want to throw IllegalStateException");
            throw new IllegalStateException();
        }

        //System.out.println("checkpoint3");
        if (skeleton.getHostName() != null && skeleton.getHostName().equals("0.0.0.0")) {
            try {
                //System.out.println("here in wild card");
                InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                //System.out.println("wildcard but no localhost");
                throw new UnknownHostException();

            }
        }
        //System.out.println("checkpoint4");
        InetSocketAddress address = skeleton.getAddr();


        InvocationHandler handler = new StubInvocationHandler(address, c);
        //System.out.println("checkpoint5");

        return (T) Proxy.newProxyInstance(c.getClassLoader(),
                new Class[]{c}, handler);

    }

    /**
     * Creates a stub, given a skeleton with an assigned address and a hostname
     * which overrides the skeleton's hostname.
     * <p>
     * <p>
     * The stub is assigned the port of the skeleton and the given hostname.
     * The skeleton must either have been started with a fixed port, or else
     * it must have been started to receive a system-assigned port, for this
     * method to succeed.
     * <p>
     * <p>
     * This method should be used when the stub is created together with the
     * skeleton, but firewalls or private networks prevent the system from
     * automatically assigning a valid externally-routable address to the
     * skeleton. In this case, the creator of the stub has the option of
     * obtaining an externally-routable address by other means, and specifying
     * this hostname to this method.
     *
     * @param c        A <code>Class</code> object representing the interface
     *                 implemented by the remote object.
     * @param skeleton The skeleton whose port is to be used.
     * @param hostname The hostname with which the stub will be created.
     * @return The stub created.
     * @throws IllegalStateException If the skeleton has not been assigned a
     *                               port.
     * @throws NullPointerException  If any argument is <code>null</code>.
     * @throws Error                 If <code>c</code> does not represent a remote interface
     *                               - an interface in which each method is marked as throwing
     *                               <code>RMIException</code>, or if an object implementing
     *                               this interface cannot be dynamically created.
     */
    public static <T> T create(Class<T> c, Skeleton<T> skeleton,
                               String hostname) {


        if (c == null || skeleton == null || hostname == null) {
            throw new NullPointerException();
        }

        if (!c.isInterface()) {
            throw new Error("now a interface ");
        }
        if (!checkRMIException(c)) {
            throw new Error("Has method does not have RMI Exception type");
        }

        if (skeleton.getPort() == 0) {
            throw new IllegalStateException();
        }

        InetSocketAddress address = new InetSocketAddress(hostname,
                skeleton.getPort());


        InvocationHandler handler = new StubInvocationHandler(address, c);


        return (T) Proxy.newProxyInstance(c.getClassLoader(), new Class[]{c}, handler);


    }

    /**
     * Creates a stub, given the address of a remote server.
     * <p>
     * <p>
     * This method should be used primarily when bootstrapping RMI. In this
     * case, the server is already running on a remote host but there is
     * not necessarily a direct way to obtain an associated stub.
     *
     * @param c       A <code>Class</code> object representing the interface
     *                proxyemented by the remote object.
     * @param address The network address of the remote skeleton.
     * @return The stub created.
     * @throws NullPointerException If any argument is <code>null</code>.
     * @throws Error                If <code>c</code> does not represent a remote interface
     *                              - an interface in which each method is marked as throwing
     *                              <code>RMIException</code>, or if an object implementing
     *                              this interface cannot be dynamically created.
     */
    public static <T> T create(Class<T> c, InetSocketAddress address) {

        if (c == null || address == null) {
            throw new NullPointerException();
        }

        if (!c.isInterface()) {
            throw new Error("now a interface ");
        }
        if (!checkRMIException(c)) {
            throw new Error("Has method does not have RMI Exception type");
        }


        InvocationHandler handler = new StubInvocationHandler(address, c);


        return (T) Proxy.newProxyInstance(c.getClassLoader(), new Class[]{c}, handler);

    }

    /*
     * check all methods has exception RMI exception
     */





    private static boolean checkRMIException(Class c) {

        Method[] methods = c.getMethods();
        if (methods.length == 0) {
            return true;
        }


        for (int i = 0; i < methods.length; i++) {
            Class[] exceptions = methods[i].getExceptionTypes();
            if (exceptions.length == 0) {
                return false;
            }
            for (int j = 0; j < exceptions.length; j++) {
                if (exceptions[j].getName().contains("RMIException")) {

                    break;
                } else if (j == exceptions.length - 1) {
                    return false;
                }
            }
        }
        return true;
    }


}
