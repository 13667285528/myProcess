package com.zhou.myProcess.xmlParser;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * xml解析类的基类
 */
public abstract class XmlReader {

    /**
     * 从读取出的xml文件中所进行构建的dom4j对象
     */
    protected Document document;

    /**
     * 根节点对象
     */
    protected Element root;

    /**
     * 构造器中直接进行xml文件的读取
     * @param xmlPath 文件路径
     */
    public XmlReader(String xmlPath) throws DocumentException {
        Document document = new SAXReader()
                .read(XmlReader.class.getClassLoader().getResourceAsStream(xmlPath));
        this.document = document;
        this.root = document.getRootElement();
    }

}
