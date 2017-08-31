package com.zhou.myProcess.xmlParser;

import com.zhou.myProcess.process.SqlMapper;
import com.zhou.myProcess.process.SqlSescribe;
import com.zhou.myProcess.util.Util;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

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
            if(Util.isNotEmpty(sql.attributeValue("paramType")))
                sqlSescribe.setParamType(Class.forName(sql.attributeValue("paramType")));
            sqlSescribeMap.put(sql.attributeValue("id"), sqlSescribe);
        }
        List<Element> selects = root.elements("select");
        for(Element sql : selects){
            SqlSescribe sqlSescribe = new SqlSescribe();
            sqlSescribe.setSql(sql.getText());
            sqlSescribe.setType("select");
            sqlSescribe.setId(sql.attributeValue("id"));
            if(Util.isNotEmpty(sql.attributeValue("paramType")))
                sqlSescribe.setParamType(Class.forName(sql.attributeValue("paramType")));
            if(Util.isNotEmpty(sql.attributeValue("resultType")))
                sqlSescribe.setResultType(Class.forName(sql.attributeValue("resultType")));
            sqlSescribeMap.put(sql.attributeValue("id"), sqlSescribe);
        }
        return sqlMapper;
    }
}
