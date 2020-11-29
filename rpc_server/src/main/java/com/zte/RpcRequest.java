package com.zte;

import java.io.Serializable;
import java.lang.reflect.Method;

public class RpcRequest implements Serializable {
    private static final long serialVersionUID = -5854150492574586489L;
    private String className;
    private Object[] args;
    private String methodName;
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
