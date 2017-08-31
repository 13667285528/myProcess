package com.zhou.myProcess.util;

import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Util {

    public static boolean isEmpty(Object obj) {
        if (obj instanceof String) {
            if (obj == null || ((String) obj).trim().length() == 0)
                return true;
        } else if (obj instanceof Map) {
            if (obj == null || ((Map) obj).size() == 0)
                return true;
        } else if (obj instanceof Collection) {
            if (obj == null || ((Collection) obj).size() == 0)
                return true;
        } else if (obj != null && obj.getClass().isArray()) {
            if (obj == null || ((Object[]) obj).length == 0)
                return true;
        } else {
            if (obj == null)
                return true;
            else
                return false;
        }
        return false;
    }

    public static boolean isNotEmpty(Object obj){
        return !isEmpty(obj);
    }

    /**
     * 进行包扫描，获取一个指定的包中的Sql.xml文件
     * @param packagePath
     * @return
     */
    public static List<String> getXmlPathFromPackage(String packagePath, String endWith) throws IOException, DocumentException {
        List<String> xmlPaths = new LinkedList<String>();
        String packageDirName = packagePath.replace('.', '/');

        String thisPath = Util.class.getResource("").getFile();
        String filePath = thisPath.substring(0,thisPath.length() - new String("com/zhou/myProcess/util/").length())+packageDirName;

        File file = new File(filePath);
        if(file.exists() && file.isDirectory()){
            for(File xml:file.listFiles()){
                if(xml.getName().endsWith(endWith)){
                    xmlPaths.add(packageDirName+"/"+xml.getName());
                }
            }
        }
        return xmlPaths;
    }

    /**
     * 调用一个对象的get方法
     */
    public static Object invokeGet(Object obj, String paramName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = null;
        paramName = "get" + paramName.substring(0,1).toUpperCase() + paramName.substring(1);
    	method = obj.getClass().getMethod(paramName);
        return method.invoke(obj);
    }

    public static void invokeSet(Object obj, String paramName, Object arg) throws InvocationTargetException, IllegalAccessException {
        Method method = null;
        try {
            if(arg == null){
                return ;
            }
            paramName = "set" + paramName.substring(0,1).toUpperCase() + paramName.substring(1);
            method = obj.getClass().getMethod(paramName,arg.getClass());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("相应的参数："+paramName);
        }
        method.invoke(obj, arg);
    }
}
