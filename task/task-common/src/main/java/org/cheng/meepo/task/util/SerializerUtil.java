package org.cheng.meepo.task.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * Created by ChengLi on 2016/6/19.
 */
public class SerializerUtil {
    private static Logger log = LoggerFactory.getLogger(SerializerUtil.class);

    public static Object byte2object(byte[] bytes) {
        Object obj = null;
        try {
            // bytearray to object
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);

            obj = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            log.error("translation",e);
        }
        return obj;
    }

    public static byte[] object2byte(java.lang.Object obj) {
        byte[] bytes = null;
        try {
            // object to bytearray
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);

            bytes = bo.toByteArray();

            bo.close();
            oo.close();
        } catch (Exception e) {
            log.error("translation",e);
        }
        return bytes;
    }

    public static String object2base64(Object obj){
        return new BASE64Encoder().encode(object2byte(obj));
    }

    public static Object base642object(String base64){
        try {
            return byte2object(new BASE64Decoder().decodeBuffer(base64));
        } catch (IOException e) {
            log.error("translation",e);
        }
        return null;
    }

}
