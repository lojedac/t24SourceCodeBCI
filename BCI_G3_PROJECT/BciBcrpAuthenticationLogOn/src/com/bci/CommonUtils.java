package com.bci;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * TODO: Document me!
 *
 * @author David Barahona
 * @mail david.barahona@nagarro.com
 */

public class CommonUtils {
    public static Dictionary<String, String> splitData(String response) {
        Dictionary<String, String> dic = new Hashtable<String, String>();
        String[] parts = response.split("&");
        for (String string : parts) {
            String[] datos = string.split("=");
            if (datos.length == 1) {
                dic.put(datos[0], "None");
            } else {
                try {
                    dic.put(datos[0], datos[1]);
                } catch (Exception e) {
                }
            }
        }
        return dic;
    }

    public static String joinData(LinkedHashMap<String, String> dic) {
        String request = "";
        String key = "";
        String value = "";
        Set<String> keys = dic.keySet();
        Iterator<String> ekey = keys.iterator();
        while (ekey.hasNext()) {
            key = ekey.next();
            value = dic.get(key);
            request += key + "=" + value + "&";
        }
        return request;
    }

}
