package org.cheng.meepo.task.executor.dubbo;

import org.cheng.meepo.task.util.PropertiesParse;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ChengLi on 2016/9/1.
 * 通过Zookeeper中的Dubbo节点，获取本机发布的Dubbo服务方法
 */
public class FoundProviderZKService implements IFoundProviderService {

    @Override
    public List<String> list() {
        try {
            System.out.println("FoundProviderService.list获取本地Dubbo服务列表");
            List<String> localIPList = getLocalIPList();
            final CountDownLatch connectionLatch = new CountDownLatch(1);
            ZooKeeper zk = new ZooKeeper(PropertiesParse.getProperty("dubbo.registry.address"), 60000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        connectionLatch.countDown();
                        System.out.println("连接Zookeeper成功");
                    } else if (event.getState() == Event.KeeperState.Expired) {
                        System.out.println("会话超时，连接Zookeeper失败");
                    }
                }
            });
            connectionLatch.await();
            List<String> dubbo = zk.getChildren("/dubbo", true);
            String regex = "dubbo://(.*?):";
            Pattern p = Pattern.compile(regex);
            List<String> providerServiceList = new ArrayList<>();
            for (String servicePath : dubbo) {
                List<String> providersPath = zk.getChildren("/dubbo/" + servicePath + "/providers", true);
                for (String provider : providersPath) {
                    String url = URLDecoder.decode(provider, "utf-8");
                    Matcher m = p.matcher(url);
                    while (m.find()) {
                        if (localIPList.contains(m.group(1))) {
                            providerServiceList.add(servicePath);
                        }
                    }
                }
            }
            zk.close();
            return providerServiceList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 获取本机所有网卡的IPV4地址
     *
     * @return IPV4列表
     * @throws SocketException
     */
    private List<String> getLocalIPList() throws SocketException {
        List<String> ipList = new ArrayList<>();
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        while (e.hasMoreElements()) {
            NetworkInterface networkInterface = e.nextElement();
            Enumeration<InetAddress> iparray = networkInterface.getInetAddresses();
            while (iparray.hasMoreElements()) {
                InetAddress ip = iparray.nextElement();
                String hostAddress = ip.getHostAddress();
                // 忽略本地地址及IPV6地址
                if (!Objects.equals(hostAddress, "127.0.0.1") && !hostAddress.contains(":")) {
                    ipList.add(hostAddress);
                }
            }
        }
        return ipList;
    }
}
