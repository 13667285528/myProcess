package com.zhou.myProcess.process;

import com.zhou.myProcess.exception.SqlResultException;
import com.zhou.myProcess.util.Util;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class CheckTable {
    public static void checkTable(DataSource dataSource) throws Exception {
        Connection conn = null;
        try {
            Document document = new SAXReader()
                    .read(CheckTable.class.getClassLoader().getResourceAsStream("com/zhou/myProcess/sql/CheckTable.xml"));
            Element root = document.getRootElement();
            List<Element> checkSqls = root.elements("checkSql");
            conn = dataSource.getConnection();
            conn.setAutoCommit(true);
            String database = getDataBase(conn);
            for (Element checkSql : checkSqls) {
                checkOneTable(checkSql.attributeValue("table"), conn, checkSql.getText(), database);
            }
        }finally {
            if(conn != null)
                conn.close();
        }
    }

    /**
     * 获取当前所连接的数据库的名称
     */
    private static String getDataBase(Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            String sqlStr = "select database();";
            ps = conn.prepareStatement(sqlStr);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }else{
                throw new SQLException("无法获取当前连接的数据库");
            }
        } finally {
            if (ps != null)
                ps.close();
            if (resultSet != null)
                resultSet.close();
        }
    }

    /**
     * 验证某个表示否是存在的,如果这个表不存在则执行这张表的创建sql
     * @param tableName
     * @throws Exception
     */
    private static void checkOneTable(String tableName, Connection conn, String tableSql, String dataBase) throws Exception{
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try{
            String sqlStr = "select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_NAME='"
                    +tableName+"' AND TABLE_SCHEMA = '"+dataBase+"'";
            ps = conn.prepareStatement(sqlStr);
            resultSet = ps.executeQuery();
            if(!resultSet.next()){
                ps.executeUpdate(tableSql);
            }
        }finally{
            if(ps != null)
                ps.close();
            if(resultSet != null)
                resultSet.close();
        }
    }
}
