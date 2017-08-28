package org.cheng.meepo.task.executor.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created with Intellij IDEA 14.
 * User: ChengLi
 * Date: 2016-03
 * 泛化调用Dubbo接口
 */
public class GenericInvoke {

    private static Logger log = Logger.getLogger(GenericInvoke.class);

    private static GenericInvoke instance = null;

    private Boolean initialize = false;

    private ApplicationConfig application = null;

    private RegistryConfig registry = null;

    private ConcurrentMap<String, GenericService> serviceMap = new ConcurrentHashMap<>();

    private GenericInvoke() {
    }

    private void init() {
        if (initialize)
            return;
        initialize = true;
        // 当前应用配置
        application = new ApplicationConfig();
        registry = new RegistryConfig();
        registry.setProtocol("zookeeper");
    }

    public static GenericInvoke getInstance() {
        if (null == instance) {
            synchronized (GenericInvoke.class) {
                instance = new GenericInvoke();
                instance.init();
            }
        }
        return instance;
    }

    private GenericService getService(String interfaceName) {
        GenericService genericService = serviceMap.get(interfaceName);
        if (null != genericService) {
            return genericService;
        }
        // 引用远程服务
        // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        try {
            // 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接
            ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
            reference.setApplication(application);
            reference.setRegistry(registry); // 多个注册中心可以用setRegistries()
            reference.setInterface(interfaceName);
            reference.setGeneric(true);
            reference.setCheck(false);
            reference.setAsync(true);
            genericService = reference.get();
            serviceMap.put(interfaceName, genericService);
        } catch (Exception e) {
            log.error("GET dubbo reference has error of Interface: " + interfaceName);
        }
        return genericService;
    }

    /**
     * 调用指定Dubbo接口
     *
     * @param interfaceName 接口名称
     * @param method        方法
     * @param paramsType    参数类型列表
     * @param params        参数列表
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public Object invoke(String interfaceName, String method, String[] paramsType, Object[] params)
            throws InterruptedException, ExecutionException, TimeoutException {
        GenericService service = GenericInvoke.getInstance().getService(interfaceName);
        return service.$invoke(method, paramsType, params);
    }


}
