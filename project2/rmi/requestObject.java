package rmi;

import java.io.Serializable;
import java.lang.reflect.*;


public class requestObject implements Serializable {



    public Object[] args;
    public  String MethodName;
    public  Class <?> [] ParameterTypes;
    public  Class <?> ReturnTypes;


    public requestObject(Method method, Object[] args) {
        this.MethodName = method.getName();
        this.ParameterTypes =   method.getParameterTypes();
        this.ReturnTypes =  method.getReturnType();
        this.args = args;
    }


}
