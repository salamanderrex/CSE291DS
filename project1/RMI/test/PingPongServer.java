package test;

import rmi.RMIException;

/**
 * Created by qingyu on 4/16/16.
 */
public interface PingPongServer {
    public String ping(int id) throws RMIException;

}
