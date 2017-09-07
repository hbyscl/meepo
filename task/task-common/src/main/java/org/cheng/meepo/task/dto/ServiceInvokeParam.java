package org.cheng.meepo.task.dto;

import java.io.Serializable;

/**
 * Created by ChengLi on 2016/6/17.
 */
public class ServiceInvokeParam implements Serializable {
    private String interfaceName;
    private String methodName;
    private String version;
    private String[] paramsType;
    private Object[] params;

    private ServiceInvokeParam nextService;

    public ServiceInvokeParam(String interfaceName, String methodName, String[] paramsType, Object[] params) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.paramsType = paramsType;
        this.params = params;
    }

    public ServiceInvokeParam() {
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String[] getParamsType() {
        return paramsType;
    }

    public void setParamsType(String[] paramsType) {
        this.paramsType = paramsType;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public ServiceInvokeParam setNextService(ServiceInvokeParam serviceInvokeParam){
        this.nextService = serviceInvokeParam;
        return serviceInvokeParam;
    }

    public void addNextService(ServiceInvokeParam serviceInvokeParam){
        if(null == this.nextService){
            this.nextService = serviceInvokeParam;
        }
        else{
            this.nextService.addNextService(serviceInvokeParam);
        }

    }

    public ServiceInvokeParam getNextService() {
        return nextService;
    }
}
