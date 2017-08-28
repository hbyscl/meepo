package org.cheng.meepo.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesParse extends PropertyPlaceholderConfigurer {

    private static Map<String, String> configMap;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory,
                                     Properties props) throws BeansException {

        super.processProperties(beanFactory, props);
        configMap = new HashMap<>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            configMap.put(keyStr, value);
        }
    }

    /**
     * 获取key对应的值
     */
    public static String getProperty(String key) {
        return configMap != null ? configMap.get(key) : null;
    }

    /**
     * 获取key对应的值，如果value为空，则返传入的回默认值
     */
    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        if (null != value && !"".equals(value)) {
            return value;
        } else {
            return defaultValue;
        }
    }
} 