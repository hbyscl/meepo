package org.cheng.meepo.task.executor.dubbo;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengLi on 2016/9/5.
 * 读取本地XML配置获取发布的Dubbo服务列表
 */
public class FoundProviderXMLService implements IFoundProviderService {

    private List<File> xmlFileList = new ArrayList<>();

    @Override
    public List<String> list() {
        return addXmlFile2List(new File(loadClassPath())).loadDubboService(xmlFileList);
    }

    /**
     * 根据XML文件列表过滤出含有<dubbo:service>标签中的interface元素，即为发布的Dubbo服务
     *
     * @param xmlFileList CLASSPATH中的XML文件列表
     * @return 发布的Dubbo服务列表
     */
    private List<String> loadDubboService(List<File> xmlFileList) {
        List<String> dubboServiceList = new ArrayList<>();
        for (File file : xmlFileList) {
            SAXReader saxReader = new SAXReader();
            try {
                Document document = saxReader.read(file);
                Element rootElement = document.getRootElement();
                if (rootElement.getName().equals("beans")) {
                    List<Element> elements = rootElement.elements("service");
                    for (Element dubboNode : elements) {
                        if ("dubbo".equals(dubboNode.getNamespace().getPrefix())) {
                            dubboServiceList.add(dubboNode.attributeValue("interface"));
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dubboServiceList;
    }

    /**
     * 递归过滤以获取XML文件
     *
     * @param dir CLASSPATH中的目录
     * @return this, 适应流式编程风格
     */
    private FoundProviderXMLService addXmlFile2List(File dir) {
        if (dir.exists()) {
            dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (file.isDirectory() && !file.getName().contains("com")) {
                        addXmlFile2List(file);
                    } else if (file.getName().endsWith(".xml")) {
                        xmlFileList.add(file);
                    }
                    return false;
                }

            });
        }
        return this;
    }

    /**
     * 获取CLASSPATH目录
     *
     * @return
     */
    private String loadClassPath() {
        URL resource = FoundProviderXMLService.class.getResource("/");
        return resource.getPath();
    }
}
