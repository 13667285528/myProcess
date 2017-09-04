package com.zhou.myProcess.xmlParser;

import com.zhou.myProcess.exception.IllegalityParamException;
import com.zhou.myProcess.exception.IllegalityResultException;
import com.zhou.myProcess.process.SqlMapper;
import com.zhou.myProcess.process.SqlSescribe;
import com.zhou.myProcess.util.Util;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过解析xml文件来解析sql描述
 */
public class SqlXmlReader extends XmlReader{

    public SqlXmlReader(String xmlPath) throws DocumentException {
        super(xmlPath);
    }

    public String getPackage(){
        return root.attributeValue("package");
    }

    /**
     * 读取sql信息
     * @throws Exception
     */
    public SqlMapper readSqlXml() throws Exception{
        SqlMapper sqlMapper = new SqlMapper();
        Map<String, SqlSescribe> sqlSescribeMap = new HashMap<String, SqlSescribe>();
        sqlMapper.setMap(sqlSescribeMap);
        sqlMapper.setInstance(Class.forName(root.attributeValue("instance")));
        this.root = document.getRootElement();
        List<Element> updates = root.elements("update");
        for(Element sql : updates){
            SqlSescribe sqlSescribe = new SqlSescribe();
            sqlSescribe.setSql(sql.getText());
            sqlSescribe.setType("update");
            sqlSescribe.setId(sql.attributeValue("id"));
            if(Util.isNotEmpty(sql.attributeValue("paramType"))) {
                sqlSescribe.setParamType(Class.forName(sql.attributeValue("paramType")));
                checkParamAndResult(sqlMapper.getInstance().getMethod(sqlSescribe.getId(), sqlSescribe.getParamType()), sqlSescribe);
            }else{
                checkParamAndResult(sqlMapper.getInstance().getMethod(sqlSescribe.getId()), sqlSescribe);
            }
            sqlSescribeMap.put(sql.attributeValue("id"), sqlSescribe);
        }
        List<Element> selects = root.elements("select");
        for(Element sql : selects){
            SqlSescribe sqlSescribe = new SqlSescribe();
            sqlSescribe.setSql(sql.getText());
            sqlSescribe.setType("select");
            sqlSescribe.setId(sql.attributeValue("id"));
            if(Util.isNotEmpty(sql.attributeValue("resultType")))
                sqlSescribe.setResultType(Class.forName(sql.attributeValue("resultType")));
            if(Util.isNotEmpty(sql.attributeValue("paramType"))){
                sqlSescribe.setParamType(Class.forName(sql.attributeValue("paramType")));
                checkParamAndResult(sqlMapper.getInstance().getMethod(sqlSescribe.getId(), sqlSescribe.getParamType()), sqlSescribe);
            }else{
                checkParamAndResult(sqlMapper.getInstance().getMethod(sqlSescribe.getId()), sqlSescribe);
            }
            sqlSescribeMap.put(sql.attributeValue("id"), sqlSescribe);
        }
        return sqlMapper;
    }

    /**
     * 验证 这个方法的传入参数与返回类型是否符合Sql描述
     * @param method
     * @param sqlSescribe
     */
    private static void checkParamAndResult(Method method, SqlSescribe sqlSescribe) throws IllegalityParamException, IllegalityResultException {
        if(method.getParameterTypes().length > 1 )
            throw new IllegalityParamException("方法"+method.getDeclaringClass().getName()+"."+method.getName()+"中最多只可以传入一个参数");
        if(method.getParameterTypes().length > 0 && !method.getParameterTypes()[0].equals(sqlSescribe.getParamType()))
            throw new IllegalityParamException("方法"+method.getDeclaringClass().getName()+"."+method.getName()+"与paramType："+sqlSescribe.getParamType()+"不符");
        if(sqlSescribe.getType().equals("select")){
            if(method.getReturnType().isAssignableFrom(List.class)){
                Type returnType = method.getGenericReturnType();
                if(returnType instanceof ParameterizedType){
                    Type[] typesto = ((ParameterizedType) returnType).getActualTypeArguments();// 强制转型为带参数的泛型类型，
                    if(typesto.length > 0 && !typesto[0].equals(sqlSescribe.getResultType()))
                        throw new IllegalityResultException("方法"+method.getDeclaringClass().getName()+"."+method.getName()+"具有错误的返回类型："+typesto[0].getTypeName());
                }
            }else{
                if(sqlSescribe.getResultType() != null && !sqlSescribe.getResultType().equals(method.getReturnType()))
                    throw new IllegalityResultException("方法"+method.getDeclaringClass().getName()+"."+method.getName()+"没有使用指定的返回值类型");
            }
        }
    }
}
