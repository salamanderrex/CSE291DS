package rmi;

import java.io.Serializable;
import java.lang.reflect.*;


public class requestObject implements Serializable {


    private Method method;
    private Object[] args;

    public requestObject(Method method, Object[] args) {
        this.method = method;
        this.args = args;
    }

    public String getName() {
        return method.getName();
    }

    public Object getParameterTypes() {
        return method.getParameterTypes();
    }

    public String getReturnType() {
        return method.getReturnType().getTypeName();
    }

    public Object[] getArgs() {
        return args;
    }
}
